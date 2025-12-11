package me.seadlnej.app.core.communication.modules;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.configuration.modules.BadgeBox;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.utilities.JSON;

public class NotificationsView extends BadgeBox {

    // Instance
    private static NotificationsView instance;

    // Constants
    private final VBox list = new VBox();
    private final ScrollPane scrollPane = new ScrollPane();
    private final ApiHandler api = new ApiHandler();

    // Private constructor
    private NotificationsView() {
        super("List of all notifications", "Check out your new messages, tasks or friend requests.");

        // ScrollPane styling a content
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setContent(list);

        // Adding scroll pane as badge box setting
        addSetting(scrollPane);

        // First time loading data
        fetch();

        // WsClient for subscribe
        Main.getWsClient().subscribe("/topic/notifications/" + AccountManager.getInstance().getActive().getUsername(),
                message -> { fetch(); });
        }

    // Initialization
    public static void init() {
        if(instance == null) {
            instance = new NotificationsView();
        }
    }

    // Getting instance
    public static NotificationsView getInstance() {
        return instance;
    }

    // Fetching data from server
    public void fetch() {

        // Clearing api handler
        api.getData().clear();

        // Getting api response
        JSON response = api.post("notifications/show", SessionHandler.getInstance().getToken());

        // Setting erro message
        if(!response.isSuccess()) {
            getError().setText(response.getMessage());
            return;
        }

        // Clearing old notifications
        list.getChildren().clear();

        // Going through list
        for (String key : response.getJson("notifications").getData().keySet()) {
            JSON data = response.getJson("notifications").getJson(key);
            list.getChildren().add(notifBlock(data));
        }
    }

    // Creating box for notification
    private HBox notifBlock(JSON data) {

        // Default styling of box
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.ALWAYS);

        VBox details = new VBox();
        details.setPrefHeight(60);
        details.setAlignment(Pos.CENTER_LEFT);

        // Type of notification label
        Label type = new Label(data.get("type"));
        type.setFont(Font.font("Inter", 14));
        type.setTextFill(Color.valueOf(Colors.Header()));
        ThemeManager.onChange(() -> type.setTextFill(Color.valueOf(Colors.Header())));

        // Message of notification
        Label message = new Label(data.get("message"));
        message.setFont(Font.font("Inter", 12));
        message.setTextFill(Color.valueOf(Colors.Description()));
        ThemeManager.onChange(() -> message.setTextFill(Color.valueOf(Colors.Description())));

        // Date of notification creation
        Label created = new Label(data.get("createdAt"));
        created.setFont(Font.font("Inter", 12));
        created.setTextFill(Color.valueOf(Colors.Description()));
        ThemeManager.onChange(() -> created.setTextFill(Color.valueOf(Colors.Description())));

        // Adding everything into boxes
        details.getChildren().addAll(type, message);
        box.getChildren().addAll(details, linedSpacer(), created);

        return box;
    }

    // Lined spacer pre oddeľovanie
    private HBox linedSpacer() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.NEVER);

        box.setPrefWidth(1);
        box.setPrefHeight(30);
        box.setMinWidth(1);
        box.setMinHeight(30);
        box.setMaxWidth(1);
        box.setMaxHeight(30);

        box.setStyle("-fx-background-color: " + Colors.Description() + ";");
        HBox.setMargin(box, new Insets(5));

        return box;
    }

}
