package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.core.communication.Content;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

public class ConversationView extends VBox {

    // Instance
    private static ConversationView instance;

    private ConversationView(Region parent) {

        super(10);
        setPadding(new Insets(10));
        prefWidthProperty().bind(parent.widthProperty().multiply(0.75));

        // Background style and theme change color
        setStyle("-fx-background-color: " + Colors.Primary() + "; -fx-border-radius: 4px;" +
                "-fx-background-radius: 4px;");
        ThemeManager.onChange(() -> setStyle("-fx-background-color: " + Colors.Primary() + ";"));

    }

    // Initialization
    public static void init(Region parent) {
        if(instance == null) {
            instance = new ConversationView(parent);
        }
    }

    // Getting instance
    public static ConversationView getInstance() {
        return instance;
    }

    public void setContent(Node node) {
        getChildren().setAll(node);
    }


}