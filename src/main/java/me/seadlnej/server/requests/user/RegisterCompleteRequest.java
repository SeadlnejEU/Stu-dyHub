package me.seadlnej.server.requests.user;

public class RegisterCompleteRequest {

    private String code;
    private String token;

    public boolean isEmpty() {
        return (code == null || token == null);
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}