package me.seadlnej.server.controller;

import me.seadlnej.server.model.User;
import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.repository.user.UserRepository;
import me.seadlnej.server.repository.user.SessionRepository;
import me.seadlnej.server.requests.profile.UpdateProfileRequest;
import me.seadlnej.server.requests.user.UpdateUserRequest;
import me.seadlnej.server.service.ProfileService;
import me.seadlnej.server.service.UserSessionService;
import me.seadlnej.server.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService service;

    // Services
    private final UserService userService;
    private final UserSessionService userSessionService;

    // Repositories
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private final SimpMessagingTemplate messagingTemplate;

    // Constructor
    public ProfileController(ProfileService service, UserService userService, UserSessionService userSessionService,
                             SessionRepository sessionRepository, SimpMessagingTemplate messagingTemplate,
                             UserRepository userRepository) {
        this.service = service;
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.sessionRepository = sessionRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    // Returning data of user's profile
    @PostMapping("/basic")
    public HashMap<String, Object> getBasic(@RequestBody(required = false) Map<String, String> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        String username = request.get("username");

        // If request is null
        if(username.isBlank()) { response.put("message", "Not all data were sent.");
            return response;
        }

        return service.getBasic(username);
    }

    // Returning data of user's profile
    @PostMapping("/extended")
    public HashMap<String, Object> getExtended(@RequestBody(required = false) Map<String, String> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        String username = request.get("username");
        String token = request.get("token");

        // If request is null
        if(username.isBlank()) { response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token.");
            return response;
        }


        return service.getExtended(username, session.getUserId());
    }

    // Returning profile configuration data
    @PostMapping("/status")
    public HashMap<String, Object> setStatus(@RequestBody ProfileStatusRequest request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // If request is null
        if(request == null) { response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(request.getToken());
        if(session == null) { response.put("message","Invalid session token.");
            return response;
        }

        /*
        UserProfile.Status newStatus;
        try {
            newStatus = UserProfile.Status.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            return new ApiResponse().setSuccess(false).setData("message", "Invalid status value.");
        }

        return service.setStatus(session.getUserId(), newStatus);

         */
        return response;
    }

    public static class ProfileStatusRequest {

        private String token;
        private String status;

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }

        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Updating user and profile information
    @PostMapping("/update")
    public HashMap<String, Object> update(@RequestBody ProfileUpdateRequest request) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response

        // If request is null
        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        UserSession session = userSessionService.validateToken(request.getToken());
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        Optional<User> userOpt = userRepository.findById(session.getUserId());
        if(userOpt.isEmpty()) {
            response.put("message", "There is no such user.");
            return response;
        }
        User userEntity = userOpt.get();
        String oldUsername = userEntity.getUsername();

        HashMap<String, Object> user = userService.update(request.getUser(), session.getUserId());
        HashMap<String, Object> profile = service.update(request.getProfile(), session.getUserId());

        boolean successfull = ((Boolean) user.getOrDefault("success", false) ||
                (Boolean) profile.getOrDefault("success", false));
        if(!successfull) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Sending update to topic
        messagingTemplate.convertAndSend("/topic/update/" + oldUsername, oldUsername);

        response.put("success", true);
        response.put("user", user);
        response.put("profile", profile);
        response.put("message", "Your data were saved correctly.");
        return response;
    }

    public static class ProfileUpdateRequest {

        private String token;
        private UpdateUserRequest user;
        private UpdateProfileRequest profile;

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }

        public UpdateUserRequest getUser() {
            return user;
        }
        public void setUser(UpdateUserRequest user) {
            this.user = user;
        }

        public UpdateProfileRequest getProfile() { return profile; }
        public void setProfile(UpdateProfileRequest profile) {
            this.profile = profile;
        }
    }



}
