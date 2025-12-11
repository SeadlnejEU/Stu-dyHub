package me.seadlnej.server.requests.group;

public class GroupRemoveRequest {

    private String token;
    private long id;
    private String username;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }


}
