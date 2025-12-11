package me.seadlnej.app.core.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.scenes.Communication;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.SceneTypes;

public class TitleBar extends HBox {

    private double xOffset = 0;
    private double yOffset = 0;

    private static boolean isMaximized = false;
    private static double prevX, prevY, prevWidth, prevHeight;

    private static TitleBar instance;

    private TitleBar(Stage stage) {

        initUI(stage);

    }

    public static void init(Stage stage) {
        if (instance == null) { instance = new TitleBar(stage); }
    }

    public static TitleBar getInstance() { return instance; }

    private void initUI(Stage stage) {

        // Default content style
        setStyle("-fx-background-color: " + Colors.Bar() + ";");

        // Split the bar into two sections:
            //Left section
        HBox left = new HBox(5);
        left.setPadding(new Insets(10));
        left.setAlignment(Pos.CENTER_LEFT);

        Button minimize = UIFactory.barFuncBtn("-");
        Button maximize = UIFactory.barFuncBtn("[]");
        Button close = UIFactory.barFuncBtn("X");


            //Right section
        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS); // Pushies left bar to left


        right.getChildren().addAll(minimize, maximize, close);


        // Disables default title bar given by OS and styling
        setStyle("-fx-background-color: " + Colors.Bar() + ";");

        minimize.setOnAction(e -> stage.setIconified(true));
        maximize.setOnAction(e -> maximize(stage));
        close.setOnAction(e -> stage.close());

        getChildren().addAll(left, right);

        // Mouse dragging and clicking
        setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        setOnMouseDragged((MouseEvent e) -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            }
        });

        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });

        //
        ThemeManager.onChange(() -> {
            setStyle("-fx-background-color: " + Colors.Bar() + ";");
        });

    }

    public void maximize(Stage stage) {

        if(!isMaximized) {

            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();

            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());

            isMaximized = true;

        } else {

            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);

            isMaximized = false;

        }

    }

}