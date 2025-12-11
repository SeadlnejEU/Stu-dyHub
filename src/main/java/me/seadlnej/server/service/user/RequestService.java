package me.seadlnej.server.service.user;

import me.seadlnej.server.model.*;
import me.seadlnej.server.repository.user.*;
import me.seadlnej.server.service.chat.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RequestService {

    private final ChatService chatService;

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ContactRepository contactRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public RequestService(
            RequestRepository requestRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            ContactRepository contactRepository,
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate,
            NotificationService notificationService,
            ChatService chatService
    ) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.contactRepository = contactRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.chatService = chatService;
    }

    // Showing all requests of user
    public HashMap<String, Object> show(Long userId) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if user id is null
        if(userId == null) { response.put("message", "No data were send."); return response; }

        // List of all user requests where receiver id is user's
        List<UserRequest> requests = requestRepository.findByReceiverId(userId);
        HashMap<String, Object> responseMap = new HashMap<>(); // HashMap for requests

        int i = 1; // Going through all requests
        for(UserRequest req : requests) {

            // Sender account
            User sender = userRepository.findById(req.getSenderId()).orElse(null);
            if (sender == null) continue;

            // Sender profile
            Optional<UserProfile> profile = profileRepository.findById(req.getSenderId());
            if (profile.isEmpty()) continue;

            // Map for data of request
            Map<String, Object> data = new HashMap<>();

            // Image
            byte[] imageBytes = Optional.ofNullable(profile.get().getImage()).orElse(new byte[0]);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Adding data to request data
            data.put("id", req.getId());
            data.put("image", base64Image);
            data.put("firstname", sender.getFirstname());
            data.put("lastname", sender.getLastname());
            data.put("username", sender.getUsername());
            data.put("created", req.getCreatedAt().toString());

            // Adding data to response map
            responseMap.put("request-" + i, data);

            i++;
        }

        // Returning
        response.put("success", true);
        response.put("requests", responseMap);
        return response;
    }

    //  Send new friend request
    public HashMap<String, Object> send(Long senderId, String username) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if data is null
        if(senderId == null || username.isBlank()) { response.put("message", "No data were send."); return response; }

        // Finding receiver user
        User receiver = userRepository.findByUsername(username);
        if(receiver == null) { response.put("message", "Invalid receiver."); return response; }

        // Cant request yourself
        if(senderId.equals(receiver.getId())) { response.put("message", "Cannot send friend request to yourself."); return response; }

        // Checking if there is already request sent
        boolean alreadyRequested = requestRepository.findBySenderIdAndReceiverId(senderId, receiver.getId()).isPresent()
                || requestRepository.findBySenderIdAndReceiverId(receiver.getId(), senderId).isPresent();

        if(alreadyRequested) { response.put("message", "Request already sent."); return response; }

        // If receiver is already contact
        UserContactId contactId = new UserContactId(senderId, receiver.getId());
        if(contactRepository.existsById(contactId)) { response.put("message", "You are already friends."); return response; }

        // Creating new request
        UserRequest request = new UserRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiver.getId());
        request.setCreatedAt(LocalDateTime.now());

        // Saving into repository
        requestRepository.save(request);

        // Sending topic update
        messagingTemplate.convertAndSend("/topic/requests/" + username, "true");

        // Creation notification
        notificationService.createNotification(receiver.getId(), receiver.getUsername(),
                "You've got new friend request", "Friend request");

        // Returning
        response.put("success", true);
        response.put("message", "Request successfully sent.");
        return response;
    }

    // Responding to friend request
    public HashMap<String, Object> respond(Long userId, Long requestId, boolean accept) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if data is null
        if(userId == null || requestId == null) { response.put("message", "No data were send."); return response; }

        // If request is not found
        UserRequest request = requestRepository.findById(requestId).orElse(null);
        if(request == null) { response.put("message", "Request not found."); return response; }

        // Checking if request belongs to user
        if(!request.getReceiverId().equals(userId)) { response.put("message", "You are not allowed to respond to this request."); return response; }

        // Setting request status if its accepted or rejected
        request.setStatus(accept ? UserRequest.Status.accepted : UserRequest.Status.rejected);
        request.setRespondedAt(LocalDateTime.now());

        if(accept) {

            // Creating new contact
            UserContact contact = new UserContact();
            contact.setUserId(request.getSenderId());
            contact.setContactId(request.getReceiverId());
            contact.setSince(LocalDateTime.now());

            // Saving contact and deleting request
            contactRepository.save(contact);
            requestRepository.delete(request);
        } else {
            requestRepository.save(request); // User can still accept request
        }

        // Accepted or rejected
        String accepted = "rejected.";
        if(accept) {
            accepted = "accepted.";
        }

        // Sending notification to request sender;
        Optional<User> senderOpt = userRepository.findById(request.getSenderId());
        if(senderOpt.isPresent()) {
            // Sending update topic
            messagingTemplate.convertAndSend("/topic/requests/" + senderOpt.get().getUsername(), "true");
            notificationService.createNotification(request.getSenderId(), senderOpt.get().getUsername(),
                    "Your friend request was " + accepted, "Friend request");
            chatService.createDirectConversation(senderOpt.get().getId(), request.getReceiverId());
        }

        // Returning
        response.put("success", true);
        response.put("message", "Request was " + accepted);
        return response;
    }

}