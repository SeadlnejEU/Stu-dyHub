package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository  extends JpaRepository<UserProfile, Long>  {

    UserProfile findByUserId(Long user_id);

}