package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetRepository extends JpaRepository<UserReset, Long> {
    UserReset findByToken(String token);
    void deleteByUserId(Long userId);
}