package me.seadlnej.server.controller.notification;

import jakarta.servlet.http.HttpServletRequest;
import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.service.UserSessionService;
import me.seadlnej.server.service.user.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserSessionService sessionService;

    public NotificationController(NotificationService service, UserSessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    // Getting all notifications
    @PostMapping("/show")
    public HashMap<String, Object> showNotifications(
            @RequestBody(required = false) HashMap<String, Object> request
    ) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Constants
        String token = request.get("token").toString();

        // If request is null
        if(token.isBlank()) { response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking if session is valid
        UserSession session = sessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token.");
            return response;
        }

        return service.getNotifications(session.getUserId());
    }

    // Delete specific notification
    @PostMapping("/delete")
    public HashMap<String, Object> deleteNotification(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        Long id = (Long) request.get("id");
        String token = request.get("token").toString();

        // If request is null
        if(id == null || token.isBlank()) { response.put("message", "Not all data were sent.");
            return response;
        }

        // Checking if session is valid
        UserSession session = sessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token.");
            return response;
        }

        return service.deleteNotification(id, session.getUserId());
    }
}
