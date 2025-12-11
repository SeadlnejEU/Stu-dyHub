package me.seadlnej.app.core.authentification.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.providers.TextValue;
import me.seadlnej.app.utilities.JSON;

public class ForgotPassView extends VBox {

    private final ApiHandler api;
    private final ApiHandler codeApi;

    private final VBox eDetails;
    private final VBox cDetails;

    private final HBox eButtons;
    private final HBox cButtons;

    private final Label eError = new Label();
    private final Label cError = new Label();

    // Constructor
    public ForgotPassView(Parent parent) {

        // Default styling
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        VBox.setVgrow(this, Priority.ALWAYS);

        // Api handlers for communication
        api = new ApiHandler();
        codeApi = new ApiHandler();

        // Creating sections of process
        eDetails = createEmailDetailsSection(api);
        cDetails = createCodeDetailsSection(codeApi);

        // Creating buttons
        eButtons = createEmailButton();
        cButtons = createResetButton();

        // Adding everything into box
        getChildren().addAll(eDetails, eButtons);
    }

    // Text input for email detail
    private VBox createEmailDetailsSection(ApiHandler api) {

        // Styling
        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        // Fields
        TextField email = new UIFactory().authField("Email");

        // Error box
        eError.setTextFill(Paint.valueOf("#ff0000"));
        eError.setFont(Font.font("Inter", 10));

        // Putting info into api
        TextValue value = new TextValue(email);
        api.put("email", value);
        codeApi.put("email", value);

        // Adding everything into box
        box.getChildren().addAll(email, eError);
        return box;
    }

    // Text input for code
    private VBox createCodeDetailsSection(ApiHandler api) {

        // Styling
        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        // Fields
        TextField newPassword = new UIFactory().authField("New Password");
        TextField repeatPassword = new UIFactory().authField("Repeat Password");
        TextField codeField = new UIFactory().authField("Verification Code");

        // Error box
        cError.setTextFill(Paint.valueOf("#ff0000"));
        cError.setFont(Font.font("Inter", 10));

        // Putting info into api
        api.put("password1", new TextValue(newPassword)).put("password2", new TextValue(repeatPassword))
                .put("code", new TextValue(codeField)).put("token", null);

        // Adding everything into box
        box.getChildren().addAll(newPassword, repeatPassword, codeField, cError);
        return box;
    }

    // Creates button to request reset
    private HBox createEmailButton() {

        // Styling
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        // Buttons and logic
        Button requestBtn = UIFactory.authBtn("Request reset");
        requestBtn.setOnAction(e -> {

            // Getting api response
            JSON response = api.post("users/password-reset/request", null);
            if(!response.isSuccess()) {
                eError.setText(response.getMessage());
                return;
            }

            // If successfully
            String token = response.get("token");
            codeApi.put("token", token);
            changeContent(cDetails, cButtons);

        });

        // Adding everything into box
        box.getChildren().addAll(requestBtn);
        return box;
    }

    private HBox createResetButton() {

        // Styling
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        // Button and logic
        Button reset = UIFactory.authBtn("Reset Password");

        reset.setOnAction(e -> {

            // Getting api response
            JSON response = codeApi.post("users/password-reset/complete", null);
            if(!response.isSuccess()) {
                cError.setText(response.getMessage());
                return;
            }

            // If reset was successfully
            onResetSuccess();

        });

        // Adding everything into box
        box.getChildren().addAll(reset);
        return box;
    }

    // Change form based on button action
    private void changeContent(Region section, Region buttons) {
        getChildren().setAll(section, buttons);
    }

    // If reset was successfully
    private void onResetSuccess() {
        cError.setText("Password successfully reset.");
    }

}