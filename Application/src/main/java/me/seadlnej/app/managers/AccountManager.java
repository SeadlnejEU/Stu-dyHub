package me.seadlnej.app.managers;

import javafx.scene.image.Image;
import me.seadlnej.app.core.scenes.Authentification;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.resources.ConfigurationSection;
import me.seadlnej.app.resources.Resources;
import me.seadlnej.app.resources.YamlFile;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.io.ByteArrayInputStream;
import java.util.*;

public class AccountManager {

    // Instance
    private static AccountManager instance;

    // Constants
    private Account active;
    private final List<Account> accounts = new ArrayList<>();

    // Private constructor
    private AccountManager() {}

    // Initialization
    public static void init() {
        if (instance == null) {
            instance = new AccountManager();
            instance.load();
        }
    }

    // Getting instance
    public static AccountManager getInstance() {
        return instance;
    }

    // Loading data
    private void load() {

        // Clearing accounts
        accounts.clear();

        // Getting resource and keys
        YamlFile accFile = Resources.getAccounts();
        if (accFile.getSection("Accounts").getKeys(false) == null) return;

        // Section of accounts
        ConfigurationSection section = accFile.getSection("Accounts");

        for (String key : section.getKeys(false)) {
            String token = accFile.getString("Accounts." + key + ".Token");
            if (!SessionHandler.isTokenValid(token)) {
                accFile.set("Accounts." + key, "");
                accFile.save();
                continue;
            }

            // Získanie dát z API podľa tokenu
            ApiHandler api = new ApiHandler();
            api.put("token", token);
            JSON response = api.post("users/me");

            if (!response.isSuccess()) continue;

            String firstname = response.get("firstname");
            String lastname = response.get("lastname");
            String username = response.get("username");
            String base64 = response.get("image");

            Image image;
            if (base64 == null || base64.isBlank()) {
                image = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
            } else {
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                image = new Image(new ByteArrayInputStream(imageBytes));
                if (image.isError()) {
                    image = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
                }
            }

            Account account = new Account(firstname, lastname, username, image, token);
            accounts.add(account);
        }

        // Automatické prihlásenie podľa nastavení
        YamlFile confFile = Resources.getConfiguration();
        String defaultUserToken = confFile.getString("Account.DefaultToken");
        boolean autoLogin = confFile.getBoolean("Account.Autologin", false);

        active = null;
        if (autoLogin && !accounts.isEmpty()) {
            if (accounts.size() == 1) active = accounts.get(0);
            else active = accounts.stream().filter(acc -> acc.getToken().equals(defaultUserToken)).findFirst().orElse(null);
        }

        if (active != null) SessionHandler.getInstance().save(active.getToken());
    }

    public void addAccount(JSON data, boolean stay) {
        String token = data.get("token");

        System.out.println(data + " is stays: " + stay);

        ApiHandler api = new ApiHandler();
        api.put("token", token);

        JSON response = api.post("users/me");
        if (!response.isSuccess()) return;

        String firstname = response.get("firstname");
        String lastname = response.get("lastname");
        String username = response.get("username");
        String base64 = response.get("image");

        Image image;
        if (base64 == null || base64.isBlank()) {
            image = new Image(Objects.requireNonNull(AccountManager.class.getResourceAsStream("/images/user.png")));
        } else {
            image = Base64Converter.base64ToImage(base64);
        }

        Account account = new Account(firstname, lastname, username, image, token);
        active = account;

        System.out.println(active.getUsername());

        if (stay) {
            accounts.add(account);
            YamlFile resource = Resources.getAccounts();
            String path = "Accounts.Account-" + UUID.randomUUID();
            resource.set(path + ".Token", token);
            resource.save();
        }

        SessionHandler.getInstance().save(active.getToken());
        Profile.getInstance().update(active);
    }

    public void refreshAccounts() {

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);

            ApiHandler api = new ApiHandler();
            api.put("token", account.getToken());
            JSON response = api.post("users/me");

            if (!response.isSuccess()) continue;

            String firstname = response.get("firstname");
            String lastname  = response.get("lastname");
            String username  = response.get("username");
            String base64    = response.get("image");

            account.setFirstname(firstname != null ? firstname : "");
            account.setLastname(lastname != null ? lastname : "");
            account.setUsername(username != null ? username : "");

            if (base64 != null && !base64.isBlank()) {
                account.setImage(Base64Converter.base64ToImage(base64));
            } else {
                account.setImage(new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png"))));
            }
        }

        if (active != null) {
            Profile.getInstance().update(active);
        }
    }

    public void removeAccount(Account account) {

        accounts.remove(account);

        YamlFile accFile = Resources.getAccounts();
        ConfigurationSection section = accFile.getSection("Accounts");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                String token = accFile.getString("Accounts." + key + ".Token");
                if (token != null && token.equals(account.getToken())) {
                    accFile.set("Accounts." + key, null);
                    accFile.save();
                    break;
                }
            }
        }

        if (active != null && active.getToken().equals(account.getToken())) {
            active = null;
            SessionHandler.getInstance().save(null);
        }
    }

    public Account getActive() {
        return active;
    }

    public void setActive(Account active) {
        this.active = active;
        SessionHandler.getInstance().save(active.getToken());
        Profile.getInstance().update(active);
    }

    public void clearActive() {
        this.active = null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

}