package me.seadlnej.server.requests;

public class LoginRequest {

    // Login requirements
    private String username;
    private String password;

    public boolean isEmpty() {
        if (username  == null || username.isBlank() || password == null || password.isBlank()) {
            return true;
        }
        return false;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
