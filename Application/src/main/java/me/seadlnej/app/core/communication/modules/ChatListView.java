package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.communication.Content;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.managers.contact.ContactsManager;

public class ChatListView extends VBox {

    private final HBox header = new HBox();
    private final VBox content = new VBox();
    private final HBox footer = new HBox();

    private final TextField searchBar = new TextField();
    private ContactsManager manager;

    public ChatListView(HBox parent) {

        super(10);
        setPadding(new Insets(10)); // Padding and width of box
        prefWidthProperty().bind(parent.widthProperty().multiply(0.25));

        String style = "-fx-border-radius: 4px; -fx-background-radius: 4px;";
        setStyle(style + "-fx-background-color: " + Colors.Primary());
        ThemeManager.onChange(() -> setStyle(style + "-fx-background-color: " + Colors.Primary() + ";"));

        initUI();

        getChildren().addAll(header, content, footer);

        refresh();

        Main.getWsClient().subscribe("/topic/general/" + AccountManager.getInstance().getActive().getUsername(),
                message -> {
                    showAllContent();
                }
        );
    }

    public void refresh() {
        manager = new ContactsManager();
    }

    public void initUI() {

        initHeader();
        initContent();
        initFooter();
    }

    public void initHeader() {

        header.setSpacing(10);
        header.setPrefHeight(30);
        header.setPadding(new Insets(6));

        String style = "-fx-border-radius: 4; -fx-background-radius: 4;";
        header.setStyle(style + "-fx-background-color: " + Colors.Secondary());
        ThemeManager.onChange(() -> header.setStyle(style + "-fx-background-color: " + Colors.Secondary() + ";"));

        Button all = UIFactory.commChatSwit(header, "All");
        Button friends = UIFactory.commChatSwit(header, "Friends");
        Button groups = UIFactory.commChatSwit(header, "Groups");
        Button tasks = UIFactory.commChatSwit(header, "Tasks");

        all.setOnAction(e -> showAllContent());
        friends.setOnAction(e -> showFriendsContent());
        groups.setOnAction(e -> showGroupsContent());
        tasks.setOnAction(e -> showTasksContent());

        header.getChildren().addAll(all, friends, groups, tasks);
    }

    public void initContent() {
        VBox.setVgrow(content, Priority.ALWAYS);
    }

    public void initFooter() {

        footer.setSpacing(10);
        footer.setPrefHeight(30);
        footer.setPadding(new Insets(4));

        String style = "-fx-border-radius: 15; -fx-background-radius: 15;";
        footer.setStyle(style + "-fx-background-color: " + Colors.Secondary());
        ThemeManager.onChange(() -> footer.setStyle(style + "-fx-background-color: " + Colors.Secondary() + ";"));

        Node node = UIFactory.circleAvatar("fields/magnifier.png", 22);

        searchBar.setPromptText("Filter contacts...");
        searchBar.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;" +
                "-fx-background-color: transparent;"+
                "-fx-text-fill: " + Colors.Description() + ";"
        );

        HBox.setHgrow(searchBar, Priority.ALWAYS);
        searchBar.setPrefHeight(22);

        footer.getChildren().addAll(node, searchBar);
    }

    public Region spacer() {
        Region region = new Region();
        region.setPrefWidth(100);
        return region;
    }

    public void showAllContent() {

        clearContent();

        manager = new ContactsManager();
        VBox containerAll = manager.getContainerAll();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(containerAll);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        content.getChildren().add(scrollPane);
    }

    public void showFriendsContent() {

        clearContent();

        manager = new ContactsManager();
        VBox containerFriends = manager.getContainerDirect();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(containerFriends);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        content.getChildren().add(scrollPane);
    }

    public void showGroupsContent() {

        clearContent();

        manager = new ContactsManager();
        VBox containerGroups = manager.getContainerGroups();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(containerGroups);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        content.getChildren().add(scrollPane);
    }

    public void showTasksContent() {

        clearContent();
        manager = new ContactsManager();
        VBox containerTasks = manager.getContainerDirect();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(containerTasks);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        content.getChildren().add(scrollPane);
    }

    public void clearContent() {
        content.getChildren().clear();
    }

}