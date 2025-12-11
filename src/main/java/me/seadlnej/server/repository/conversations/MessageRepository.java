package me.seadlnej.server.repository.conversations;

import me.seadlnej.server.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<ConversationMessage, Long> {
    List<ConversationMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);

    Optional<ConversationMessage> findFirstByConversationIdOrderBySentAtDesc(Long conversationId);
}