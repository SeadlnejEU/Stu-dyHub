package me.seadlnej.app.core.authentification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.seadlnej.app.core.authentification.modules.ForgotPassView;
import me.seadlnej.app.core.authentification.modules.SignInView;
import me.seadlnej.app.core.authentification.modules.SignUpView;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.managers.Account;
import me.seadlnej.app.managers.AccountManager;

public class AccountsPanel extends VBox {

    private final HBox container;

    private VBox accountsList;
    private SignInView signInView;
    private SignUpView signUpView;
    private ForgotPassView forgotPassView;

    // Constructor
    public AccountsPanel(Region parent) {

        // Default styling of box
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);
        prefWidthProperty().bind(parent.widthProperty().multiply(0.3));

        setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-background-radius: 4;");
        ThemeManager.onChange(() -> setStyle("-fx-background-color: " + Colors.Secondary() + ";"));

        // Header
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30));

        // Main title
        Text title = new Text("STU:DYHUB");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 60));
        title.setFill(Paint.valueOf(Colors.Header()));
        ThemeManager.onChange(() ->  title.setFill(Paint.valueOf(Colors.Header())));
        header.getChildren().add(title);

        container = new HBox(10);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(container, Priority.ALWAYS);

        accountsList();

        if(!AccountManager.getInstance().getAccounts().isEmpty()) {
            container.getChildren().addAll(accountsList);
        } else {
            showSignInView();
        }

        getChildren().addAll(header, container);
    }

    // Refresh UI
    public void refresh() {
        Platform.runLater(() -> {
            accountsList();

            if (!AccountManager.getInstance().getAccounts().isEmpty()) {
                changeContent(accountsList);
            } else {
                showSignInView();
            }
        });
    }

    public void accountsList() {

        // Defining and styling box
        accountsList = new VBox(10);
        HBox.setHgrow(accountsList, Priority.ALWAYS);

        // Scroll pane list
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // VBox for actual list
        VBox list = new VBox(10);

        for (Account account : AccountManager.getInstance().getAccounts()) {

            HBox block = UIFactory.authAccoButn(account);

            // Container for whole row
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            // Remove button
            Button removeBtn = UIFactory.authSwitchForms("Remove");
            removeBtn.setOnAction(e -> {
                AccountManager.getInstance().removeAccount(account);
                refresh();
            });

            row.getChildren().addAll(block, removeBtn);
            list.getChildren().add(row);

            block.setOnMouseClicked(e -> {
                AccountManager.getInstance().setActive(account);
            });
        }

        scroll.setContent(list);

        // Button container
        HBox btnContainer = new HBox();
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.setPadding(new Insets(10));
        HBox.setHgrow(btnContainer, Priority.ALWAYS);

        Button addAccount = UIFactory.authSwitchForms("Use another account");
        addAccount.setOnAction(e -> showSignInView());

        btnContainer.getChildren().addAll(addAccount);

        accountsList.getChildren().addAll(scroll, btnContainer);
    }

    private void showSignInView() {

        if (signInView == null) {
            signInView = new SignInView(container);

            // Register "forgot password" callback
            signInView.setOnForgotPass(() -> showForgotPassView());
        }

        VBox wrapper = new VBox(10);
        wrapper.setFillWidth(true);
        HBox.setHgrow(wrapper, Priority.ALWAYS);

        VBox backContainer = new VBox();
        backContainer.setAlignment(Pos.CENTER_LEFT);
        backContainer.setPadding(new Insets(0, 40, 0 , 40));
        HBox.setHgrow(backContainer, Priority.ALWAYS);

        Button backBtn = UIFactory.authBackList("\uD83E\uDC18");
        backBtn.setOnAction(e -> changeContent(accountsList));

        HBox regContainer = new HBox();
        regContainer.setAlignment(Pos.CENTER);
        regContainer.setPadding(new Insets(10));
        HBox.setHgrow(regContainer, Priority.ALWAYS);

        Button signUp = UIFactory.authSwitchForms("New here? Sign up");
        signUp.setOnAction(e -> showSignUpView());

        backContainer.getChildren().addAll(backBtn);
        regContainer.getChildren().addAll(signUp);

        if(AccountManager.getInstance().getAccounts().isEmpty()) {
            wrapper.getChildren().addAll(signInView, regContainer);
        } else {
            wrapper.getChildren().addAll(backContainer, signInView, regContainer);
        }

        changeContent(wrapper);
    }

    private void showSignUpView() {

        if (signUpView == null) {
            signUpView = new SignUpView(container);
        }

        VBox wrapper = new VBox(10);
        wrapper.setFillWidth(true);
        HBox.setHgrow(wrapper, Priority.ALWAYS);

        VBox backContainer = new VBox();
        backContainer.setAlignment(Pos.CENTER_LEFT);
        backContainer.setPadding(new Insets(0, 40, 0 , 40));
        HBox.setHgrow(backContainer, Priority.ALWAYS);

        Button backBtn = UIFactory.authBackList("\uD83E\uDC18");
        backBtn.setOnAction(e -> changeContent(accountsList));

        HBox logContainer = new HBox();
        logContainer.setAlignment(Pos.CENTER);
        logContainer.setPadding(new Insets(10));
        HBox.setHgrow(logContainer, Priority.ALWAYS);

        Button signIn = UIFactory.authSwitchForms("Already have an account");
        signIn.setOnAction(e -> showSignInView());

        backContainer.getChildren().addAll(backBtn);
        logContainer.getChildren().addAll(signIn);

        if(AccountManager.getInstance().getAccounts().isEmpty()) {
            wrapper.getChildren().addAll(signUpView, logContainer);
        } else {
            wrapper.getChildren().addAll(backContainer, signUpView, logContainer);
        }

        changeContent(wrapper);
    }

    private void showForgotPassView() {

        if (forgotPassView == null) {
            forgotPassView = new ForgotPassView(container);
        }

        VBox wrapper = new VBox(10);
        wrapper.setFillWidth(true);
        HBox.setHgrow(wrapper, Priority.ALWAYS);

        VBox backContainer = new VBox();
        backContainer.setAlignment(Pos.CENTER_LEFT);
        backContainer.setPadding(new Insets(0, 40, 0 , 40));
        HBox.setHgrow(backContainer, Priority.ALWAYS);

        Button backBtn = UIFactory.authBackList("\uD83E\uDC18");
        backBtn.setOnAction(e -> showSignInView());

        backContainer.getChildren().add(backBtn);

        wrapper.getChildren().addAll(backContainer, forgotPassView);

        changeContent(wrapper);
    }

    public void changeContent(Region box) {
        container.getChildren().setAll(box);
    }
}
