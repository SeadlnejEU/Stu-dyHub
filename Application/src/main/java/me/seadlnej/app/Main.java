package me.seadlnej.app;

import javafx.application.Application;
import javafx.stage.Stage;
import me.seadlnej.app.core.scenes.Communication;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.connection.ConnectionHandler;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.resources.Resources;
import me.seadlnej.app.utilities.WsClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main extends Application {

    private static  WsClient wsClient;
    private final String Server = "http://localhost:8080/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {



    }

    @Override
    public void start(Stage stage) throws Exception {

        establish(stage);
    }

    public void establish(Stage stage) throws URISyntaxException {

        // Default load of all YAML files
        Resources.load();
        wsClient = new WsClient("http://localhost:8080/notify");

        SessionHandler.init();

        // Setting up a connection and session of user
        ConnectionHandler.init(Server + "api/");
        SceneHandler.init(stage);
        AccountManager.init();


        // Staring scene to show
        SceneHandler.getInstance().establish();

    }

    public boolean checkConnection() {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(Server)).build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return resp.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }

    }


    public Main getMain() {
        return this;
    }

    public static WsClient getWsClient() {
        return wsClient;
    }
}