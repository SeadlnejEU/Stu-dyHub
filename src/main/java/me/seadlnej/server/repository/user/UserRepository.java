package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);

    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
    boolean existsByPhone(String username);

}