package me.seadlnej.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.service.UserSessionService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SessionController {

    private final UserSessionService userSessionService;

    public SessionController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @PostMapping("/token")
    public HashMap<String, Object> verify(@RequestParam String token, HttpServletRequest httpRequest) {

        HashMap<String, Object> response = new HashMap<>(); // Api response

        String ip = httpRequest.getRemoteAddr();
        UserSession session = userSessionService.validateToken(token);
        if (session == null) {
            response.put("token-validation", "invalid");
            return response;
        }

        response.put("token-validation", "valid");
        return response;
    }

}