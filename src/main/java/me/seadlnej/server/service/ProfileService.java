package me.seadlnej.server.service;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.model.User;
import me.seadlnej.server.model.UserProfile;
import me.seadlnej.server.repository.user.ContactRepository;
import me.seadlnej.server.repository.user.ProfileRepository;
import me.seadlnej.server.repository.user.SessionRepository;
import me.seadlnej.server.repository.user.UserRepository;
import me.seadlnej.server.requests.profile.UpdateProfileRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository repository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private final ContactRepository contactRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public ProfileService(ProfileRepository repository, UserRepository userRepository,
                          SessionRepository sessionRepository, SimpMessagingTemplate messagingTemplate,
                          ContactRepository contactRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.messagingTemplate = messagingTemplate;
        this.contactRepository = contactRepository;
    }

    // Getting primary information about user
    public HashMap<String, Object> getBasic(String username) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Find user by username
        User user = userRepository.findByUsername(username);
        if(user == null) {  response.put("message", "User no found.");
            return response;
        }

        // Putting data into hashmap
        response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname());
        response.put("username", user.getUsername());

        // Returning
        response.put("success", true);
        return response;
    }

    // Getting extended more detailed profile information
    public HashMap<String, Object> getExtended(String username, Long userId) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        // Find user by id
        User user = userRepository.findByUsername(username);
        if(user == null) {
            response.put("message", "No user found.");
            return response;
        }

        // Creating profile if user does not exist
        if(repository.findById(user.getId()).isEmpty()) {
            defaultProfile(user.getId());
        }

        // Find profile by id
        Optional<UserProfile> profileOpt = repository.findById(user.getId());
        if(profileOpt.isEmpty()) {
            response.put("message", "Profile still not found.");
            return response;
        }
        UserProfile profile = profileOpt.get();

        // Putting data into hashmap
        response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());

        // Decoding image
        byte[] imageBytes = Optional.ofNullable(profile.getImage()).orElse(new byte[0]);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        response.put("image", base64Image);

        response.put("status", profile.getStatus().toString());
        response.put("address", profile.getAddress() != null ? profile.getAddress() : "");
        response.put("birthdate", profile.getBirthdate() != null ? String.valueOf(profile.getBirthdate()) : "");
        response.put("bio", profile.getBio() != null ? profile.getBio() : "");

        // ------------------- Friend / request status -------------------
        boolean isFriend = false;
        boolean requestPending = false;

        // Check friendship (replace with actual repository logic)
        if(contactRepository.areFriends(userId, user.getId())) {
            isFriend = true;
        } else if(contactRepository.requestExists(userId, user.getId())) {
            requestPending = true;
        }

        response.put("isFriend", isFriend);
        response.put("requestPending", requestPending);
        response.put("canSendRequest", !isFriend && !requestPending);

        // ---------------------------------------------------------------

        response.put("success", true);
        return response;
    }










    @PostMapping("/show")
    public HashMap<String, Object> setStatus(@RequestBody(required = false) Map<String, String> request) {

        String username = request.get("username");

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // If request is null
        if(username.isBlank()) { response.put("message", "Not all data were sent.");
            return response;
        }

        // Returning
        return response;

    }

    public void defaultProfile(Long id) {

        if(userRepository.findById(id).isEmpty()) {
            return;
        }

        User user = userRepository.findById(id).get();
        UserProfile profile = new UserProfile();

        profile.setUser(user);
        profile.setStatus(UserProfile.Status.Online);

        repository.save(profile);
    }


    public ApiResponse setStatus(Long userId, UserProfile.Status status) {

        ApiResponse response = new ApiResponse();

        UserProfile profile = repository.findByUserId(userId);
        if (profile == null) {
            defaultProfile(userId);
            profile = repository.findByUserId(userId);
        }

        profile.setStatus(status);
        repository.save(profile);

        messagingTemplate.convertAndSend("/topic/general",true);

        return response.setSuccess(true);
    }

    public HashMap<String, Object> update(UpdateProfileRequest request, Long id) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking is user exists
        Optional<User> userOpt = userRepository.findById(id);
        if(userOpt.isEmpty()) {
            response.put("message", "There no such user.");
            return response;
        }

        // Getting user
        User user = userOpt.get();

        // Checking is profile exists, if not = create
        UserProfile profile = repository.findByUserId(id);
        if(profile == null) { defaultProfile(user.getId()); }
        profile = repository.findByUserId(id);

        // Image validation + save
        String base64Image = request.getImage();
        if (base64Image != null && !base64Image.isBlank()) {
            try {
                if (base64Image.contains(",")) {
                    base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                }

                byte[] decoded = Base64.getDecoder().decode(base64Image);
                profile.setImage(decoded);
                response.put("image-success", true);

            } catch (IllegalArgumentException ex) {
                response.put("image-message", "Invalid Base64 image.");
            }
        }

        // Address validation
        String address = request.getAddress();
        if(address != null && !address.isBlank()) {
            if(!address.matches("[a-zA-Z0-9\\-\\s,./]+") || address.length() > 64) {
                response.put("address-message", "Address can contain only letters, " +
                        "numbers, spaces, - , . / and max 64 characters.");
            } else {
                profile.setAddress(address);
                response.put("address-success", true);
            }
        }

        // Birthdate validation
        String birthdate = request.getBirthdate();
        if(birthdate != null && !birthdate.isBlank()) {
            try {
                LocalDate parsed = LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE);
                profile.setBirthdate(parsed);
                response.put("birthdate-success", true);
            } catch(DateTimeParseException e) {
                response.put("birthdate-message", "Birthdate must be in the format YYYY-MM-DD.");
            }
        }

        // Bio validation
        String bio = request.getBio();
        if(bio != null) {
            if(bio.length() > 64) {
                response.put("bio-message", "Bio must be maximum 64 characters.");
            } else {
                profile.setBio(bio);
                response.put("bio-success", true);
            }
        }

        repository.save(profile);
        response.put("success", true);
        return response;

    }

}