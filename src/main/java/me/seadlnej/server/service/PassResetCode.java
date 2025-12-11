package me.seadlnej.server.service;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.requests.RegisterRequest;

public class PassResetCode {

    private String email;

    private String code;
    private long timeStamp;
    private final long codeExpiry;
    private final long requestExpiry;

    public PassResetCode(String email, String code, long now, long codeExpiryMs, long requestExpiryMs) {
        this.email = email;
        this.code = code;
        this.timeStamp = now;
        this.codeExpiry = now + codeExpiryMs;
        this.requestExpiry = now + requestExpiryMs;
    }
    public ApiResponse canResend() {

        ApiResponse response = new ApiResponse().setSuccess(false); // Api response
        long now = System.currentTimeMillis();

        // Check if registration expired
        if (now > requestExpiry) {
            return response.setData("message", "Registration expired, please start again.");
        }

        // Check cooldown 30s
        if (now - timeStamp < 30000) {
            long remaining = (30000 - (now - timeStamp)) / 1000;
            return response.setData("message", "Please wait " + remaining + " seconds before resending the code.");
        }

        // Everything ok, can resend
        return response.setSuccess(true);
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
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code, long now) { this.code = code; this.timeStamp = now; }

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(Long timeStamp) { this.timeStamp = timeStamp; }

    public long getCodeExpiry() { return codeExpiry; }

    public long getRequestExpiry() { return requestExpiry; }

}