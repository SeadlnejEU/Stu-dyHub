package me.seadlnej.app.managers.message;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.managers.contact.ContactType;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.*;

public class MessagesManager {

    // Constants
    private final long id;
    private Image image;
    private String title;
    private String status;
    private final ContactType type;

    // UI Constants
    private final VBox container;
    private final HBox header;
    private final VBox content;

    private Circle groupAvatar;
    private TextField groupNameField;
    private TextField groupDescriptionField;
    private Button saveGroupBtn;

    private VBox messagesPane;
    private VBox settingsPane;
    private VBox photosPane;
    private VBox filesPane;
    private VBox tasksPane;

    private ScrollPane messagesScroll;
    private VBox messagesList;

    private HBox inputBox;
    private TextField inputField;

    private VBox usersListContainer;
    private TextField addUserField;
    private Label errorLabel;

    private VBox notificationPopup;

    private final ApiHandler api = new ApiHandler();
    private final List<Message> messages = new ArrayList<>();
    private final Map<Message, MessageTile> tiles = new HashMap<>();

    private String currentTab = "messages";

    // Constructor
    public MessagesManager(Long id, Image image, String title, String status, ContactType type) {

        this.id = id;
        this.image = image;
        this.title = title;
        this.status = status;
        this.type = type;

        container = new VBox();
        header = new HBox();
        content = new VBox(10);

        // Initialize UI
        initUI();

        // WebSocket subscription
        Main.getWsClient().subscribe("/topic/messages/" + id, message -> {

            Platform.runLater(() -> {
                fetchData();
            });

        });
    }

    // Initializing UI
    private void initUI() {

        // Default styling of box
        container.setSpacing(10);
        VBox.setVgrow(container, Priority.ALWAYS);

        // Initialing other UI components
        initHeader();
        initContent();
        initMessages();
        initSettings();
        initPhotos();
        initFiles();
        initTasks();

        // Adding boxes to container
        container.getChildren().addAll(header, content);
        fetchData();

    }

