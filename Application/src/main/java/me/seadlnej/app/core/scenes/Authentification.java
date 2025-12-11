package me.seadlnej.app.core.scenes;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import me.seadlnej.app.core.authentification.AccountsPanel;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.managers.AccountManager;

public class Authentification {

    // Constants
    private final Scene scene;
    private final BorderPane root;
    private final AccountsPanel panel;

    private double offsetX;
    private double offsetY;

    private boolean isMaximized = false;
    private static final int BORDER = 6;
    private double prevX, prevY, prevWidth, prevHeight;

    // Instance
    private static Authentification instance;

    // Private constructor
    private Authentification(Stage stage) {

        // Defining and styling root
        root = new BorderPane();
        root.setPrefSize(1280, 720);
        root.setStyle("-fx-border-width: 1; -fx-border-radius: 8;" +
                "-fx-background-radius: 8; -fx-border-color: #323232;" +
                "-fx-background-color: " + Colors.LinearGradient() + ";");

        root.setTop(titleBar(stage));

        HBox wrapper = new HBox(20);
        wrapper.setPadding(new Insets(20));

        panel = new AccountsPanel(root);
        wrapper.getChildren().addAll(panel);
        root.setCenter(wrapper);

        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

    }

    // Initialization
    public static void init(Stage stage) {
        if(instance == null) { instance = new Authentification(stage); }
    }


    // Custom authentication title bar
    public HBox titleBar(Stage stage) {

        // Defining box and style
        HBox box = new HBox();
        box.setPrefHeight(35);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setStyle("-fx-background-color: transparent;");

        // UIFactory creation of buttons
        Button minimize = UIFactory.authBarFunc("\uD83D\uDDD5");
        Button maximize = UIFactory.authBarFunc("\uD83D\uDDD6");
        Button close = UIFactory.authBarClose("\uD83D\uDDD9");

        // Minimize
        minimize.setOnAction(e -> stage.setIconified(true));
        // Maximize / Restore
        maximize.setOnAction(e -> setMaximized(stage));
        // Close
        close.setOnAction(e -> stage.close());

        // Double clicking maximize and minimize
        box.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                setMaximized(stage);
            }
        });

        // Dragging scene around desktop
        box.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                offsetX = e.getSceneX(); // Offset inside scene itself
                offsetY = e.getSceneY();
            }
        });

        // Moving scene while dragging
        box.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - offsetX); // Where is mouse on screen
                stage.setY(e.getScreenY() - offsetY); // and offset of mouse in scene
            }
        });

        // Adding everything into box
        box.getChildren().addAll(minimize, maximize, close);
        return box;
    }

    // Maximizing and minimizing scene
    public void setMaximized(Stage stage) {

        // If scene is maximized
        if(!isMaximized) {

            // Saving actual coordinates and dimension
            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();

            // Setting new dimensions
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            // Removing border
            root.setStyle(root.getStyle() + "-fx-border-width: 0; -fx-border-radius: 0;" +
                    "-fx-background-radius: 0; -fx-border-color: transparent;");

            isMaximized = true; // Now its maximized
        } else {

            // Setting previous values
            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);

            // Adding border
            root.setStyle(root.getStyle() + "-fx-border-width: 1; -fx-border-radius: 8;" +
                    "-fx-background-radius: 8; -fx-border-color: #323232;");

            isMaximized = false; // Now it is not maximized
        }
    }

    public void refresh() {
        AccountManager.getInstance().refreshAccounts();
        panel.refresh();
    }

    // Getter and Setter
    public Scene getScene() { return scene; }

    public static Authentification getInstance() { return instance; }

    public AccountsPanel getPanel() { return panel; }
}