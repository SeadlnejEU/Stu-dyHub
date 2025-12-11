package me.seadlnej.server.controller.group;

import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.service.chat.GroupService;
import me.seadlnej.server.service.UserSessionService;
import org.hibernate.mapping.Map;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    // Services
    private final GroupService service;
    private final UserSessionService sessionService;

    // Constructor
    public GroupController(GroupService service, UserSessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @PostMapping("/create")
    public HashMap<String, Object> createGroup(@RequestBody(required = false) HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        if(request == null) {
            response.put("message", "No data were send");
            return response;
        }

        String token = (String) request.get("token");
        String name = (String) request.get("name");
        String description = (String) request.get("description");

        // Handle image sent as Base64 string
        byte[] image = null;
        Object imageObj = request.get("image");
        if(imageObj instanceof String) {
            try {
                image = Base64.getDecoder().decode((String) imageObj);
            } catch(Exception e) {
                response.put("message", "Invalid image format");
                return response;
            }
        }

        // Handle users safely
        HashMap<String, String> users = new HashMap<>();
        Object usersObj = request.get("users");
        if(usersObj instanceof Map) {
            users = (HashMap<String, String>) usersObj;
        }

        if(token == null || token.isBlank() || name == null || name.isBlank() ||
                description == null || description.isBlank() || image == null) {
            response.put("message", "No data were send");
            return response;
        }

        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message","Invalid session token.");
            return response;
        }

        return service.create(session.getUserId(), name, description, image, users);
    }


    // Update group endpoint
    @PostMapping("/update")
    public HashMap<String, Object> updateGroup(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        // Bezpečné čítanie dát
        Object tokenObj = request.get("token");
        Object idObj = request.get("id");
        Object nameObj = request.get("name");
        Object descriptionObj = request.get("description");
        Object imageObj = request.get("image");

        if(tokenObj == null || idObj == null || nameObj == null || descriptionObj == null) {
            response.put("message", "Missing required fields");
            return response;
        }

        String token = tokenObj.toString();
        Long id = null;
        if(idObj instanceof Number) {
            id = ((Number) idObj).longValue();
        } else {
            try {
                id = Long.parseLong(idObj.toString());
            } catch(NumberFormatException e) {
                response.put("message", "Invalid group ID");
                return response;
            }
        }

        String name = nameObj.toString();
        String description = descriptionObj.toString();

        byte[] image = null;
        if(imageObj instanceof String) {
            try {
                image = Base64.getDecoder().decode((String) imageObj);
            } catch(Exception e) {
                response.put("message", "Invalid image format");
                return response;
            }
        }

        if(token.isBlank() || name.isBlank() || description.isBlank()) {
            response.put("message", "Required fields cannot be blank");
            return response;
        }

        // Validate session
        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        return service.update(session.getUserId(), id, name, description, image);
    }

    // Get group users
    @PostMapping("/users")
    public HashMap<String, Object> users(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        String token = (String) request.get("token");
        Long id = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;

        if(token == null || token.isBlank()) {
            response.put("message", "Invalid token");
            return response;
        }

        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        return service.users(id);
    }

    // Add member to group
    @PostMapping("/add")
    public HashMap<String, Object> addMember(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        String token = (String) request.get("token");
        Long id = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;
        String username = (String) request.get("username");

        if(token == null || token.isBlank() || username == null || username.isBlank()) {
            response.put("message", "Invalid data");
            return response;
        }

        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        return service.add(session.getUserId(), id, username);
    }

    // Remove member from group
    @PostMapping("/remove")
    public HashMap<String, Object> removeMember(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        String token = (String) request.get("token");
        Long id = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;
        String username = (String) request.get("username");

        if(token == null || token.isBlank() || username == null || username.isBlank()) {
            response.put("message", "Invalid data");
            return response;
        }

        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        return service.remove(session.getUserId(), id, username);
    }

    // Delete group
    @PostMapping("/delete")
    public HashMap<String, Object> deleteGroup(@RequestBody HashMap<String, Object> request) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        String token = (String) request.get("token");
        Long id = request.get("id") instanceof Number ? ((Number) request.get("id")).longValue() : null;

        if(token == null || token.isBlank()) {
            response.put("message", "No token provided");
            return response;
        }

        UserSession session = sessionService.validateToken(token);
        if(session == null) {
            response.put("message", "Invalid session token.");
            return response;
        }

        return service.delete(session.getUserId(), id);
    }
}