    // Initializing header
    private void initHeader() {

        header.setSpacing(10);
        header.setPadding(new Insets(5));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 4;");
        ThemeManager.onChange(() -> header.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 4;"));

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPreserveRatio(true);
        Circle clip = new Circle(15, 15, 15);
        imageView.setClip(clip);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().add(imageView);

        if (type == ContactType.DIRECT) {
            Label name = new Label(title + " (" + status + ")");
            name.setFont(Font.font("Inter", FontWeight.BOLD, 14));
            header.getChildren().addAll(name, spacer);
        } else if (type == ContactType.GROUP) {

            VBox details = new VBox(5);

            Label name = new Label(title);
            name.setTextFill(Paint.valueOf(Colors.Header()));
            name.setFont(Font.font("Inter", FontWeight.BOLD, 12));

            Label description = new Label("Popis");
            description.setTextFill(Paint.valueOf(Colors.Description()));
            description.setFont(Font.font("Inter", FontWeight.BOLD, 10));

            details.getChildren().addAll(name, description);

            Button messagesBtn = iconButton("/images/chat/messages.png");
            Button settingsBtn = iconButton("/images/chat/gear.png");
            Button photosBtn = iconButton("/images/chat/images.png");
            Button filesBtn = iconButton("/images/chat/files.png");
            Button tasksBtn = iconButton("/images/chat/todo.png");

            messagesBtn.setOnAction(e -> switchTab("messages"));
            settingsBtn.setOnAction(e -> switchTab("settings"));
            photosBtn.setOnAction(e -> switchTab("photos"));
            filesBtn.setOnAction(e -> switchTab("files"));
            tasksBtn.setOnAction(e -> switchTab("tasks"));

            HBox tabs = new HBox(5, messagesBtn, settingsBtn, photosBtn, filesBtn, tasksBtn);
            tabs.setAlignment(Pos.CENTER_RIGHT);
            header.getChildren().addAll(details, spacer, tabs);

        }
    }

    // Initializing content
    private void initContent() {

        content.setSpacing(10);
        VBox.setVgrow(content, Priority.ALWAYS);




    }

    // Initializing messages
    public void initMessages() {

        messagesPane = new VBox(5);
        messagesPane.setPadding(new Insets(5));
        VBox.setVgrow(messagesPane, Priority.ALWAYS);

        messagesList = new VBox(5);
        messagesList.setPadding(new Insets(5));
        VBox.setVgrow(messagesList, Priority.ALWAYS);

        messagesScroll = new ScrollPane(messagesList);
        messagesScroll.setFitToWidth(true);
        messagesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(messagesScroll, Priority.ALWAYS);

        // Input bo
        initFooter();
        messagesPane.getChildren().addAll(messagesScroll, inputBox);


        content.getChildren().add(messagesPane);
    }

    private void initSettings() {

        settingsPane = new VBox(10);
        settingsPane.setPadding(new Insets(10));
        VBox.setVgrow(settingsPane, Priority.ALWAYS);

        // --- GROUP DETAILS UI ---
        VBox groupDetailsBox = new VBox(10);
        groupDetailsBox.setPadding(new Insets(5));

        Label title = new Label("Group settings");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        title.setTextFill(Paint.valueOf(Colors.Header()));

        // Avatar
        StackPane avatarStack = new StackPane();
        groupAvatar = new Circle(50); // default radius
        groupAvatar.setFill(image != null ? new ImagePattern(image) : Paint.valueOf("#CCCCCC"));

        Circle gray = new Circle(50, Color.GRAY);
        gray.setOpacity(0);

        Image editImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
        Circle hover = new Circle(50, new ImagePattern(editImg));
        hover.setOpacity(0);

        hover.setOnMouseEntered(e -> {
            gray.setOpacity(0.5);
            hover.setOpacity(1.0);
        });
        hover.setOnMouseExited(e -> {
            gray.setOpacity(0);
            hover.setOpacity(0);
        });

        hover.setOnMouseClicked(e -> selectGroupImage());

        avatarStack.getChildren().addAll(groupAvatar, gray, hover);

        // Name and Description
        groupNameField = new TextField("Popis");
        groupNameField.setPromptText("Group Name");
        groupNameField.setText(this.title);

        groupDescriptionField = new TextField();
        groupDescriptionField.setPromptText("Description");
        groupDescriptionField.setText(this.status); // alebo description

        saveGroupBtn = new Button("Save changes");
        saveGroupBtn.setOnAction(e -> saveGroupDetails());

        VBox fieldsBox = new VBox(5, groupNameField, groupDescriptionField, saveGroupBtn);
        fieldsBox.setAlignment(Pos.CENTER_LEFT);

        HBox topBox = new HBox(10, avatarStack, fieldsBox);
        topBox.setAlignment(Pos.CENTER_LEFT);

        groupDetailsBox.getChildren().addAll(title, topBox);

        if (type == ContactType.GROUP) {
            Button deleteGroupBtn = new Button("Delete Group");
            deleteGroupBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
            deleteGroupBtn.setOnAction(e -> deleteGroup());

            settingsPane.getChildren().add(deleteGroupBtn);
        }

        // --- ADD USER FIELD + BUTTON ---
        HBox addUserBox = new HBox(5);
        addUserField = new TextField();
        addUserField.setPromptText("Enter username...");
        HBox.setHgrow(addUserField, Priority.ALWAYS);

        Button addUserBtn = new Button("+ Add");
        addUserBtn.setOnAction(e -> addUser());

        addUserBox.getChildren().addAll(addUserField, addUserBtn);

        // --- ERROR / STATUS LABEL ---
        errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("red"));

        // --- USERS SCROLL LIST ---
        usersListContainer = new VBox(5);
        ScrollPane usersScroll = new ScrollPane(usersListContainer);
        usersScroll.setFitToWidth(true);
        VBox.setVgrow(usersScroll, Priority.ALWAYS);

        settingsPane.getChildren().addAll(groupDetailsBox, addUserBox, errorLabel, usersScroll);

        // Load initial users
        Platform.runLater(this::loadUsers);
    }

