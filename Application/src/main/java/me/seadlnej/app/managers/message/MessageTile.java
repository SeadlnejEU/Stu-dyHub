package me.seadlnej.app.managers.message;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MessageTile extends HBox {

    private final Circle circle;
    private final Label name;
    private final Label tLabel;
    private final Label mLabel;
    private final Button deleteButton;

    public MessageTile(String firstname, String lastname, Image image, String message,
                       LocalDateTime time, boolean isOwnMessage, Runnable onDelete) {

        this.setSpacing(5);
        this.setPadding(new Insets(5));

        if (image.isError()) {
            image = new Image(Objects.requireNonNull(MessageTile.class.getResourceAsStream("/images/user.png")));
        }

        circle = new Circle(20);
        circle.setFill(new ImagePattern(image));

        name = new Label(firstname + " " + lastname);
        name.setStyle("-fx-font-weight: bold;");

        tLabel = new Label(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        tLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        mLabel = new Label(message);
        mLabel.setWrapText(true);
        mLabel.setTextFill(Paint.valueOf(Colors.Header()));
        mLabel.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-padding: 5 10 5 10; -fx-background-radius: 8;");
        ThemeManager.onChange(() -> {
            mLabel.setTextFill(Paint.valueOf(Colors.Header()));
            mLabel.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-padding: 5 10 5 10; -fx-background-radius: 8;");
        });

        VBox textBox = new VBox(name, mLabel, tLabel);
        textBox.setSpacing(2);

        if (isOwnMessage) {
            // Delete button
            deleteButton = new Button("❌");
            deleteButton.setStyle("-fx-background-radius: 5; -fx-font-size: 10;");
            deleteButton.setOnAction(e -> {
                if (onDelete != null) Platform.runLater(onDelete);
            });

            HBox messageBox = new HBox(textBox, deleteButton);
            messageBox.setSpacing(5);
            messageBox.setAlignment(Pos.CENTER_RIGHT);

            this.setAlignment(Pos.TOP_RIGHT);
            this.getChildren().addAll(messageBox, circle);
        } else {
            deleteButton = null;
            this.setAlignment(Pos.TOP_LEFT);
            this.getChildren().addAll(circle, textBox);
        }
    }

}
