package me.seadlnej.app.managers.contact;

import javafx.scene.image.Image;

import java.time.LocalDateTime;

public class Contact {

    // Group constants
    private String name;
    private final Long id;

    // Direct constants
    private String firstname;
    private String lastname;
    private String username;
    private String status;

    private String lastMessage;
    private boolean messageRead;
    private LocalDateTime lastMessageTime;
    private Image profilePicture;

    private final ContactType type;

    // Constructor for Direct
    public Contact(String firstname, String lastname, String username, String status, Long id, String lastMessage,
                   boolean messageRead, LocalDateTime lastMessageTime, Image profilePicture) {

        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.status = status;

        this.id = id;
        this.name = null;

        this.lastMessage = lastMessage;
        this.messageRead = messageRead;
        this.lastMessageTime = lastMessageTime;
        this.profilePicture = profilePicture;

        this.type = ContactType.DIRECT;
    }

    // Constructor for group
    public Contact(Long id, String name, String lastMessage, boolean messageRead,
                   LocalDateTime lastMessageTime, Image profilePicture) {
        this.firstname = null;
        this.lastname = null;
        this.username = null;
        this.status = null;

        this.id = id;
        this.name = name;

        this.lastMessage = lastMessage;
        this.messageRead = messageRead;
        this.lastMessageTime = lastMessageTime;
        this.profilePicture = profilePicture;

        this.type = ContactType.GROUP;
    }

    // Getters and Setters
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public boolean isMessageRead() { return messageRead; }
    public void setMessageRead(boolean messageRead) { this.messageRead = messageRead; }

    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public Image getProfilePicture() { return profilePicture; }
    public void setProfilePicture(Image profilePicture) { this.profilePicture = profilePicture; }

    public ContactType getType() { return type; }

}