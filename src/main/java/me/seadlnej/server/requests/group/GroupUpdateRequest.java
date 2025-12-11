package me.seadlnej.server.requests.group;

import java.util.List;

public class GroupUpdateRequest {

    private String token;
    private long id;
    private String name;
    private  byte[] image;
    private String description;// usernames of users to add

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public byte[] getImage() { return image; }

    public void setImage(byte[] image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}