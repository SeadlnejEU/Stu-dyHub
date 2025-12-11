package me.seadlnej.server.service.chat;

import me.seadlnej.server.model.*;
import me.seadlnej.server.repository.conversations.ConversationRepository;
import me.seadlnej.server.repository.conversations.MediaRepository;
import me.seadlnej.server.repository.conversations.MembersRepository;
import me.seadlnej.server.repository.conversations.MessageRepository;
import me.seadlnej.server.repository.user.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {

    // Repositories
    private final ConversationRepository repository;

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final MembersRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MediaRepository mediaRepository;
    private final ContactRepository contactRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Constructor
    public ChatService(
            ConversationRepository repository,
            UserRepository userRepository, ProfileRepository profileRepository,
            MembersRepository memberRepository, MessageRepository messageRepository,
            MediaRepository mediaRepository, ContactRepository contactRepository,
            ConversationRepository conversationRepository, SimpMessagingTemplate messagingTemplate
    ) {
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.contactRepository = contactRepository;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Get all contacts (directs and groups)
    public HashMap<String, Object> getContacts(Long userId) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // List of all conversations where
        List<Conversation> conversations = memberRepository.findAllConversationsOfUser(userId);

        // Map for conversation data
        HashMap<String, Object> contactsMap = new HashMap<>();
        int i = 1;

        // Going through list
        for (Conversation conv : conversations) {

            // Create HashMap and putting type and id
            HashMap<String, Object> convMap = new HashMap<>();
            convMap.put("id", conv.getId());
            convMap.put("type", conv.getType().name());

            //
            if(conv.getType() == Conversation.Type.group) {
                convMap.put("name", conv.getName());
                convMap.put("description", conv.getDescription());
                convMap.put("image", conv.getGroupImage() != null ? Base64.getEncoder().encodeToString(conv.getGroupImage()) : null);
            } else {


                List<ConversationMember> members = memberRepository.findByConversationId(conv.getId());
                ConversationMember otherMember = members.stream()
                        .filter(m -> !m.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);

                if(otherMember == null) continue;;

                Optional<UserProfile> otherProfileOpt = profileRepository.findById(otherMember.getUserId());
                if (otherProfileOpt.isEmpty()) continue;
                UserProfile otherProfile = otherProfileOpt.get();

                convMap.put("firstname", otherProfile.getUser().getFirstname());
                convMap.put("lastname", otherProfile.getUser().getLastname());
                byte[] imageBytes = Optional.ofNullable(otherProfile.getImage()).orElse(new byte[0]);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                convMap.put("image", base64Image);
                convMap.put("status", otherProfile.getStatus());
                convMap.put("create", conv.getCreatedAt());

            }

            Optional<ConversationMessage> lastMsg = messageRepository.findFirstByConversationIdOrderBySentAtDesc(conv.getId());
            if(lastMsg.isPresent()) {
                convMap.put("lastMessage", lastMsg.get().getTextContent());
                convMap.put("lastSent", lastMsg.get().getSentAt().toString());
            } else {
                convMap.put("lastMessage", "No messages yet.");
                convMap.put("lastSent", LocalDateTime.now());
            }

            contactsMap.put("contact-" + i, convMap);
            i++;
        }

        // Returning response
        response.put("success", true);
        response.put("contacts", contactsMap);
        return response;
    }

    // Send message to conversation
    public HashMap<String, Object> sendMessage(Long senderId, Long conversationId, String text) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if data is null
        if(senderId == null || conversationId == null || text.isBlank()) {
            response.put("message", "No data were send.");
            return response;
        }

        // If member is in conversation
        if (!memberRepository.existsByConversationIdAndUserId(conversationId, senderId)) {
            response.put("message", "User is not part of this conversation.");
            return response;
        }

        // Creating new message
        ConversationMessage message = new ConversationMessage();
        message.setType(ConversationMessage.Type.text);
        message.setConversationId(conversationId);
        message.setSentAt(LocalDateTime.now());
        message.setSenderId(senderId);
        message.setTextContent(text);
        message.setSentAt(LocalDateTime.now());
        message.setMediaId(0L);

        ConversationMessage saved = messageRepository.save(message);

        String destination = "/topic/messages/" + saved.getConversationId();
        messagingTemplate.convertAndSend(destination,"true");

        // Returning response
        response.put("success", true);
        response.put("messageId", message.getId());
        return response;
    }

    public HashMap<String, Object> getMessages(Long userId, Long conversationId) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if user is member of conversation
        ConversationMember member = memberRepository.findByConversationIdAndUserId(conversationId, userId);
        if(member == null) { response.put("message","You are not a member of this conversation."); return response; }

        // List of all messages
        List<ConversationMessage> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);

        // HashMap for messages
        HashMap<String, Object> responseMap = new HashMap<>();

        int i = 1; // Going through all messages
        for(ConversationMessage msg : messages) {

            // Map for data of message
            HashMap<String, Object> data = new HashMap<>();

            // Adding data to message data
            data.put("id", msg.getId());
            data.put("senderId", msg.getSenderId());
            data.put("text", msg.getTextContent());
            data.put("mediaId", msg.getMediaId());
            data.put("type", msg.getType().name());
            data.put("isOwn", msg.getSenderId().equals(userId));
            data.put("sentAt", msg.getSentAt().toString());

            // doplnenie údajov o senderovi
            Optional<UserProfile> profileOpt = profileRepository.findById(msg.getSenderId());
            if (profileOpt.isPresent()) {
                UserProfile profile = profileOpt.get();
                data.put("firstname", profile.getUser().getFirstname());
                data.put("lastname", profile.getUser().getLastname());
                byte[] imageBytes = Optional.ofNullable(profile.getImage()).orElse(new byte[0]);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                data.put("image", base64Image);
            } else {
                data.put("firstname", "Unknown");
                data.put("lastname", "User");
                data.put("image", "");
            }

            // Adding data to response map
            responseMap.put("message-" + i, data);
            i++;
        }

        response.put("success", true);
        response.put("messages", responseMap);
        return response;
    }

    public HashMap<String, Object> deleteMessage(Long userId, Long messageId) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        if (userId == null || messageId == null) {
            response.put("message", "Missing parameters.");
            return response;
        }

        Optional<ConversationMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            response.put("message", "Message not found.");
            return response;
        }

        ConversationMessage message = messageOpt.get();


        if (!message.getSenderId().equals(userId)) {
            response.put("message", "You are not allowed to delete this message.");
            return response;
        }

        messageRepository.deleteById(messageId);

        String destination = "/topic/messages/" + message.getConversationId();
        messagingTemplate.convertAndSend(destination, "deleted-" + messageId);

        response.put("success", true);
        response.put("message", "Message deleted successfully.");
        response.put("messageId", messageId);
        return response;
    }


    public void createDirectConversation(Long sender, Long receiver) {

        Conversation conversation = new Conversation();
        conversation.setType(Conversation.Type.direct);
        conversation.setName("Direct Chat");
        conversation.setCreatedBy(sender);
        conversation.setCreatedAt(LocalDateTime.now());

        repository.save(conversation);

        ConversationMember member1 = new ConversationMember();
        member1.setConversationId(conversation.getId());
        member1.setUserId(sender);
        member1.setRole(ConversationMember.Role.member);
        member1.setJoinedAt(LocalDateTime.now());
        memberRepository.save(member1);

        ConversationMember member2 = new ConversationMember();
        member2.setConversationId(conversation.getId());
        member2.setUserId(receiver);
        member2.setRole(ConversationMember.Role.member);
        member2.setJoinedAt(LocalDateTime.now());
        memberRepository.save(member2);

        Optional<User> senderOpt = userRepository.findById(sender);
        Optional<User> receiverOpt = userRepository.findById(receiver);
        if(senderOpt.isPresent() && receiverOpt.isPresent()) {
            messagingTemplate.convertAndSend("/topic/chats/" + senderOpt.get().getUsername(), "true");
            messagingTemplate.convertAndSend("/topic/chats/" + receiverOpt.get().getUsername(), "true");
        }

    }

}
