package me.seadlnej.app.managers.contact;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.core.communication.modules.ConversationView;
import me.seadlnej.app.core.scenes.Communication;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.managers.message.MessagesManager;

import java.time.format.DateTimeFormatter;

public class ContactTile extends HBox {

    private final Contact contact;

    private final Circle circle;
    private final Label name;
    private final Label message;
    private final Label time;
    private final HBox statusBox;

    public ContactTile(Contact contact) {

        this.contact = contact;

        // Default styling
        setSpacing(10);
        setPadding(new Insets(10));

        // Profile picture
        circle = new Circle(20);
        if (contact.getProfilePicture() != null) {
            circle.setFill(new ImagePattern(contact.getProfilePicture()));
        }

        // Box for details
        VBox details = new VBox(5);

        // Status and name label
        statusBox = getFormatStatus(contact.getStatus());

        String fullName; // If name is user or group
        if(contact.getType() == ContactType.DIRECT) {
            fullName = contact.getFirstname() + " " + contact.getLastname();
        } else { fullName = contact.getName(); }

        name = new Label(fullName);
        name.setFont(Font.font("Inter", 14));
        name.setTextFill(Paint.valueOf(Colors.Header())); // Color and Theme
        ThemeManager.onChange(() -> name.setTextFill(Paint.valueOf(Colors.Header())));

        // Adding both to nameBox
        HBox nameBox = new HBox(5, statusBox, name);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Time of last message
        time = new Label(contact.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        time.setFont(Font.font("Inter", 10));
        time.setTextFill(Paint.valueOf("#888888"));

        // Settings of message label
        message = new Label(contact.getLastMessage());
        message.setMaxWidth(200);
        message.setEllipsisString("...");
        message.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        message.setFont(Font.font("Inter", contact.isMessageRead() ? FontWeight.NORMAL : FontWeight.BOLD, 12));
        message.setTextFill(contact.isMessageRead() ? Paint.valueOf(Colors.Header()) : Paint.valueOf("#FFFFFF"));
        ThemeManager.onChange(() -> message.setTextFill(contact.isMessageRead() ? Paint.valueOf(Colors.Header()) : Paint.valueOf("#BBBBBB")));

        // Message box, msg and time
        HBox msgBox = new HBox(5, time, message);
        msgBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(msgBox, Priority.ALWAYS);

        // Adding everything into details
        details.getChildren().addAll(nameBox, msgBox);
        getChildren().addAll(circle, details);

        setStyle("-fx-background-radius: 15;");
        setOnMouseEntered(e -> setStyle(getStyle() + "-fx-background-color: " + Colors.Secondary() + ";"));
        setOnMouseExited(e -> setStyle(getStyle() + "-fx-background-color: transparent;"));

        this.setOnMouseClicked(e -> {
            MessagesManager manager = new MessagesManager(contact.getId(), contact.getProfilePicture(),
                    fullName, contact.getStatus(), contact.getType());

            ConversationView.getInstance().setContent(manager.getContainer());

        });

    }

    // Formating status to color dots
    private HBox getFormatStatus(String status) {

        // Main box for dot nad text
        HBox statusHBox = new HBox(5);
        statusHBox.setAlignment(Pos.CENTER_LEFT);

        // Checking if status is null
        if (status == null) { status = ""; }

        String color;
        switch (status.toLowerCase()) {
            case "offline" -> color = Colors.Offline();
            case "away" -> color = Colors.Away();
            case "busy" -> color = Colors.Busy();
            case "dnd" -> color = Colors.DnD();
            default -> color = Colors.Online();
        }

        // Creating circle and translate it
        Circle circle = new Circle(6, Color.valueOf(color));
        circle.setTranslateY(-1);

        // Status label
        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Inter", 12));
        statusLabel.setTextFill(Paint.valueOf(Colors.Description())); // Color and theme
        ThemeManager.onChange(() -> statusLabel.setTextFill(Paint.valueOf(Colors.Description())));

        // Adding everything into box
        if(contact.getType() == ContactType.GROUP) {
            statusHBox.getChildren().addAll(statusLabel);
        } else { statusHBox.getChildren().addAll(circle, statusLabel); }
        return statusHBox;
    }
}