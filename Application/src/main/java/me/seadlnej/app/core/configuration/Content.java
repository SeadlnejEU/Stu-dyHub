package me.seadlnej.app.core.configuration;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.core.configuration.modules.Credentials;
import me.seadlnej.app.core.configuration.modules.Appearance;
import me.seadlnej.app.core.configuration.modules.Credentials;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

import java.util.HashMap;
import java.util.Map;

public class Content extends VBox {

    // Instance
    private static Content instance;

    private Map<Categories, VBox> categories = new HashMap<>();
    public enum Categories { CREDENTIALS, APPEARANCE }

    // Private constructor
    private Content(Region parent) {

        setStyle("-fx-background-color: " + Colors.Primary() + ";");

        setPadding(new Insets(10));
        prefHeightProperty().bind(parent.heightProperty());
        prefWidthProperty().bind(parent.widthProperty().multiply(0.75));

        ThemeManager.onChange(() -> setStyle(getStyle() + "-fx-background-color: " + Colors.Primary() + ";"));

    }

    // Initialize
    public static void init(Region parent) {
        if(instance == null) {
            instance = new Content(parent);
        }
    }

    // Getting instance
    public static Content getInstance() {
        return instance;
    }

    // Switching between settings
    public void update(Categories category) {
        getChildren().setAll(categories.get(category));
    }

    // Refresh UI
    public void refresh() {
        categories.clear();
        System.out.println("refresh");
        categories.put(Categories.APPEARANCE, new Appearance());
        categories.put(Categories.CREDENTIALS, new Credentials());

        // Reset visible content
        update(Categories.APPEARANCE);
    }

}