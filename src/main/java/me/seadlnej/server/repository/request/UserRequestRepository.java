package me.seadlnej.server.repository.request;

import me.seadlnej.server.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
    List<UserRequest> findByReceiverId(Long receiverId);
    List<UserRequest> findBySenderId(Long senderId);
    List<UserRequest> findByReceiverIdAndStatus(Long receiverId, UserRequest.Status status);
    List<UserRequest> findBySenderIdAndStatusIn(Long senderId, List<UserRequest.Status> statuses);
    Optional<UserRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}