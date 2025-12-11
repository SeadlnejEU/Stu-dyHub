package me.seadlnej.app.core.authentification.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.providers.TextValue;
import me.seadlnej.app.utilities.JSON;

public class SignUpView extends VBox {

    private final VBox pDetails;
    private final VBox sDetails;
    private final VBox cDetails;

    private final HBox pButtons;
    private final HBox sButtons;
    private final HBox cButtons;

    private final ApiHandler api;
    private final ApiHandler codeApi;

    private final Label error = new Label();
    private final Label timer = new Label();

    // Constructor
    public SignUpView(Parent parent) {

        // Default styling
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        VBox.setVgrow(this, Priority.ALWAYS);

        // Api handlers for communication
        api = new ApiHandler();
        codeApi = new ApiHandler();

        // Creating sections of process
        pDetails = createPersonalDetailsSection(api);
        sDetails = createContactDetailsSection(api);
        cDetails = createCodeVerificationSection(codeApi);

        // Creating buttons
        pButtons = createPersonalButtons();
        sButtons = createContactButtons();
        cButtons = createCodeButtons();

        // Adding everything into box
        getChildren().addAll(pDetails, pButtons);
    }

    // Primary account details
    private VBox createPersonalDetailsSection(ApiHandler api) {

        // Styling
        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        // Fields
        TextField firstname = new TextField();
        TextField lastname = new TextField();
        TextField username = new TextField();

        // Creating designed fields with icon
        HBox firstnameIconed = UIFactory.authFieldWithIcon(firstname,
                "Firstname", "");
        HBox lastnameIconed = UIFactory.authFieldWithIcon(lastname,
                "Lastname", "");
        HBox usernameIconed = UIFactory.authFieldWithIcon(username,
                "Username", "/images/fields/user.png");

        // Putting info into api
        api.put("firstname", new TextValue(firstname)).put("lastname", new TextValue(lastname))
                .put("username", new TextValue(username));

        // Adding everything into box
        box.getChildren().addAll(firstnameIconed, lastnameIconed, usernameIconed);
        return box;
    }

    // Box of account details
    private VBox createContactDetailsSection(ApiHandler api) {

        // Styling
        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        // Fields
        TextField email = new TextField();
        TextField phone = new TextField();
        PasswordField password1 = new PasswordField();
        PasswordField password2 = new PasswordField();

        // Creating designed fields with icon
        HBox emailIconed = UIFactory.authFieldWithIcon(email,
                "Email", "/images/fields/email.png");
        HBox phoneIconed = UIFactory.authFieldWithIcon(phone,
                "Phone", "/images/fields/phone.png");
        HBox pass1Iconed = UIFactory.authFieldWithIcon(password1,
                "Enter password", "/images/fields/padlock.png");
        HBox pass2Iconed = UIFactory.authFieldWithIcon(password2,
                "Repeat password", "/images/fields/padlock.png");

        // Error box
        error.setTextFill(Paint.valueOf("#ff0000"));
        error.setFont(Font.font("Inter", 10));

        // Putting info into api
        api.put("email", new TextValue(email)).put("phone", new TextValue(phone))
                .put("password1", new TextValue(password1)).put("password2", new TextValue(password2));

        // Adding everything into box
        box.getChildren().addAll(emailIconed, phoneIconed, pass1Iconed, pass2Iconed, error);
        return box;
    }

    // Box for registration code from email
    private VBox createCodeVerificationSection(ApiHandler codeApi) {

        // Styling
        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        // Title
        Label title = new Label("We’ve sent a verification code to your email.");
        title.setTextFill(Paint.valueOf("#ffffff"));
        title.setFont(Font.font("Inter", 10));

        // Fields and setting timer styling
        TextField code = new UIFactory().authField("Input code");
        timer.setTextFill(Paint.valueOf("#ffffff"));
        timer.setFont(Font.font("Inter", 10));

        // Putting info into api
        codeApi.put("code", new TextValue(code));

        // Adding everything into box
        box.getChildren().addAll(title, code, timer);
        return box;
    }

    // Creates continue button
    private HBox createPersonalButtons() {

        // Styling
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        // Buttons and logic
        Button conti = UIFactory.authBtn("Continue");
        conti.setOnAction(e -> changeContent(sDetails, sButtons));

        // Adding everything into box
        box.getChildren().add(conti);
        return box;
    }

    // Creates button to request sign up
    private HBox createContactButtons() {

        // Styling
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        // Buttons and logic
        Button back = UIFactory.authBtn("←");
        Button signup = UIFactory.authBtn("Sign up");

        back.setOnAction(e -> changeContent(pDetails, pButtons));

        signup.setOnAction(e -> {

            // Getting api response
            JSON response = api.post("users/register");
            if(!response.isSuccess()) { // Not correct data
                error.setText(response.getMessage());
                return;
            }

            // Change content and save registration code to new api
            codeApi.put("token", response.get("token"));
            changeContent(cDetails, cButtons);
        });

        // Adding everything into box
        box.getChildren().addAll(back, signup);
        return box;
    }

    // Creates button to perform sign up
    private HBox createCodeButtons() {

        // Styling
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        // Buttons and logic
        Button resend = UIFactory.authBtn("Resend code");
        Button complete = UIFactory.authBtn("Complete");

        resend.setOnAction(e -> {

            // Getting api response
            JSON response = codeApi.post("users/register-resend", null);
            if(!response.isSuccess()) { // Not correct data
                timer.setText(response.getMessage());
                return;
            }

            // Displaying message
            timer.setText(response.getMessage());
        });

        complete.setOnAction(e -> {

            // Getting api response
            JSON response = codeApi.post("users/register-complete", null);
            if(!response.isSuccess()) { // Not correct data
                timer.setText(response.getMessage());
                return;
            }

            // If process was successfully
            onSignUpSuccess(response, true);

        });

        // Adding everything into box
        box.getChildren().addAll(resend, complete);
        return box;
    }

    // Change form based on button action
    public void changeContent(Region box, Region btn) {
        this.getChildren().setAll(box, btn);
    }

    // If registration was successfully
    private void onSignUpSuccess(JSON data, boolean stay) {

        // pridanie do AccountManagera
        AccountManager manager = AccountManager.getInstance();
        manager.addAccount(data, stay);

        // prepni scénu
        SceneHandler.getInstance().switchOnToken();
    }

}