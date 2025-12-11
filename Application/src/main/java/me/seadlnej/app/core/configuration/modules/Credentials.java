package me.seadlnej.app.core.configuration.modules;


import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.managers.Account;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class Credentials extends VBox {

    // Constants
    private BadgeBox mainDetails;
    private BadgeBox subDetails;

    private Circle avatar;
    private Button save;
    private Image chooseImage;
    private final ApiHandler api = new ApiHandler();
    private final Map<String, DetailField> fields = new HashMap<>();

    // Constructor
    public Credentials() {

        // Default styling of box
        super(10);
        setAlignment(Pos.CENTER);

        // Initialize UI
        initUI();

        // Adding everything into box
        getChildren().addAll(mainDetails, subDetails, save);
    }

    public void fetch() {

        // Clearing api handler
        api.getData().clear();

        // Sending api request
        Account active = AccountManager.getInstance().getActive();

        // Getting api respons
        api.put("username", active.getUsername());
        System.out.println(active.getUsername());
        JSON response = api.post("profile/extended", SessionHandler.getInstance().getToken());
        if(!response.isSuccess()) {
            return;
        }

        fields.forEach((key, field) -> {
            String val = (String) response.get(key);
            field.setValue(val == null ? "" : val);
        });

        // Update avatar
        avatar.setFill(new ImagePattern(Profile.getInstance().getProfileImage()));

    }

    // Initializing all UI components
    public void initUI() {

        initMainDetails();
        initSubDetails();
        saveButton();

        fetch();

    }
    // Initializing main details badgeBox UI
    public void initMainDetails() {

        // Default styling of badge box
        mainDetails = new BadgeBox("Accounts and orgs", "Manage your account details.");

        // Creating box for main-fields
        HBox keyBox = new HBox(10, createAvatar(), keyFields());
        keyBox.setAlignment(Pos.CENTER_LEFT);

        // Adding everything into badge box
        mainDetails.getChildren().addAll(keyBox);
    }

    private VBox createAvatar() {

        // Wrapper box and stack pane
        VBox wrapper = new VBox();
        StackPane stack = new StackPane();

        // Defining avatar circle
        avatar = new Circle(75); // Setting picture
        avatar.setFill(new ImagePattern(Profile.getInstance().getProfileImage()));

        // Gray circle for hover effect
        Circle gray = new Circle(75, Color.GRAY);
        gray.setOpacity(0);

        // Edit icon for when avatar has hover effect
        Image editImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
        Circle hover = new Circle(75, new ImagePattern(editImg));
        hover.setOpacity(0);

        // Showing and hiding edit symbol with hover circle
        hover.setOnMouseEntered(e -> {
            gray.setOpacity(0.5);
            hover.setOpacity(1.0);
        });
        hover.setOnMouseExited(e -> {
            gray.setOpacity(0);
            hover.setOpacity(0);
        });

        //
        hover.setOnMouseClicked(e -> select());

        // Adding everything into boxes
        stack.getChildren().addAll(avatar, gray, hover);
        wrapper.getChildren().add(stack);

        // Returning
        return wrapper;
    }

    // Opening file chooser to select new image
    private void select() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose profile image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(SceneHandler.getInstance().getStage());
        if (file != null) {
            try {
                Image img = new Image(new FileInputStream(file));
                avatar.setFill(new ImagePattern(img));
                chooseImage = img;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Creating main details fields for new data
    public HBox keyFields() {

        // Default styling of box
        HBox content = new HBox(25);
        content.setAlignment(Pos.CENTER_LEFT);

        // Creating new fields
        fields.put("firstname", new DetailField("Firstname", ""));
        fields.put("lastname", new DetailField("Lastname", ""));
        fields.put("username", new DetailField("Username", ""));

        // Saving them into hashmap
        content.getChildren().addAll(
                fields.get("firstname"),
                fields.get("lastname"),
                fields.get("username")
        );

        // Returning
        return content;
    }

    public void initSubDetails() {

        // Default styling of badge box
        subDetails = new BadgeBox("Additional details", "Contact details, location, etc.");

        // Creating box for sub-fields
        HBox subBox = new HBox(10, subFields());
        subBox.setAlignment(Pos.CENTER_LEFT);

        // Adding everything into badge box
        subDetails.getChildren().add(subBox);
    }

    private HBox subFields() {

        // Default styling of box
        HBox box = new HBox(25);
        box.setAlignment(Pos.CENTER_LEFT);

        // Creating new fields
        fields.put("email", new DetailField("Email", ""));
        fields.put("phone", new DetailField("Phone", ""));
        fields.put("address", new DetailField("Address", ""));
        fields.put("birthdate", new DetailField("Birthdate", ""));
        fields.put("bio", new DetailField("Bio", ""));

        // Saving them into hashmap
        box.getChildren().addAll(
                fields.get("email"),
                fields.get("phone"),
                fields.get("address"),
                fields.get("birthdate"),
                fields.get("bio")
        );

        // Returning
        return box;
    }

    private void saveButton() {

        // Default styling of button
        save = new Button("Save changes");
        save.setPrefSize(160, 40);

        // Default background color and hover effect color
        String style = "-fx-border-color: transparent;" + "-fx-background-radius: 0;";
        save.setStyle(style + "-fx-background-color: " + Colors.Secondary() + "; " +
                "-fx-text-fill: " + Colors.Header() + ";");
        save.setOnMouseEntered(e -> save.setStyle(style + "-fx-background-color: " +
                Colors.GradientPrimary() + ";")
        );
        save.setOnMouseEntered(e -> save.setStyle(style + "-fx-background-color: " +
                Colors.Secondary() + ";")
        );
        ThemeManager.onChange(() -> save.setStyle(style + "-fx-background-color: " + Colors.Secondary() + "; " +
                "-fx-text-fill: " + Colors.Header() + ";"));

        // Action when clicked
        save.setOnAction(e -> {

            // Clearing api handler
            api.getData().clear();

            // Add all fields
            JSON user = new JSON();
            user.put("firstname", fields.get("firstname").getValue()).put("lastname", fields.get("lastname").getValue())
                    .put("username", fields.get("username").getValue()).put("email", fields.get("email").getValue())
                    .put("phone", fields.get("phone").getValue());

            JSON profile = new JSON();
            profile.put("address", fields.get("address").getValue()).put("birthdate", fields.get("birthdate").getValue())
                    .put("bio", fields.get("bio").getValue());

            if (chooseImage != null) {
                try {
                    String base64 = Base64Converter.imageToBase64(chooseImage);
                    profile.put("image", base64);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            api.put("user", user);
            api.put("profile", profile);

            // Sending api request
            JSON response = api.post("profile/update", SessionHandler.getInstance().getToken());
            if(!response.isSuccess()) {
                fetch();
                return;
            }

            fields.forEach((key, field) -> {
                field.setError(null);

                JSON userData = response.getJson("user");
                if (userData != null) {
                    String errorKey = key + "-message";
                    if (userData.get(errorKey) != null) {
                        field.setError((String) userData.get(errorKey));
                    }
                }

                JSON profileData = response.getJson("profile");
                if (profileData != null) {
                    String errorKey = key + "-message";
                    if (profileData.get(errorKey) != null) {
                        field.setError((String) profileData.get(errorKey));
                    }
                }

            });


        });

    }

}