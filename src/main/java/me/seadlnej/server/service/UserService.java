package me.seadlnej.server.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import me.seadlnej.server.configuration.PasswordEncoder;
import me.seadlnej.server.model.User;
import me.seadlnej.server.model.UserProfile;
import me.seadlnej.server.repository.user.ProfileRepository;
import me.seadlnej.server.repository.user.SessionRepository;
import me.seadlnej.server.repository.user.UserRepository;
import me.seadlnej.server.repository.user.ResetRepository;
import me.seadlnej.server.requests.LoginRequest;
import me.seadlnej.server.requests.RegisterRequest;
import me.seadlnej.server.requests.user.PasswordResetRequest;
import me.seadlnej.server.requests.user.RegisterCompleteRequest;
import me.seadlnej.server.requests.user.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private JavaMailSender mailSender;

    private final UserRepository repository;

    // Services
    private final MailService mailService;

    // Repository
    private final ResetRepository resetRepository;
    private final SessionRepository sessionRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    // Registration and Password reset pending
    private final Map<String, RegistrationCode> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String, PassResetCode> pendingResets = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    // Regexes
    private final String USER_REGEX = "^[A-Za-z0-9_]{3,16}$";
    private final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final String PHONE_REGEX = "^\\+?[0-9]{7,15}$";
    private final String PASS_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()/_\\-+=.]).{8,32}$";

    // Constructor
    public UserService(UserRepository repository, MailService mailService,
                       SessionRepository sessionRepository, ResetRepository resetRepository, ProfileRepository profileRepository, ProfileService profileService,
                       SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.mailService = mailService;
        this.sessionRepository = sessionRepository;
        this.resetRepository = resetRepository;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
        this.messagingTemplate = messagingTemplate;
    }

    public HashMap<String, Object> getUserByToken(String token) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        if (token == null || token.isBlank()) {
            response.put("message", "Token not provided.");
            return response;
        }

        // Validate session token
        var session = sessionRepository.findByToken(token);
        if (session == null) {
            response.put("message", "Invalid or expired token.");
            return response;
        }

        // Get user by session's userId
        Optional<User> userOpt = repository.findById(session.getUserId());
        if (userOpt.isEmpty()) {
            response.put("message", "User not found.");
            return response;
        }

        User user = userOpt.get();

        UserProfile profile = profileRepository.findByUserId(user.getId());
        if(profile == null) {
            profileService.defaultProfile(user.getId());
            profile = profileRepository.findByUserId(user.getId());
        }

        response.put("success", true);
        response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname());
        response.put("username", user.getUsername());
        response.put("image", profile.getImage());

        return response;
    }

    // --------------------------------------------------
    // | Login section                                  |
    // --------------------------------------------------

    // Login process functions
    public HashMap<String, Object> login(LoginRequest request, HttpServletRequest httpRequest) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(request == null || request.isEmpty()) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Applying login be username or email
        User user;
        String identifier = request.getUsername();

        if (identifier.contains("@")) {
            user = repository.findByEmail(identifier);
        } else {
            user = repository.findByUsername(identifier);
        }

        // Return if user does not exist
        if (user == null) {
            response.put("message", "Invalid username/email or password.");
            return response;
        }

        // Does password match?
        if(!PasswordEncoder.matches(request.getPassword(), user.getPasshash())) {
            response.put("message", "Invalid username/email or password.");
            return response;
        }

        // Sending login/security alert email
        try {
            mailService.sendHtmlMail(user.getEmail(),
                    "Stu:dyHub - Security Alert",
                    "<div style=\"max-width:480px;margin:auto;padding:20px;font-family:Arial;border-radius:10px;border:1px solid #e5e7eb\">" +
                            "<h2 style=\"color:#d75c5c;text-align:center\">Security Alert</h2>" +
                            "<p>Hello " + user.getFirstname() + ",</p>" +
                            "<p>We noticed a login to your account:</p>" +
                            "<ul style=\"list-style:none;padding-left:0\">" +
                            "<li><strong>Time:</strong> " + java.time.LocalDateTime.now() + "</li>" +
                            "<li><strong>IP Address:</strong> " + httpRequest.getRemoteAddr() + "</li>" +
                            "<li><strong>Device/Browser:</strong> " + httpRequest.getHeader("User-Agent") + "</li>" +
                            "</ul>" +
                            "<p>If this was you, no action is required.</p>" +
                            "<p>If you did not login, please reset your password immediately and review your account activity.</p>" +
                            "<hr style=\"border:none;border-top:1px solid #e5e7eb;margin-top:20px\"/>" +
                            "<p style=\"font-size:12px;color:#6b7280;text-align:center\">© 2025 Stu:dyHub – All rights reserved</p>" +
                            "</div>"
            );
        } catch (MessagingException e) {
            response.put("message", "Failed to send security alert email.");
            return response;
        }

        // Returning response
        response.put("success", true);
        response.put("user", user.getId()); response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname()); response.put("username", user.getUsername());
        return response;
    }

    // --------------------------------------------------
    // | Registration section                           |
    // --------------------------------------------------

    // Registration process functions
    public HashMap<String, Object> register(RegisterRequest request) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(request == null || request.isEmpty()) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking if received data matches requirements
        Map<String, String> userFields = Map.of(
                "Firstname", request.getFirstname(),
                "Lastname", request.getLastname(),
                "Username", request.getUsername()
        );

        for (Map.Entry<String, String> entry : userFields.entrySet()) {
            if (!entry.getValue().matches(USER_REGEX)) {
                response.put("message", entry.getKey()  + "may contain only A-Z a-z 0-9 _ and must be 3–16 characters.");
                return response;
            }
        }

        if(!request.getEmail().matches(EMAIL_REGEX)) {
            response.put("message", "Invalid email format.");
            return response;
        }

        if(!request.getPhone().matches(PHONE_REGEX)) {
            response.put("message", "Invalid phone number format.");
            return response;
        }

        if(!request.getPassword1().equals(request.getPassword2())) {
            response.put("message", "Passwords do not match.");
            return response;
        }

        if(!request.getPassword1().matches(PASS_REGEX)) {
            response.put("message", "Password must be 8-32 chars, include letters, numbers and optionally !@#$%^&*()/_+-=");
            return response;
        }

        // User exists by username/email/phone
        Map<String, Boolean> checks = Map.of(
                "username", repository.existsByUsername(request.getUsername()),
                "email", repository.existsByEmail(request.getEmail()),
                "phone", repository.existsByPhone(request.getPhone())
        );

        for (Map.Entry<String, Boolean> entry : checks.entrySet()) {
            if (entry.getValue()) {
                response.put("message", "There's already a user with this " + entry.getKey() + ".");
                return response;
            }
        }

        // Generation registration token code
        SecureRandom random = new SecureRandom();
        int codeInt = 100000 + random.nextInt(900000);
        String codeRandom = String.valueOf(codeInt); // Random registration key
        String token = UUID.randomUUID().toString(); // Token of registration code

        // Creating registration code object
        long now = System.currentTimeMillis(); // Actual time
        RegistrationCode pending = new RegistrationCode(request, codeRandom, now,
                10 * 60 * 1000, 100 * 60 * 1000);

        // Adding code to list
        pendingRequests.put(token, pending);

        // Send confirmation email
        try {
            mailService.sendHtmlMail(request.getEmail(),
                    "Stu:dyHub",
                    "<div style=\"max-width:480px;margin:auto;padding:20px;font-family:Arial;border-radius:10px;border:1px solid #e5e7eb\">" + "<h2 style=\"color:#5c8bd7;text-align:center\">Email Verification</h2>" +
                            "<p>Hello,</p>" +
                            "<p>Thank you for registering with our application.</p>" +
                            "<p>Your verification code is:</p>" +
                            "<div style=\"text-align:center;font-size:32px;font-weight:bold;padding:10px 0;color:#333\">" +
                            codeRandom +
                            "</div>" +
                            "<p>This code is valid for 10 minutes.</p>" +
                            "<p>If you did not request this, ignore this email.</p>" +
                            "<hr style=\"border:none;border-top:1px solid #e5e7eb;margin-top:20px\"/>" +
                            "<p style=\"font-size:12px;color:#6b7280;text-align:center\">© 2025 Stu:dyHub – All rights reserved</p>" +
                            "</div>"
            );
        } catch (MessagingException e) {
            response.put("message", "Failed to send verification email.");
            return response;
        }

        // Returning response
        response.put("token", token);
        response.put("success", true);
        return response;
    }

    // Resen registration token code
    public HashMap<String, Object> registerResend(String token) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(token == null || token.isBlank()) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Checks if token exists
        if (!pendingRequests.containsKey(token)) {
            response.put("message", "Invalid registration token.");
            return response;
        }

        // Getting code
        RegistrationCode code = pendingRequests.get(token);

        // Checking if resend is possible
        HashMap<String, Object> codeResponse = code.canResend();
        if(!(boolean) codeResponse.get("success")) {
            return codeResponse;
        }

        // Regenerating new code for registration
        SecureRandom random = new SecureRandom();
        int codeInt = 100000 + random.nextInt(900000);
        String codeRandom = String.valueOf(codeInt);
        code.setCode(codeRandom, System.currentTimeMillis());

        // Send code resend email
        try {
            mailService.sendHtmlMail(code.getRequest().getEmail(),
                    "Stu:dyHub - Verification Code",
                    "<div style=\"max-width:480px;margin:auto;padding:20px;font-family:Arial;border-radius:10px;border:1px solid #e5e7eb\">" +
                            "<h2 style=\"color:#5c8bd7;text-align:center\">Email Verification</h2>" +
                            "<p>Your new verification code is:</p>" +
                            "<div style=\"text-align:center;font-size:32px;font-weight:bold;padding:10px 0;color:#333\">" +
                            codeRandom +
                            "</div>" +
                            "<p>This code is valid for 10 minutes.</p>" +
                            "</div>"
            );
        } catch (MessagingException e) {
            response.put("message", "Failed to resend verification email.");
            return response;
        }

        // Returning response
        response.put("success", true);
        response.put("message", "We sent you a new verification code.");
        return response;
    }

    // Completing registration process
    public HashMap<String, Object> registerComplete(RegisterCompleteRequest request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(request == null || request.isEmpty()) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Checks if token exists
        if (!pendingRequests.containsKey(request.getToken())) {
            response.put("message", "Invalid or expired registration token.");
            return response;
        }
        RegistrationCode registration = pendingRequests.get(request.getToken());

        long now = System.currentTimeMillis();
        if(!registration.isActive(request.getCode())) {
            pendingRequests.remove(request.getToken());
            response.put("message", "Registration expired, please start again.");
            return response;
        }

        if(!registration.isValid(request.getCode())) {
            response.put("message", "Invalid or expired registration token.");
            return response;
        }

        // getting userRequest and saving it into repository
        RegisterRequest req = registration.getRequest();

        User user = new User();
        user.setFirstname(req.getFirstname());
        user.setLastname(req.getLastname());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasshash(PasswordEncoder.hashPassword(req.getPassword1()));

        repository.save(user); // Saving user to table;
        pendingRequests.remove(request.getToken()); // Removes registrationCode

        // Returning response
        response.put("success", true);
        response.put("user", repository.findByUsername(req.getUsername()).getId()); response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname()); response.put("username", user.getUsername());
        return response;
    }

    // --------------------------------------------------
    // | Reset password section                         |
    // --------------------------------------------------

    // Reset password proccess
    public HashMap<String, Object> passwordReset(String email) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(email == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Getting user
        User user = repository.findByEmail(email);
        if(user == null) {
            response.put("message", "There's no registered account under that email address.");
            return response;
        }

        // Generation reset token code
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // Random registration key
        String token = UUID.randomUUID().toString(); // Token of registration code

        // Creating reset code object
        long now = System.currentTimeMillis(); // Actual time
        PassResetCode pending = new PassResetCode(email, code, now,
                10 * 60 * 1000, 100 * 60 * 1000);

        // Adding code to list
        pendingResets.put(token, pending);

        // Send alert email
        try {
            mailService.sendHtmlMail(user.getEmail(),
                    "Stu:dyHub - Password Reset",
                    "<div style=\"max-width:480px;margin:auto;padding:20px;font-family:Arial;border-radius:10px;border:1px solid #e5e7eb\">" +
                            "<h2 style=\"color:#d75c5c;text-align:center\">Password Reset Request</h2>" +
                            "<p>Hello " + user.getFirstname() + ",</p>" +
                            "<p>We received a request to reset your password.</p>" +
                            "<p>Your password reset code is:</p>" +
                            "<div style=\"text-align:center;font-size:32px;font-weight:bold;padding:10px 0;color:#333\">" +
                            code +
                            "</div>" +
                            "<p>This code is valid for 10 minutes.</p>" +
                            "<p>If you did not request a password reset, please ignore this email.</p>" +
                            "<hr style=\"border:none;border-top:1px solid #e5e7eb;margin-top:20px\"/>" +
                            "<p style=\"font-size:12px;color:#6b7280;text-align:center\">© 2025 Stu:dyHub – All rights reserved</p>" +
                            "</div>"
            );
        } catch (MessagingException e) {
            response.put("message", "Failed to send password reset email.");
            return response;
        }

        // Returning
        response.put("success", true);
        response.put("token", token);
        return response;
    }

    // Reset password complete
    public HashMap<String, Object> passwordComplete(PasswordResetRequest request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(request == null || request.isEmpty()) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Checks if token exists
        if (!pendingResets.containsKey(request.getToken())) {
            response.put("message", "No active password reset request found.");
            return response;
        }

        // Getting reset code
        PassResetCode reset = pendingResets.get(request.getToken());

        long now = System.currentTimeMillis();
        if(!reset.isActive(request.getCode())) {
            pendingResets.remove(request.getToken());
            response.put("message", "Reset expired, please start again.");
            return response;
        }

        if(!reset.isValid(request.getCode())) {
            response.put("message", "Invalid or expired reset token.");
            return response;
        }

        // If code is having valid email
        if(!request.getEmail().equals(reset.getEmail())) {
            response.put("message", "Invalid or expired reset token.");
            return response;
        }

        // If passwords match
        if(!request.getPassword1().equals(request.getPassword2())) {
            response.put("message", "Passwords do not match.");
            return response;
        }

        if(!request.getPassword1().matches(PASS_REGEX)) {
            response.put("message", "Password must be 8-32 chars, include letters, numbers and optionally !@#$%^&*()/_+-=");
            return response;
        }

        User user = repository.findByEmail(request.getEmail());
        if (user == null) {
            response.put("message", "User with this email does not exist.");
            return response;
        }

        // Update user password
        user.setPasshash(PasswordEncoder.hashPassword(request.getPassword1()));
        repository.save(user);

        // Remove used reset token
        pendingResets.remove(request.getToken());

        // Send confirmation email
        try {
            mailService.sendHtmlMail(
                    user.getEmail(),
                    "Stu:dyHub - Password Changed",
                    "<div style=\"max-width:480px;margin:auto;padding:20px;font-family:Arial;border-radius:10px;border:1px solid #e5e7eb\">" +
                            "<h2 style=\"color:#5c8bd7;text-align:center\">Password Changed</h2>" +
                            "<p>Your password has been successfully updated.</p>" +
                            "<p>If you did not perform this action, please contact support immediately.</p>" +
                            "</div>"
            );
        } catch (MessagingException e) {
            response.put("message", "Failed to send password reset email.");
            return response;
        }

        // Returning
        response.put("success", true);
        return response;
    }

    // --------------------------------------------------
    // | Update user details section                    |
    // --------------------------------------------------

    public HashMap<String, Object> update(UpdateUserRequest request, Long id) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if (request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        Optional<User> userOpt = repository.findById(id);
        if (userOpt.isEmpty()) {
            response.put("message", "There is no such user.");
            return response;
        }
        User user = userOpt.get();
        String oldUsername = user.getUsername();

        Map<String, String> errors = new HashMap<>();

        // Regex validation
        if (!request.getFirstname().matches(USER_REGEX)) errors.put("firstname", "Invalid format for firstname.");
        if (!request.getLastname().matches(USER_REGEX)) errors.put("lastname", "Invalid format for lastname.");
        if (!request.getUsername().matches(USER_REGEX)) errors.put("username", "Invalid format for username.");
        if (!request.getEmail().matches(EMAIL_REGEX)) errors.put("email", "Invalid email format.");
        if (!request.getPhone().matches(PHONE_REGEX)) errors.put("phone", "Invalid phone number format.");

        // Existence check
        if (repository.existsByUsername(request.getUsername()) && !request.getUsername().equals(user.getUsername()))
            errors.put("username", "There's already a user with this username.");
        if (repository.existsByEmail(request.getEmail()) && !request.getEmail().equals(user.getEmail()))
            errors.put("email", "There's already a user with this email.");
        if (repository.existsByPhone(request.getPhone()) && !request.getPhone().equals(user.getPhone()))
            errors.put("phone", "There's already a user with this phone.");

        errors.forEach((field, msg) -> {
            response.put(field + "-message", msg);
        });

        if (!errors.containsKey("firstname")) {
            user.setFirstname(request.getFirstname());
            response.put("firstname-success", true);
        }
        if (!errors.containsKey("lastname")) {
            user.setLastname(request.getLastname());
            response.put("lastname-success", true);
        }
        if (!errors.containsKey("username")) {
            user.setUsername(request.getUsername());
            response.put("username-success", true);
        }
        if (!errors.containsKey("email")) {
            user.setEmail(request.getEmail());
            response.put("email-success", true);
        }
        if (!errors.containsKey("phone")) {
            user.setPhone(request.getPhone());
            response.put("phone-success", true);
        }

        repository.save(user);
        response.put("success", true);
        return response;
    }

}