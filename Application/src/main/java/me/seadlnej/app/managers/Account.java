package me.seadlnej.app.managers;

import javafx.scene.image.Image;

public class Account {

    private String firstname;
    private String lastname;
    private String username;
    private Image image;
    private String token;
    private boolean isDefault;

    public Account(String firstname, String lastname, String username, Image image, String token) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.image = image;
        this.token = token;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}