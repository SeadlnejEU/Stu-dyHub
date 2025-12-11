package me.seadlnej.server.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserContactId implements Serializable {
    private Long userId;
    private Long contactId;

    public UserContactId() {}

    public UserContactId(Long userId, Long contactId) {
        this.userId = userId;
        this.contactId = contactId;
    }

    // getters a setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    // equals a hashCode (pre správne fungovanie @EmbeddedId)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserContactId)) return false;
        UserContactId that = (UserContactId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(contactId, that.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, contactId);
    }
}