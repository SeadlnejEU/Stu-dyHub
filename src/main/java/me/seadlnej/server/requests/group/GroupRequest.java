package me.seadlnej.server.requests.group;

import java.util.List;

public class GroupRequest {

    private String token;
    private String name;
    private  byte[] image;
    private String description;
    //private List<String> usernames; // usernames of users to add

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public byte[] getImage() { return image; }

    public void setImage(byte[] image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
/*
    public List<String> getUsernames() { return usernames; }
    public void setUsernames(List<String> usernames) { this.usernames = usernames; }
*/
}