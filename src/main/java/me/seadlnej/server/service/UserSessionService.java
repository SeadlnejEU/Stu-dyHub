package me.seadlnej.server.service;

import me.seadlnej.server.model.UserSession;
import me.seadlnej.server.repository.user.SessionRepository;
import me.seadlnej.server.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserSessionService {

    private final SessionRepository repository;
    private final UserRepository userRepository;

    public UserSessionService(SessionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public UserSession createSession(Long userId, String ip) {

        UserSession session = new UserSession();

        session.setUserId(userId);
        session.setToken(UUID.randomUUID().toString());
        session.setIp(ip);
        session.setExpiresAt(LocalDateTime.now().plusDays(30));

        return repository.save(session);
    }

    public UserSession validateToken(String token) {

        UserSession session = repository.findByToken(token);

        if (session == null) {
            return null;
        }

        if(userRepository.findById(session.getUserId()).isEmpty()) { return null; }

        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        session.setLastUsed(LocalDateTime.now());
        repository.save(session);

        return session;
    }

    public UserSession validateToken(String token, String ip) {

        UserSession sessionOpt = repository.findByToken(token);

        if (sessionOpt == null) {
            return null;
        }

        UserSession session = sessionOpt;
        if (!session.getIp().equals(ip)) {
            return null;
        }

        if(userRepository.findById(session.getUserId()).isEmpty()) {
            return null; }

        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        session.setLastUsed(LocalDateTime.now());
        repository.save(session);
        return session;
    }

    public void deleteUserSession(String token) {
        repository.deleteByToken(token);
    }

    public void deleteUserSessions(Long userId) {
        repository.deleteByUserId(userId);
    }
}