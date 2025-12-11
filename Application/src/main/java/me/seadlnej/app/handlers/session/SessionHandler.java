package me.seadlnej.app.handlers.session;

import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.managers.Account;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.resources.Resources;
import me.seadlnej.app.utilities.JSON;

import java.util.ArrayList;
import java.util.List;

public class SessionHandler {

    private static SessionHandler instance;   // SINGLETON

    private String token;
    private final List<Runnable> onChange = new ArrayList<>();
    private final ApiHandler api = new ApiHandler();

    // Constructor
    private SessionHandler() {}

    // Initialize
    public static void init() {
        if (instance == null) {
            instance = new SessionHandler();
        }
    }

    public void save(String token) {
        if(token.isEmpty()) {
            return;
        }
        this.token = token;
        SceneHandler.getInstance().switchOnToken();
    }

    public void clear() {
        token = null;
    }

    public boolean isTokenValid() {

        // Getting active account
        Account active = AccountManager.getInstance().getActive();
        if (active == null) return false;

        // Check if account has token
        String token = active.getToken();
        if (token == null || token.isEmpty()) return false;

        // Sending request to server
        JSON response = api.post("token?token=" + token, null);
        if(response.get("token-validation") == null) { return false; }

        // Returning
        return "valid".equals(response.get("token-validation"));
    }

    // Static check
    public static boolean isTokenValid(String token) {
        if (token == null) { return false; }
        ApiHandler api = new ApiHandler();
        JSON response = api.post("token?token=" + token, null);
        if(response.get("token-validation") == null) { return false; }
        return "valid".equals(response.get("token-validation"));
    }

    public void onChange(Runnable runnable) {
        onChange.add(runnable);
    }

    private void notifyListeners() {
        onChange.forEach(Runnable::run);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public static SessionHandler getInstance() { return instance; }
}
