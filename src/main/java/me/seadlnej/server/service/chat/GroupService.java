package me.seadlnej.server.service.chat;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.model.Conversation;
import me.seadlnej.server.model.ConversationMember;
import me.seadlnej.server.model.User;
import me.seadlnej.server.model.UserProfile;
import me.seadlnej.server.repository.user.ProfileRepository;
import me.seadlnej.server.repository.user.UserRepository;
import me.seadlnej.server.repository.conversations.MembersRepository;
import me.seadlnej.server.repository.conversations.ConversationRepository;
import me.seadlnej.server.requests.group.GroupAddRequest;
import me.seadlnej.server.requests.group.GroupRemoveRequest;
import me.seadlnej.server.requests.group.GroupRequest;
import me.seadlnej.server.requests.group.GroupUpdateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupService {

    // Repositories
    private final ConversationRepository repository;

    private final UserRepository userRepository;
    private final MembersRepository membersRepository;
    private final ProfileRepository profileRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final String NAME_REGEX = "^[A-Za-z0-9 áčďéíľňóôřšťúýžÁČĎÉÍĽŇÓÔŘŠŤÚÝŽ]{1,16}$";
    private final String DESC_REGEX = "^[A-Za-z0-9 áčďéíľňóôřšťúýžÁČĎÉÍĽŇÓÔŘŠŤÚÝŽ.,!?-]{0,64}$";

    // Constructor
    public GroupService(ConversationRepository repository, UserRepository userRepository,
                        MembersRepository membersRepository, ProfileRepository profileRepository,
                        SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.membersRepository = membersRepository;
        this.profileRepository = profileRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public HashMap<String, Object> users(Long groupId) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false); // Default false

        // Get all members of the group
        List<ConversationMember> members = membersRepository.findByConversationId(groupId);
        HashMap<String, Object> userData = new HashMap<>();

        int i = 1;
        for (ConversationMember member : members) {
            Optional<User> userOpt = userRepository.findById(member.getUserId());
            if (userOpt.isEmpty()) continue;

            User user = userOpt.get();
            UserProfile profile = profileRepository.findByUserId(user.getId());

            // Prepare user data
            HashMap<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("firstname", profile != null ? user.getFirstname() : "");
            data.put("lastname", profile != null ? user.getLastname() : "");

            userData.put("user-" + i, data);
            i++;
        }

        // Return response
        response.put("success", true);
        response.put("users", userData);

        return response;
    }

    // Create new group
    public HashMap<String, Object> create(Long ownerId, String name, String description, byte[] image, HashMap<String, String> users) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Checking if name and description is by requirements
        if(!name.matches(NAME_REGEX)) { response.put("message", "Invalid group name"); return  response ;}
        if(!description.matches(DESC_REGEX)) { response.put("message", "Invalid group description"); return response; }

        // New Conversation entity of type group
        Conversation group = new Conversation();

        group.setType(Conversation.Type.group);
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(ownerId);
        group.setCreatedAt(LocalDateTime.now());

        if (image != null) {
            group.setGroupImage(image);
        } else {
            group.setGroupImage(new byte[0]); // or leave null if you prefer
        }
        repository.save(group); // Saving group to repository

        // Add owner as member
        ConversationMember owner = new ConversationMember();
        owner.setConversationId(group.getId());
        owner.setUserId(ownerId);
        owner.setRole(ConversationMember.Role.admin);
        owner.setJoinedAt(LocalDateTime.now());
        membersRepository.save(owner); // Saving owner as member


        // Add additional users by usernames
        if(users != null && !users.isEmpty()) {

            for(Object key : users.keySet()) {

                if(!(key instanceof String)) { continue; }
                String username = key.toString();

                User user = userRepository.findByUsername(username);
                if(user == null) { continue; }

                // Creating user as member of conversation
                ConversationMember member = new ConversationMember();
                member.setConversationId(group.getId());
                member.setUserId(user.getId());
                member.setRole(ConversationMember.Role.member);
                member.setJoinedAt(LocalDateTime.now());
                membersRepository.save(member);

                messagingTemplate.convertAndSend("/topic/general/" + user.getUsername(),"true");

            }
        }

        // Returning response
        response.put("success", true);
        response.put("message", "Group created.");
        return response;
    }

    public HashMap<String, Object> update(Long userId, Long id, String name, String description, byte[] image) {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        response.put("success", false); // Default false

        // Finding group by id
        Optional<Conversation> conversationOpt = repository.findById(id);
        if(conversationOpt.isEmpty()) { response.put("message", "Group not found"); return response; }
        Conversation group = conversationOpt.get();

        if(!group.getType().equals(Conversation.Type.group)) { response.put("message", "Can update direct conversation"); return response; }

        // Only owner can update
        ConversationMember owner = membersRepository.findByConversationIdAndUserId(id, userId);
        if(owner == null || owner.getRole() != ConversationMember.Role.admin) { response.put("message", "Unauthorized"); return response; }

        if(name != null && !name.matches(NAME_REGEX)) { response.put("message", "Invalid group name."); return response; }

        if(description != null && !description.matches(DESC_REGEX)) { response.put("message", "Invalid group description."); return response; }

        group.setName(name);
        group.setDescription(description);
        repository.save(group); // Adding group to repository

        // Returning response
        response.put("success", true);
        response.put("message", "Group updated successfully");
        return response;
    }

    public HashMap<String, Object> add(Long userId, Long groupId, String username) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        // Only owner can add members
        ConversationMember owner = membersRepository.findByConversationIdAndUserId(groupId, userId);
        if(owner == null || owner.getRole() != ConversationMember.Role.admin) {
            response.put("message", "Unauthorized");
            return response;
        }

        User user = userRepository.findByUsername(username);
        if(user == null) {
            response.put("message", "User not found");
            return response;
        }

        boolean exists = membersRepository.existsByConversationIdAndUserId(groupId, user.getId());
        if(exists) {
            response.put("message", "User already in group");
            return response;
        }

        ConversationMember member = new ConversationMember();
        member.setConversationId(groupId);
        member.setUserId(user.getId());
        member.setRole(ConversationMember.Role.member);
        member.setJoinedAt(LocalDateTime.now());
        membersRepository.save(member);

        messagingTemplate.convertAndSend("/topic/general", true);

        response.put("success", true);
        response.put("message", "Member added successfully");
        return response;
    }

    public HashMap<String, Object> remove(Long userId, Long groupId, String username) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        // Only owner can remove members
        ConversationMember owner = membersRepository.findByConversationIdAndUserId(groupId, userId);
        if(owner == null || owner.getRole() != ConversationMember.Role.admin) {
            response.put("message", "Unauthorized");
            return response;
        }

        User user = userRepository.findByUsername(username);
        if(user == null) {
            response.put("message", "User not found");
            return response;
        }

        if(user.getId().equals(userId)) {
            response.put("message", "Cannot remove yourself");
            return response;
        }

        ConversationMember member = membersRepository.findByConversationIdAndUserId(groupId, user.getId());
        if(member == null) {
            response.put("message", "User is not a member of this group");
            return response;
        }

        membersRepository.delete(member);
        messagingTemplate.convertAndSend("/topic/general", true);

        response.put("success", true);
        response.put("message", "Member removed successfully");
        return response;
    }

    public HashMap<String, Object> delete(Long userId, Long groupId) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", false);

        Optional<Conversation> conversationOpt = repository.findById(groupId);
        if(conversationOpt.isEmpty()) {
            response.put("message", "Group not found");
            return response;
        }

        Conversation group = conversationOpt.get();
        if(group.getType() == Conversation.Type.direct) {
            response.put("message", "Cannot delete direct chat");
            return response;
        }

        ConversationMember owner = membersRepository.findByConversationIdAndUserId(groupId, userId);
        if(owner == null || owner.getRole() != ConversationMember.Role.admin) {
            response.put("message", "Unauthorized");
            return response;
        }

        List<ConversationMember> members = membersRepository.findByConversationId(groupId);
        membersRepository.deleteAll(members);
        repository.delete(group);

        response.put("success", true);
        response.put("message", "Group deleted successfully");
        return response;
    }

}