package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import me.seadlnej.app.core.configuration.modules.BadgeBox;
import me.seadlnej.app.core.configuration.modules.DetailField;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class AddGroupView extends VBox {

    private Button create;
    private byte[] imageData; // Changed from Image to byte[]
    private Circle avatarCircle;
    private final VBox container = new VBox();
    private final ApiHandler api = new ApiHandler();
    private final Map<String, DetailField> fields = new HashMap<>();
    private final Map<String, String> users = new HashMap<>(); // Stores usernames

    public AddGroupView() {
        super(10);
        initUI();
    }

    // Initializing style
    public void initUI() {
        container.setSpacing(10);
        container.setPadding(new Insets(10));
        HBox.setHgrow(container, Priority.ALWAYS);
        container.setStyle("-fx-background-color: " + Colors.Primary() + "; -fx-background-radius: 30;");
        ThemeManager.onChange(() -> container.setStyle("-fx-background-color: " + Colors.Primary() + "; -fx-background-radius: 30;"));

        createCreateButton();
        container.getChildren().addAll(badgeBox(), create);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        getChildren().addAll(container);
    }

    // Primary badgeBox
    private BadgeBox badgeBox() {
        BadgeBox badgeBox = new BadgeBox("Create group", "Create your own group for your friends");
        setAlignment(Pos.CENTER);

        HBox detailsBox = new HBox(10, createAvatar(), createDetailFields(), createUserListBox());
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        badgeBox.getChildren().add(detailsBox);
        return badgeBox;
    }

    // Avatar selection
    private VBox createAvatar() {
        VBox wrapper = new VBox();
        StackPane stack = new StackPane();

        avatarCircle = new Circle(75);
        avatarCircle.setFill(new ImagePattern(Profile.getInstance().getProfileImage()));

        Circle gray = new Circle(75, Color.GRAY);
        gray.setOpacity(0);

        Image editImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
        Circle hover = new Circle(75, new ImagePattern(editImg));
        hover.setOpacity(0);

        hover.setOnMouseEntered(e -> {
            gray.setOpacity(0.5);
            hover.setOpacity(1.0);
        });
        hover.setOnMouseExited(e -> {
            gray.setOpacity(0);
            hover.setOpacity(0);
        });

        hover.setOnMouseClicked(e -> selectAvatar());

        stack.getChildren().addAll(avatarCircle, gray, hover);
        wrapper.getChildren().add(stack);

        return wrapper;
    }

    // File chooser for avatar
    private void selectAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose group image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(SceneHandler.getInstance().getStage());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }
                imageData = bos.toByteArray();
                Image image = new Image(new FileInputStream(file));
                avatarCircle.setFill(new ImagePattern(image));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Fields for name and description
    private HBox createDetailFields() {
        HBox box = new HBox(25);
        box.setAlignment(Pos.CENTER_LEFT);

        fields.put("name", new DetailField("Name", ""));
        fields.put("description", new DetailField("Description", ""));

        box.getChildren().addAll(fields.get("name"), fields.get("description"));
        return box;
    }

    // User list with add/remove buttons
    private VBox createUserListBox() {
        VBox usersBox = new VBox(5);
        usersBox.setPadding(new Insets(5));

        Label title = new Label("Users");
        usersBox.getChildren().add(title);

        TextField addField = new TextField();
        addField.setPromptText("Add user");

        Button addBtn = new Button("+");
        addBtn.setOnAction(e -> {
            String username = addField.getText().trim();
            if (!username.isEmpty() && !users.containsKey(username)) {
                users.put(username, "added");
                addField.clear();
                refreshUserList(usersBox);
            }
        });

        TextField removeField = new TextField();
        removeField.setPromptText("Remove user");

        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> {
            String username = removeField.getText().trim();
            if (!username.isEmpty() && users.containsKey(username)) {
                users.remove(username);
                removeField.clear();
                refreshUserList(usersBox);
            }
        });

        HBox addBox = new HBox(5, addField, addBtn);
        HBox removeBox = new HBox(5, removeField, removeBtn);

        usersBox.getChildren().addAll(addBox, removeBox);
        return usersBox;
    }

    private void refreshUserList(VBox usersBox) {
        // Remove all labels except first 3 nodes (title + input boxes)
        usersBox.getChildren().removeIf(node -> VBox.getVgrow(node) == null && !(node instanceof HBox));

        // Add current users
        for (String username : users.keySet()) {
            Label label = new Label(username);
            usersBox.getChildren().add(label);
        }
    }

    // Create group button
    private void createCreateButton() {
        create = new Button("Create group");
        create.setPrefSize(160, 40);

        create.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-border-color: transparent;" +
                "-fx-background-radius: 0;" + "-fx-text-fill: " + Colors.Header() + ";");

        create.setOnMouseEntered(e ->
                create.setStyle(create.getStyle() + "-fx-background-color: " + Colors.GradientPrimary() + ";")
        );

        create.setOnMouseExited(e ->
                create.setStyle(create.getStyle() + "-fx-background-color: " + Colors.Secondary() + ";")
        );

        ThemeManager.onChange(() -> create.setStyle("-fx-background-color: " + Colors.Secondary() + ";" +
                "-fx-text-fill: " + Colors.Header() + ";"));

        create.setOnAction(e -> {
            ApiHandler upload = new ApiHandler();

            if (imageData != null) {
                try {
                    String base64 = Base64.getEncoder().encodeToString(imageData);
                    upload.put("image", base64);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            upload.put("name", fields.get("name").getValue());
            upload.put("description", fields.get("description").getValue());
            upload.put("users", users); // Send users map

            JSON response = upload.post("group/create", SessionHandler.getInstance().getToken());
            System.out.println(response);

            fields.get("name").setError(response.getMessage());
        });
    }
}
