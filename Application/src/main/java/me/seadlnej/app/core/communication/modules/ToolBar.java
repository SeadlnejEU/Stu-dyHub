package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import me.seadlnej.app.core.communication.Content;
import me.seadlnej.app.core.configuration.Configuration;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.Profile;

import java.util.Objects;

public class ToolBar extends HBox {

    private final HBox profile;
    private final TextField searchBar;
    private Button requests;
    private Button group;
    private Button notifications;
    private final Button configuration;

    private Runnable onConfiguration;

    // Constructor
    public ToolBar(Content parent) {

        // Default styling of box
        super(5);
        setPrefHeight(50);
        setPadding(new Insets(5));
        HBox.setHgrow(this, Priority.ALWAYS);

        // Styling background and corner radius of box
        String style = "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: " + Colors.Primary();
        setStyle(style); ThemeManager.onChange(() -> setStyle(style));

        // Initializing final constant
        profile = Profile.getInstance().getProfileBox();
        searchBar = new TextField();
        configuration = UIFactory.settingsButton();

        // Initializing UI of box
        initUI();

        // Adding everything into box;
        getChildren().addAll(profile, linedSpacer(), searchBar, linedSpacer(), requests, group, notifications, spacer(), configuration);

    }

    // Initializing all UI components
    public void initUI() {
        initSearchBar();
        initButtons();
        configuration.setOnAction(e -> {
            Region content = Configuration.getInstance();
            SceneHandler.getInstance().getCommunication().changeContent(content);
        });
    }

    // Initializing search bar UI
    public void initSearchBar() {

        // Dimensions
        searchBar.setPrefHeight(30);
        searchBar.setPrefWidth(300);

        searchBar.setPromptText("Search for friends, groups etc."); // Text and margin
        HBox.setMargin(searchBar, new Insets(5, 0, 5, 0));

        // Styling background and corner radius of bar
        String style = "-fx-border-radius: 15; -fx-background-radius: 15;" +
                "-fx-background-color: " + Colors.Secondary() + "; -fx-text-fill: " + Colors.Description() + ";";
        searchBar.setStyle(style); ThemeManager.onChange(() -> searchBar.setStyle(style));

        searchBar.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                String username = searchBar.getText().trim();
                if (!username.isEmpty()) {
                    ShowProfile prof = new ShowProfile(username);
                    ConversationView.getInstance().setContent(prof);
                }
            }
        });

    }

    public void initButtons() {

        requests = UIFactory.toolBarBtn("/images/requests.png");
        group = UIFactory.toolBarBtn("/images/group.png");
        notifications = UIFactory.toolBarBtn("/images/bell.png");

        requests.setOnAction(e -> {
            RequestsView.init();
            ConversationView.getInstance().setContent(RequestsView.getInstance());
        });

        group.setOnAction(e -> {
            AddGroupView addGroup = new AddGroupView();
            ConversationView.getInstance().setContent(addGroup);
        });

        notifications.setOnAction(e -> {
            NotificationsView.init();
            ConversationView.getInstance().setContent(NotificationsView.getInstance());
        });

    }

    // Spacer for space between components
    public Region spacer() {

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        return region;
    }

    // Spacer with line between tools
    public HBox linedSpacer() {

        // Styling
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.NEVER);

        // Setting dimensions
        box.setPrefWidth(1);
        box.setPrefHeight(30);
        box.setMinWidth(1);
        box.setMinHeight(30);
        box.setMaxWidth(1);
        box.setMaxHeight(30);

        // Lines color
        box.setStyle("-fx-background-color: " + Colors.Description() + ";");

        // Margin for spacing tools
        HBox.setMargin(box, new Insets(5));

        return box;
    }

    // Getters and Setters
    public void setOnConfiguration(Runnable callback) {
        this.onConfiguration = callback;
    }

}