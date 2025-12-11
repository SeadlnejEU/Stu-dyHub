package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<UserRequest, Long> {
    List<UserRequest> findByReceiverId(Long receiverId);
    Optional<UserRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

}