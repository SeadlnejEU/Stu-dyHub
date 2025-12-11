package me.seadlnej.server.repository.conversations;
import me.seadlnej.server.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN ConversationMember m ON m.conversationId = c.id WHERE m.userId = :userId")
    List<Conversation> findAllOfUser(Long userId);

    List<Conversation> findAllByIdInAndType(List<Long> ids, Conversation.Type type);

}