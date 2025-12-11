package me.seadlnej.server.controller.chat;

import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.service.UserSessionService;
import me.seadlnej.server.service.chat.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    // Services
    private final ChatService service;
    private final UserSessionService userSessionService;

    // Repositories

    // Constructor
    public ChatController(ChatService service, UserSessionService userSessionService) {
        this.service = service;
        this.userSessionService = userSessionService;
    }

    // Send all conversation and contacts (directs, groups)
    @PostMapping("/contacts")
    public HashMap<String, Object> getContacts(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Constants
        String token = (String) request.get("token");

        // Checking if token is null
        if(token == null || token.isBlank()) { response.put("message","No token provided."); return response; }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token."); return response; }

        // Returning
        return service.getContacts(session.getUserId());
    }

    @PostMapping("/send")
    public HashMap<String, Object> sendMessage(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Constants
        String token = (String) request.get("token");
        String text = (String) request.get("text");
        Long conversationId = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;

        // Checking if token is null
        if(token == null || token.isBlank()) { response.put("message","No token provided."); return response; }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token."); return response; }

        // Checking if data is null
        if(conversationId == null || text == null) { response.put("message","Missing data."); return response; }

        // Returning
        return service.sendMessage(session.getUserId(), conversationId, text);
    }

    @PostMapping("/messages")
    public HashMap<String, Object> getMessages(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Constants
        String token = (String) request.get("token");
        Long conversationId = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;

        // Checking if token is null
        if(token == null || token.isBlank()) { response.put("message","No token provided."); return response; }

        // Checking if token is null
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token."); return response; }

        // Checking if data is null
        if(conversationId == null) { response.put("message","No conversationId provided."); return response; }

        // Returning
        return service.getMessages(session.getUserId(), conversationId);
    }

    // Delete chat for user
    @PostMapping("/delete")
    public HashMap<String, Object> deleteMessage(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // default false

        // Constants
        String token = (String) request.get("token");
        Long messageId = request.get("messageId") instanceof Number ? ((Number) request.get("messageId")).longValue() : null;
        Long conversationId = request.get("conversationId") instanceof Number ? ((Number) request.get("conversationId")).longValue() : null;

        // Checking if token is null
        if(token == null || token.isBlank()) { response.put("message","No token provided."); return response; }

        // Checking if session is valid
        UserSession session = userSessionService.validateToken(token);
        if(session == null) { response.put("message","Invalid session token."); return response; }

        // Checking if data is null
        if(conversationId == null) { response.put("message","No conversationId provided."); return response; }

        // Returning
        return service.deleteMessage(session.getUserId(), messageId);
    }
}
