package me.seadlnej.server.service;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.requests.RegisterRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistrationCode {

    private RegisterRequest request;

    private String code;
    private long timeStamp;
    private final long codeExpiry;
    private final long requestExpiry;

    public RegistrationCode(RegisterRequest request, String code, long now, long codeExpiryMs, long requestExpiryMs) {
        this.request = request;
        this.code = code;
        this.timeStamp = now;
        this.codeExpiry = now + codeExpiryMs;
        this.requestExpiry = now + requestExpiryMs;
    }
    public HashMap<String, Object> canResend() {

        HashMap<String, Object> response = new HashMap<>(); // Api response
        long now = System.currentTimeMillis();

        // Check if registration expired
        if (now > requestExpiry) {
            response.put("message", "Registration expired, please start again.");
            return response;
        }

        // Check cooldown 30s
        if (now - timeStamp < 30000) {
            long remaining = (30000 - (now - timeStamp)) / 1000;
            response.put("message", "Please wait " + remaining + " seconds before resending the code.");
            return response;
        }

        // Everything ok, can resend
        response.put("success", true);
        return response;
    }

    public boolean isActive(String inputCode) {

        long now = System.currentTimeMillis();

        // Code expired
        if (now > requestExpiry || now > codeExpiry) {
            return false;
        }

        return true;
    }

    public boolean isValid(String inputCode) {

        long now = System.currentTimeMillis();

        // Code does not match
        if (!code.equals(inputCode)) {
            return false;
        }

        return true;
    }


    // Getters and Setters
    public RegisterRequest getRequest() { return request; }
    public void setRequest(RegisterRequest request) { this.request = request; }

    public String getCode() { return code; }
    public void setCode(String code, long now) { this.code = code; this.timeStamp = now; }

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(Long timeStamp) { this.timeStamp = timeStamp; }

    public long getCodeExpiry() { return codeExpiry; }

    public long getRequestExpiry() { return requestExpiry; }

}