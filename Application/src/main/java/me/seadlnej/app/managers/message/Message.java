package me.seadlnej.app.managers.message;

import javafx.scene.image.Image;

import java.time.LocalDateTime;

public class Message {

    public final String firstname;
    public final String lastname;
    public final Image image;

    public final long senderId;
    public final String text;
    public final LocalDateTime time;

    public Message(String firstname, String lastname, Image image, long senderId,  String text, LocalDateTime time) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.image = image;
        this.senderId = senderId;
        this.text = text;
        this.time = time;
    }
}