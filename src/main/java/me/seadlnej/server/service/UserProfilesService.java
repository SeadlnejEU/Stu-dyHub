package me.seadlnej.server.service;

import me.seadlnej.server.model.UserProfile;
import me.seadlnej.server.repository.user.ProfileRepository;
import me.seadlnej.server.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfilesService {

    private final UserRepository userRepository;
    private final ProfileRepository repository;

    public UserProfilesService(UserRepository userRepository, ProfileRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    public UserProfile setProfile(Long userId, UserProfile  data) {

        UserProfile profile = repository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            p.setUser(userRepository.getReferenceById(userId));
            return p;
        });

        profile.setImage(data.getImage());
        profile.setStatus(data.getStatus());
        profile.setAddress(data.getAddress());
        profile.setBirthdate(data.getBirthdate());

        return repository.save(profile);
    }
}
