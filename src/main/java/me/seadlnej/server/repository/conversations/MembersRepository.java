package me.seadlnej.server.repository.conversations;

import me.seadlnej.server.model.Conversation;
import me.seadlnej.server.model.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembersRepository extends JpaRepository<ConversationMember, Long> {

    // Všetky ID konverzácií, kde je user členom
    @Query("SELECT cm.conversationId FROM ConversationMember cm WHERE cm.userId = :userId")
    List<Long> findConversationIdsByUserId(Long userId);

    // Všetky group konverzácie používateľa
    @Query("SELECT c FROM Conversation c JOIN ConversationMember m ON m.conversationId = c.id WHERE m.userId = :userId AND c.type = 'group'")
    List<Conversation> findGroupsOfUser(Long userId);

    // Všetky direct konverzácie používateľa
    @Query("SELECT c FROM Conversation c JOIN ConversationMember m ON m.conversationId = c.id WHERE m.userId = :userId AND c.type = 'direct'")
    List<Conversation> findDirectsOfUser(Long userId);

    // Všetky konverzácie používateľa (direct aj group)
    @Query("SELECT c FROM Conversation c JOIN ConversationMember m ON m.conversationId = c.id WHERE m.userId = :userId")
    List<Conversation> findAllConversationsOfUser(Long userId);

    // Nájde konkrétneho člena v konverzácii (na overenie, že user je člen)
    @Query("SELECT cm FROM ConversationMember cm WHERE cm.conversationId = :conversationId AND cm.userId = :userId")
    ConversationMember findByConversationIdAndUserId(Long conversationId, Long userId);

    @Query("SELECT cm FROM ConversationMember cm WHERE cm.conversationId = :conversationId")
    List<ConversationMember> findByConversationId(Long conversationId);

    // Skontroluje, či user je členom konkrétnej konverzácie
    boolean existsByConversationIdAndUserId(Long conversationId, Long userId);

}