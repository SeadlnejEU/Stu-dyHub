package me.seadlnej.server.requests.user;

public class PasswordResetRequest {

    private String email;
    private String code;
    private String token;
    private String password1;
    private String password2;

    // Checks if required fields are empty
    public boolean isEmpty() {
        return (email == null || email.isBlank() ||
                token == null || token.isBlank() ||
                password1 == null || password1.isBlank() ||
                password2 == null || password2.isBlank());
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPassword1() { return password1; }
    public void setPassword1(String password1) { this.password1 = password1; }

    public String getPassword2() { return password2; }
    public void setPassword2(String password2) { this.password2 = password2; }
}