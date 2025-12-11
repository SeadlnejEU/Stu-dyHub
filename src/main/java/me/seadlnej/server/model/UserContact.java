package me.seadlnej.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_contacts")
@IdClass(UserContactId.class)
public class UserContact {
    @Id
    private Long userId;

    @Id
    @Column(nullable = false)
    private Long contactId;

    @Column(name = "since", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime since = LocalDateTime.now();

    // Getters and Setters
    public Long getUserId() { return userId; }
    public UserContact setUserId(Long userId) { this.userId = userId; return this; }

    public Long getContactId() { return contactId; }
    public UserContact setContactId(Long contactId) { this.contactId = contactId; return this; }

    public LocalDateTime getSince() { return since; }
    public UserContact setSince(LocalDateTime since) { this.since = since; return this; }

}