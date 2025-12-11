package me.seadlnej.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.seadlnej.server.model.User;
import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.requests.LoginRequest;
import me.seadlnej.server.requests.RegisterRequest;
import me.seadlnej.server.requests.EmailRequest;
import me.seadlnej.server.requests.user.PasswordResetRequest;
import me.seadlnej.server.requests.user.RegisterCompleteRequest;
import me.seadlnej.server.service.UserSessionService;
import me.seadlnej.server.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final UserService service;
    private final UserSessionService userSessionService;

    public AuthController(UserService service, UserSessionService userSessionService) {
        this.service = service;
        this.userSessionService = userSessionService;
    }

    @PostMapping("/me")
    public HashMap<String, Object> getCurrentUser(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        String token = (String) request.get("token");
        if(token == null || token.isBlank()) {
            response.put("message", "Invalid token");
            return response;
        }

        UserSession session = userSessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        // Získať aktuálne údaje používateľa
        return service.getUserByToken(token);
    }

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        // Trying to log in request
        HashMap<String, Object> response = service.login(request, httpRequest);
        if (!(Boolean) response.getOrDefault("success", false)) { return response; }

        String ip = httpRequest.getRemoteAddr(); // Ip address of sender
        UserSession session = userSessionService.createSession((Long) response.get("user"), ip); // Creates and saves session

        // Returning
        response.remove("user");
        response.put("token", session.getToken());
        return response;
    }

    @PostMapping("/register")
    public HashMap<String, Object> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response

        // If request is null
        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Returning
        return service.register(request);
    }

    @PostMapping("/register-resend")
    public HashMap<String, Object> registerResend(@RequestBody ResendRegister request) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response

        // If request is null
        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Returning
        return service.registerResend(request.getToken());
    }

    public static class ResendRegister {
        private String token;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    @PostMapping("/register-complete")
    public HashMap<String, Object> registerComplete(@RequestBody RegisterCompleteRequest request, HttpServletRequest httpRequest) {

        HashMap<String, Object> response = new HashMap<>(); // Api response

        // Trying to register request
        response = service.registerComplete(request);
        if (!(Boolean) response.getOrDefault("success", false)) { return response; }

        String ip = httpRequest.getRemoteAddr(); // Ip address of sender
        UserSession session = userSessionService.createSession((Long) response.get("user"), ip); // Creates and saves session

        // Returning
        response.remove("user");
        response.put("token", session.getToken());
        return response;
    }

    @PostMapping("/password-reset/request")
    public HashMap<String, Object> passwordReset(@RequestBody EmailRequest request) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response

        // If request is null
        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Returning
        return service.passwordReset(request.getEmail());
    }
    @PostMapping("/password-reset/complete")
    public HashMap<String, Object> passwordComplete(@RequestBody PasswordResetRequest request) {

        HashMap<String, Object>  response = new HashMap<>(); // Api response

        // If request is null
        if(request == null) {
            response.put("message", "Not all data were sent.");
            return response;
        }

        // Returning
        return service.passwordComplete(request);
    }

}