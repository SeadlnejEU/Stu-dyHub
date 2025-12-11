package me.seadlnej.server.controller.user;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.requests.TokenRequest;
import me.seadlnej.server.service.UserSessionService;
import me.seadlnej.server.service.user.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    // Services
    private final RequestService service;
    private final UserSessionService userSessionService;

    // Repositories

    // Constructor
    public RequestController(RequestService service, UserSessionService userSessionService) {
        this.service = service;
        this.userSessionService = userSessionService;
    }

    // Send all pending requests
    @PostMapping("/show")
    public HashMap<String, Object> show(@RequestBody(required = false) HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Constants
        String token = (String) request.get("token");
        System.out.println("gg");

        if(token == null || token.isBlank()) { response.put("message", "No data were send.");
            return response;
        }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token.");
            return response;
        }

        return service.show(session.getUserId());
    }

    @PostMapping("/send")
    public HashMap<String, Object> send(@RequestBody(required = false) HashMap<String, Object> request) {
        String token = (String) request.get("token");
        String receiver = (String) request.get("receiver");

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        if(token == null || receiver == null || token.isBlank() || receiver.isBlank()) {
            response.put("message", "Not all data were sent."); return response;
        }

        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token."); return response; }

        return service.send(session.getUserId(), receiver);
    }

    @PostMapping("/respond")
    public ApiResponse respond(@RequestBody RespondRequest request) {
        ApiResponse response = new ApiResponse().setSuccess(false);

        UserSession session = userSessionService.validateToken(request.getToken());
        if(session == null) { return response.setData("message","Invalid session token."); }

        return response.setData("response", service.respond(session.getUserId(), request.getRequestId(), request.isAccepted()));
    }

    public static class RespondRequest {
        private String token;
        private Long requestId;
        private boolean accepted;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Long getRequestId() { return requestId; }
        public void setRequestId(Long requestId) { this.requestId = requestId; }

        public boolean isAccepted() { return accepted; }
        public void setAccepted(boolean accepted) { this.accepted = accepted; }
    }

    public static class NotificationConfirmRequest {
        private String token;
        private Long notificationId;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Long getNotificationId() { return notificationId; }
        public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    }
}
