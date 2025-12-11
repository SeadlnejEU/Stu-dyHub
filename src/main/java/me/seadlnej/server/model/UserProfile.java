package me.seadlnej.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Offline;

    @Column(length = 64)
    private String address;

    private LocalDate birthdate;

    @Column(length = 64)
    private String bio;

    // Enum class for status of user
    public enum Status { Online, Offline, Away, Busy, DnD }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

}