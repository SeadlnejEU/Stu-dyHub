package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUserIdAndReadFalse(Long userId);
    List<UserNotification> findByUserId(Long userId);
}