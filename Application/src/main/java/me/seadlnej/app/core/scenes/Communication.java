package me.seadlnej.app.core.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.seadlnej.app.core.communication.Content;
import me.seadlnej.app.core.modules.TitleBar;
import me.seadlnej.app.core.configuration.Configuration;

public class Communication {

    // Instance
    private static Communication instance;

    // Constants
    private final Scene scene;
    private final BorderPane bPane;

    private final Content content;
    private final Configuration configuration;

    // Private constructor
    private Communication(Stage stage) {

        // Pane and title bar
        bPane = new BorderPane();
        TitleBar.init(stage);

        // Initializing content
        Content.init(bPane);
        content = Content.getInstance();

        // Initializing configuration
        Configuration.init(bPane);
        configuration = Configuration.getInstance();
        configuration.setOnClose(() -> bPane.setCenter(content));

        // Setting center of bPane
        bPane.setCenter(content);
        bPane.setTop(TitleBar.getInstance()); // Setting top of bPane
        scene = new Scene(bPane, 1280, 720);
    }

    // Initialization
    public static void init(Stage stage) {
        if (instance == null) {
            instance = new Communication(stage);
        }
    }

    // Getting instance
    public static Communication getInstance() {
        return instance;
    }

    public void refresh() {
        content.refresh();
        bPane.setCenter(content);
    }

    // Change center of bPane
    public void changeContent(Region region) {
        bPane.setCenter(region);
    }

    // Getters and Setter
    public Scene getScene() { return scene; }

}