package me.seadlnej.app.core.communication.modules;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.configuration.modules.BadgeBox;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.util.Objects;

public class RequestsView extends BadgeBox {

    // Singleton instance
    private static RequestsView instance;

    private final ApiHandler api = new ApiHandler();

    // ScrollPane a VBox ako člen triedy
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox list = new VBox();

    // Private constructor
    private RequestsView() {
        super("List of friend requests", "Accept or reject friend requests to you.");

        // ScrollPane styling a obsah
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setContent(list);
        addSetting(scrollPane); // pridáme iba raz

        fetch(); // prvotné načítanie

        // WS subscribe pre friend requests
        Main.getWsClient().subscribe("/topic/requests/" + AccountManager.getInstance().getActive().getUsername(),
                message -> Platform.runLater(this::fetch));
    }

    // Inicializácia singletonu
    public static void init() {
        if(instance == null) {
            instance = new RequestsView();
        }
    }

    // Získanie instance
    public static RequestsView getInstance() {
        return instance;
    }

    // Fetchovanie dát z API
    public void fetch() {
        api.getData().clear();

        JSON response = api.post("request/show", SessionHandler.getInstance().getToken());
        if(!response.isSuccess()) {
            getError().setText(response.getMessage());
            return;
        }

        // Clear starých requestov
        list.getChildren().clear();

        for (String key : response.getJson("requests").getData().keySet()) {
            JSON data = response.getJson("requests").getJson(key);
            list.getChildren().add(requestBlock(data));
        }
    }

    // Vytvorenie HBox bloku pre jeden request
    private HBox requestBlock(JSON data) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.ALWAYS);

        // Image
        Image image;
        if (data.get("image").isBlank()) {
            image = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
        } else {
            image = Base64Converter.base64ToImage(data.get("image"));
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
        Circle clip = new Circle(30, 30, 30);
        imageView.setClip(clip);

        // Details VBox
        VBox details = new VBox();
        details.setPrefHeight(60);
        details.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(data.get("firstname") + " " + data.get("lastname"));
        name.setFont(Font.font("Inter", 14));
        name.setTextFill(Color.valueOf(Colors.Header()));
        ThemeManager.onChange(() -> name.setTextFill(Color.valueOf(Colors.Header())));

        HBox buttons = new HBox(3);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button accept = button("Accept", Colors.Online());
        Button reject = button("Reject", Colors.DnD());

        ApiHandler respond = new ApiHandler();
        respond.put("requestId", data.getObj("id"));

        accept.setOnAction(e -> {
            respond.put("accepted", true);
            JSON response = respond.post("request/respond", SessionHandler.getInstance().getToken());
            getError().setText(response.getMessage());
            if(response.isSuccess()) { fetch(); }
        });

        reject.setOnAction(e -> {
            respond.put("accepted", false);
            JSON response = respond.post("request/respond", SessionHandler.getInstance().getToken());
            getError().setText(response.getMessage());
            if(response.isSuccess()) { fetch(); }
        });

        buttons.getChildren().addAll(accept, reject);
        details.getChildren().addAll(name, buttons);
        box.getChildren().addAll(imageView, details);

        return box;
    }

    private Button button(String title, String color) {
        Button button = new Button(title);
        button.setPrefHeight(20);
        button.setTextFill(Paint.valueOf(Colors.Header()));
        button.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        String style = "-fx-background-radius: none; -fx-border-radius: none;";
        button.setStyle(style + "-fx-background-color: " + color + ";");
        return button;
    }
}
