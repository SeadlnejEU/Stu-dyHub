package me.seadlnej.app.core.configuration.modules;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

public class BadgeBox extends VBox {

    private Label title;
    private Label description;
    private final VBox content;
    private Label error;

    public BadgeBox(String titleText, String descriptionText) {

        // Default styling of box
        setPadding(new Insets(10));
        setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 10;");

        // Defining and styling title
        title = new Label(titleText);
        title.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        title.setTextFill(Paint.valueOf(Colors.Header()));

        // Defining and styling description
        description = new Label(descriptionText);
        description.setFont(Font.font("Inter", 12));
        description.setTextFill(Paint.valueOf(Colors.Description()));

        // Defining and designing content box
        content = new VBox();
        content.setPadding(new Insets(5));

        // Defining and styling error
        error = new Label("");
        error.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        error.setTextFill(Paint.valueOf(Colors.Header()));

        // Theme change colors
        ThemeManager.onChange(() -> {
            title.setTextFill(Paint.valueOf(Colors.Header()));
            description.setTextFill(Paint.valueOf(Colors.Description()));
            setStyle(getStyle() + "-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 10;");
        });

        // Adding everything into box
        getChildren().addAll(title, description, content, error);
    }

    public void addSetting(Node node) {
        content.getChildren().add(node);
    }

    // Getters and Setters

    public VBox getContent() {
        return content;
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public Label getDescription() {
        return description;
    }

    public void setDescription(Label description) {
        this.description = description;
    }

    public Label getError() {
        return error;
    }

    public void setError(Label error) {
        this.error = error;
    }

}