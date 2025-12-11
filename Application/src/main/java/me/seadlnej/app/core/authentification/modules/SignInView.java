package me.seadlnej.app.core.authentification.modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.managers.AccountManager;
import me.seadlnej.app.providers.PasswordValue;
import me.seadlnej.app.providers.TextValue;
import me.seadlnej.app.utilities.JSON;

public class SignInView extends VBox {

    private final ApiHandler api;
    private CheckBox stay;
    private Runnable onForgotPass; // ← callback
    private final Label error = new Label();

    public SignInView(Parent parent) {

        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        VBox.setVgrow(this, Priority.ALWAYS);

        api = new ApiHandler();

        VBox details = createSignInDetails(api);
        HBox button = createSignInButton();

        getChildren().addAll(details, button);
    }

    private VBox createSignInDetails(ApiHandler api) {

        VBox box = new VBox(15);
        VBox.setVgrow(box, Priority.ALWAYS);

        TextField username = new TextField();
        PasswordField password = new PasswordField();

        HBox usernameIconed = UIFactory.authFieldWithIcon(username,
                "Username / Email", "/images/fields/user.png");
        HBox passwordIconed = UIFactory.authFieldWithIcon(password,
                "Password", "/images/fields/padlock.png");

        // --- CLICKABLE "FORGOT PASSWORD" TEXT ---
        Text forgot = new Text("Forgot password?");
        forgot.setFill(Paint.valueOf("#4e9cff"));
        forgot.setFont(Font.font("Inter", 12));
        forgot.setOnMouseClicked(e -> {
            if (onForgotPass != null) onForgotPass.run();
        });

        stay = UIFactory.authCheckbox("Stay signed in");

        error.setTextFill(Paint.valueOf("#ff0000"));
        error.setFont(Font.font("Inter", 10));

        api.put("username", new TextValue(username)).put("password", new PasswordValue(password));

        // Add components including forgot-password text
        box.getChildren().addAll(usernameIconed, passwordIconed, forgot, stay, error);
        return box;
    }

    private HBox createSignInButton() {

        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        Button singin = UIFactory.authBtn("Sign in");
        singin.setOnAction(e -> {

            JSON response = api.post("users/login");
            if(!response.isSuccess()) {
                error.setText(response.getMessage());
                return;
            }

            onSignInSuccess(response, stay.isSelected());

        });

        box.getChildren().add(singin);
        return box;
    }

    private void onSignInSuccess(JSON data, boolean stay) {

        AccountManager manager = AccountManager.getInstance();
        manager.addAccount(data, stay);

        SceneHandler.getInstance().switchOnToken();
    }

    // --- callback setter for AccountsPanel ---
    public void setOnForgotPass(Runnable onForgotPass) {
        this.onForgotPass = onForgotPass;
    }
}
