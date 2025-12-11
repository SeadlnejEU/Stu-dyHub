package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.managers.contact.Contact;

import java.time.format.DateTimeFormatter;

public class GroupTile extends HBox {

    private final Circle circle;

    public final Label name;
    private final Label message;
    private final Label time;

    // Constructor
    public GroupTile(Contact contact) {

        // Default styling
        setSpacing(10);
        setPadding(new Insets(10));

        // Profile image
        circle = new Circle(20);
        if (contact.getProfilePicture() != null) {
            circle.setFill(new ImagePattern(contact.getProfilePicture()));
        }

        // ---- Name and Message ----
        VBox details = new VBox();
        details.setSpacing(5);

        // Name of contact
        name = new Label(contact.getFirstname() + " " + contact.getLastname());
        name.setFont(Font.font("Inter", 14));
        name.setTextFill(Paint.valueOf(Colors.Header())); // Color and Theme
        ThemeManager.onChange(() -> name.setTextFill(Paint.valueOf(Colors.Header())));

        // Message details
        HBox msgBox = new HBox();
        msgBox.setSpacing(5);
        msgBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(msgBox, Priority.ALWAYS);

        // Message time stamp
        time = new Label(contact.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        time.setFont(Font.font("Inter", 10));
        time.setTextFill(Paint.valueOf("#888888")); // Color

        // Message label
        message = new Label(contact.getLastMessage());
        message.setMaxWidth(200);
        message.setEllipsisString("...");
        message.setWrapText(false);
        message.setTextOverrun(OverrunStyle.ELLIPSIS);
        message.setFont(Font.font("Inter", contact.isMessageRead() ? FontWeight.NORMAL : FontWeight.BOLD, 12));
        message.setTextFill(contact.isMessageRead() ? Paint.valueOf(Colors.Header()) : Paint.valueOf("#FFFFFF"));
        ThemeManager.onChange(() -> message.setTextFill(contact.isMessageRead() ?
                Paint.valueOf(Colors.Header()) : Paint.valueOf("#BBBBBB")));

        msgBox.getChildren().addAll(time, message);
        details.getChildren().setAll(name, msgBox);

        getChildren().addAll(circle, details);

        this.setStyle("-fx-background-radius: 15;");
        this.setOnMouseEntered(e -> setStyle(getStyle() + "-fx-background-color: " + Colors.Secondary() + ";"));
        this.setOnMouseExited(e -> setStyle(getStyle() + "-fx-background-color: transparent ;"));

        this.setOnMouseClicked(e -> {
            System.out.println("Open chat: " + contact.getFirstname());
        });
    }

    public void update(Contact contact) {
        name.setText(contact.getFirstname() + " " + contact.getLastname());
        message.setText(contact.getLastMessage());
        message.setFont(Font.font("Inter", contact.isMessageRead() ? FontWeight.NORMAL : FontWeight.BOLD, 12));
        message.setTextFill(contact.isMessageRead() ? Paint.valueOf("#AAAAAA") : Paint.valueOf("#FFFFFF"));
        time.setText(contact.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }



}