package me.seadlnej.app.core.communication.modules;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import me.seadlnej.app.core.configuration.modules.BadgeBox;
import me.seadlnej.app.core.configuration.modules.DetailField;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.Profile;

import java.util.Objects;

public class ShowProfile extends BadgeBox {

    // Constants
    private final String username;
    private final ApiHandler api = new ApiHandler();

    // Constructor
    public ShowProfile(String username) {
        super("","");
        this.username = username;
        fetch();
    }

    // Fetching data from server
    public void fetch() {

        // Clearing api handler
        api.getData().clear();

        // Setting username to json
        api.put("username", username);

        // Getting api response
        JSON response = api.post("profile/extended", SessionHandler.getInstance().getToken());
        if(!response.isSuccess()) {
            System.out.println(response);
            return;
        }

        // Initializing UI of box
        initUI(response);
    }

    // Initializing all UI components
    private void initUI(JSON data) {

        // Setting up badge box's title and description
        getTitle().setText(data.get("firstname") + " " + data.get("lastname") + "'s profile");
        getDescription().setText("User details and status");

        // HBox to hold avatar + fields
        HBox details = new HBox(20);
        details.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle
        Circle avatar = new Circle(60);
        Image image;
        if (data.get("image").isBlank()) { // If image base is null
            image = new Image(Objects.requireNonNull(Profile.class.getResourceAsStream("/images/user.png")));
        } else {
            image = Base64Converter.base64ToImage(data.get("image"));
        }

        // Filling circle with image
        avatar.setFill(new ImagePattern(image));

        // VBox wrapper for details and buttons
        VBox wrapper = new VBox(10);

        // Box for all fields
        HBox fields = new HBox(20);

        // Detail fields
        DetailField usernameField = new DetailField("Username", data.get("username"));
        DetailField emailField = new DetailField("Email", data.get("email"));
        DetailField phoneField = new DetailField("Phone", data.get("phone"));
        DetailField bioField = new DetailField("Bio", data.get("bio"));

        // Make fields non-editable
        usernameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        bioField.setEditable(false);

        // Adding everyhting into fields
        fields.getChildren().addAll(usernameField, emailField, phoneField, bioField);

        // Default styling of box buttons
        HBox buttons = new HBox(20);

        Button send = UIFactory.sendRequ("Send request", Colors.Description());
        Button cancel = UIFactory.sendRequ("Cancel request", Colors.Description());

        // Default hidden
        send.setVisible(false);
        cancel.setVisible(false);

        if((boolean) data.getObj("isFriend")) {
            getError().setText("Already friends");
        } else {
            if((boolean) data.getObj("requestPending")) {
                cancel.setVisible(true);
            } else {
                send.setVisible(true);
            }
        }

        send.setOnAction(e -> {

            api.getData().clear();

            api.put("receiver", username);
            JSON response = api.post("request/send", SessionHandler.getInstance().getToken());

            if(response.isSuccess()) {
                cancel.setVisible(true);
                send.setVisible(false);
            }

            getError().setText(response.getMessage());

        });

        // Adding buttons to box
        buttons.getChildren().addAll(send, cancel);

        // Adding everything into boxes
        wrapper.getChildren().addAll(fields, buttons);
        details.getChildren().addAll(avatar, wrapper);

        // Add HBox to badge box content
        addSetting(details);
    }



}