    private void deleteGroup() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Group");
        confirm.setHeaderText("Are you sure you want to delete this group?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        api.getData().clear();
        api.put("token", SessionHandler.getInstance().getToken());
        api.put("id", id);

        JSON response = api.post("group/delete");

        if (response.isSuccess()) {
            Alert done = new Alert(Alert.AlertType.INFORMATION);
            done.setHeaderText("Group deleted successfully");
            done.show();

            // zavrieme celé okno chatu a refreshneme kontakty
            SceneHandler.getInstance().getCommunication().refresh();

        } else {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setHeaderText("Failed to delete group");
            err.setContentText(response.get("message"));
            err.show();
        }
    }

    // --- Select group image ---
    private void selectGroupImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose group image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(SceneHandler.getInstance().getStage()); // alebo SceneHandler
        if (file != null) {
            try {
                Image img = new Image(new FileInputStream(file));
                groupAvatar.setFill(new ImagePattern(img));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // --- Save group details ---
    private void saveGroupDetails() {
        api.getData().clear();
        api.put("id", id);
        api.put("name", groupNameField.getText());
        api.put("description", groupDescriptionField.getText());

        // Convert avatar to Base64
        if (groupAvatar.getFill() instanceof ImagePattern pattern) {
            try {
                Image img = pattern.getImage();
                String base64 = me.seadlnej.app.utilities.Base64Converter.imageToBase64(img);
                api.put("image", base64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSON response = api.post("group/update", SessionHandler.getInstance().getToken());
        if (response.isSuccess()) {
            errorLabel.setText("Group updated successfully");
            // aktualizovať lokálne premenné
            this.title = groupNameField.getText();
            this.status = groupDescriptionField.getText();
            this.image = ((ImagePattern) groupAvatar.getFill()).getImage();
        } else {
            errorLabel.setText(response.get("message"));
        }
    }

    // --- Load users from server ---
    private void loadUsers() {
        usersListContainer.getChildren().clear();
        errorLabel.setText("");

        api.getData().clear();
        api.put("id", id);
        JSON response = api.post("group/users", SessionHandler.getInstance().getToken());

        if (!response.isSuccess()) {
            errorLabel.setText("Failed to load users");
            return;
        }

        JSON usersJson = response.getJson("users");
        for (String key : usersJson.getData().keySet()) {
            JSON user = usersJson.getJson(key);
            if (user == null) continue;

            String firstname = user.get("firstname");
            String lastname = user.get("lastname");
            String username = user.get("username");

            HBox userBox = new HBox(10);
            userBox.setPadding(new Insets(5));
            userBox.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(firstname + " " + lastname + " (" + username + ")");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Button removeBtn = new Button("Remove");
            removeBtn.setOnAction(e -> removeUser(username));

            userBox.getChildren().addAll(nameLabel, removeBtn);
            usersListContainer.getChildren().add(userBox);
        }
    }

    // --- Add user ---
    private void addUser() {
        String username = addUserField.getText().trim();
        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty");
            return;
        }

        api.getData().clear();
        api.put("token", SessionHandler.getInstance().getToken());
        api.put("id", id);
        api.put("username", username);

        JSON response = api.post("group/add");
        if (response.isSuccess()) {
            addUserField.clear();
            errorLabel.setText("User added successfully");
            loadUsers();
        } else {
            errorLabel.setText(response.get("message"));
        }
    }

    // --- Remove user ---
    private void removeUser(String username) {

        api.getData().clear();
        api.put("token", SessionHandler.getInstance().getToken());
        api.put("id", id);
        api.put("username", username);

        JSON response = api.post("group/remove");
        if (response.isSuccess()) {
            errorLabel.setText("User removed successfully");
            loadUsers();
        } else {
            errorLabel.setText(response.get("message"));
        }
    }

    private void initPhotos() {

        // Default styling
        photosPane = new VBox(10);
        photosPane.setPadding(new Insets(10));
        VBox.setVgrow(photosPane, Priority.ALWAYS);

        Label title = new Label("Group's images");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        title.setTextFill(Paint.valueOf(Colors.Header()));

        photosPane.getChildren().add(title);
    }

    private void initFiles() {

        // Default styling
        filesPane = new VBox(10);
        filesPane.setPadding(new Insets(10));
        VBox.setVgrow(filesPane, Priority.ALWAYS);

        Label title = new Label("Group's files");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        title.setTextFill(Paint.valueOf(Colors.Header()));

        filesPane.getChildren().add(title);
    }

    private void initTasks() {

        // Default styling
        tasksPane = new VBox(10);
        tasksPane.setPadding(new Insets(10));
        VBox.setVgrow(tasksPane, Priority.ALWAYS);

        Label title = new Label("All group's tasks for users ");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        title.setTextFill(Paint.valueOf(Colors.Header()));

        tasksPane.getChildren().add(title);
    }

    public void switchTab(String tabName) {
        content.getChildren().clear(); // vyčistíme starý obsah

        switch (tabName.toLowerCase()) {
            case "messages" -> content.getChildren().add(messagesPane);
            case "settings" -> content.getChildren().add(settingsPane);
            case "photos" -> content.getChildren().add(photosPane);
            case "files" -> content.getChildren().add(filesPane);
            case "tasks" -> content.getChildren().add(tasksPane);
            default -> content.getChildren().add(messagesPane); // default je messages
        }

        currentTab = tabName.toLowerCase();
    }

    public void fetchData() {
        if (messagesList == null) return; // ak ešte nie je initMessages volané

        messagesList.getChildren().clear();
        tiles.clear();
        messages.clear();

        api.getData().clear();
        api.put("id", id);
        JSON response = api.post("chat/messages", SessionHandler.getInstance().getToken());

        if (!response.isSuccess()) return;

        JSON messagesJson = response.getJson("messages");
        List<String> keys = new ArrayList<>(messagesJson.getData().keySet());
        keys.sort(Comparator.comparingInt(k -> Integer.parseInt(k.split("-")[1])));

        for (String key : keys) {
            JSON message = messagesJson.getJson(key);
            if (message == null) continue;

            Image img;
            if (message.get("image") == null || message.get("image").isEmpty()) {
                img = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
            } else {
                try {
                    byte[] imgData = Base64.getDecoder().decode(message.get("image"));
                    img = new Image(new ByteArrayInputStream(imgData));
                } catch (Exception e) {
                    img = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
                }
            }

            long senderId = ((Number) message.getObj("senderId")).longValue();
            LocalDateTime time = LocalDateTime.parse(message.get("sentAt"));

            Message msgObj = new Message(
                    message.get("firstname"),
                    message.get("lastname"),
                    img,
                    senderId,
                    message.get("text"),
                    time
            );

            MessageTile tile = new MessageTile(
                    message.get("firstname"),
                    message.get("lastname"),
                    img,
                    message.get("text"),
                    time,
                    (boolean) message.getObj("isOwn"),
                    () -> deleteMessage((Number) message.getObj("senderId"), (Number) message.getObj("id"))
            );

            tiles.put(msgObj, tile);
            messagesList.getChildren().add(tile);

        }

        Platform.runLater(() -> messagesScroll.setVvalue(1.0));
    }

    private void sendMessage() {
        if (inputField == null || inputField.getText().isBlank()) return;

        String text = inputField.getText();
        inputField.clear();

        api.getData().clear();
        api.put("id", id);
        api.put("type", type);
        api.put("text", text);
        api.put("mediaId", 0);
        api.put("sentAt", LocalDateTime.now().toString());

        JSON response = api.post("chat/send", SessionHandler.getInstance().getToken());
        System.out.println(response);
        fetchData();
    }

    private void deleteMessage(Number senderId, Number messageId) {
        api.getData().clear();
        api.put("conversationId", id);
        api.put("messageId", messageId);
        JSON response = api.post("chat/delete", SessionHandler.getInstance().getToken());
        System.out.println(response);
        fetchData();
    }






    private void initFooter() {
        inputBox = new HBox(5);
        inputBox.setPadding(new Insets(5));
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setSpacing(5);
        inputBox.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 5;");

        inputField = new TextField();
        inputField.setPromptText("Write a message...");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendBtn = new Button("➤");
        sendBtn.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(inputField, sendBtn);
        VBox.setVgrow(inputBox, Priority.NEVER);
    }

    // Header button style
    public Button iconButton(String imagePath) {

        // Loading image from resources
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPreserveRatio(true);

        // Creating button object
        Button button = new Button();
        button.setGraphic(imageView);

        // Removing default style
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-width: 0;");
        button.setFocusTraversable(false);

        // Returning
        return button;
    }

    public VBox getContainer() {
        return container;
    }


}
