package me.seadlnej.app.handlers.scene;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.seadlnej.app.core.scenes.Authentification;
import me.seadlnej.app.core.scenes.Communication;
import me.seadlnej.app.handlers.session.SessionHandler;

public class SceneHandler {

    private static SceneHandler instance;

    private final Stage stage;

    private Communication communication;
    private Authentification authentification;

    // Private constructor
    private SceneHandler(Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);
    }

    // Initialize
    public static void init(Stage stage) {
        if (instance == null) {
            instance = new SceneHandler(stage);
        }
    }

    // Establish basic logic
    public void establish() {
        switchOnToken();
        stage.show();
    }

    // Switching methods
    public void switchOnToken() {

        if (SessionHandler.getInstance().isTokenValid()) {

            if(communication == null) { Communication.init(stage); communication = Communication.getInstance(); }

            // Refresh main content
            communication.refresh();
            stage.setScene(communication.getScene());


        } else {
            if(authentification == null) { Authentification.init(stage); authentification = Authentification.getInstance(); }

            // Refresh the auth panel
            authentification.refresh();
            stage.setScene(authentification.getScene());

        }

    }

    // Getters and Setters
    public Stage getStage() { return stage; }
    public static SceneHandler getInstance() { return instance; }
    public Communication getCommunication() { return communication; }
}