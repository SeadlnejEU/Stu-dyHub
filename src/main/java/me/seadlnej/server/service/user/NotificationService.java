package me.seadlnej.server.service.user;

import me.seadlnej.server.model.UserNotification;
import me.seadlnej.server.repository.user.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class NotificationService {

    // Repositories
    private final NotificationRepository repository;

    private final SimpMessagingTemplate messaging;

    public NotificationService(NotificationRepository repository, SimpMessagingTemplate messaging) {
        this.repository = repository;
        this.messaging = messaging;
    }

    // Getting all notification of user
    public HashMap<String, Object> getNotifications(Long userId) {

        HashMap<String, Object> response = new HashMap<>(); // Api handler
        response.put("success", false); // Default false

        if(userId == null) { response.put("message", "No data were send."); return response; }

        List<UserNotification> notifications = repository.findByUserId(userId);
        HashMap<String, Object> respondMap = new HashMap<>();

        int i = 1;
        for(UserNotification notification : notifications) {

            HashMap<String, Object> data = new HashMap<>();

            data.put("id", notification.getId());
            data.put("type", notification.getType());
            data.put("read", notification.isRead());
            data.put("message", notification.getMessage());
            data.put("createdAt", notification.getCreatedAt());

            respondMap.put("notification-" + i, data);

            i += 1;
        }

        response.put("success", true);
        response.put("notifications", respondMap);
        return response;
    }

    public HashMap<String, Object> deleteNotification(Long id, Long userId) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            repository.deleteById(id);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", "Error deleting notification.");
        }

        return map;
    }

    public HashMap<String, Object> createNotification(Long userId, String username, String message, String type) {

        HashMap<String, Object> map = new HashMap<>();

        UserNotification not = new UserNotification();
        not.setUserId(userId);
        not.setMessage(message);
        not.setType(type);

        repository.save(not);

        System.out.println("rgrg");
        String path = "/topic/notifications/" + username;
        System.out.println(path);
        messaging.convertAndSend(path, "true");

        map.put("success", true);
        return map;
    }
}
