package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SessionRepository extends JpaRepository<UserSession, Long> {

    // SPRÁVNE:
    UserSession findByToken(String token);
    UserSession findByIp(String ip);

    void deleteByUserId(Long userId);
    void deleteByToken(String token);
    void deleteByIp(String ip);

    default boolean isValid(String token) {
        UserSession session = findByToken(token);
        if (session == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return session.getExpiresAt() != null && session.getExpiresAt().isAfter(now);
    }
}