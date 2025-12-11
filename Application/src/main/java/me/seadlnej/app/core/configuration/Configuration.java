package me.seadlnej.app.core.configuration;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.handlers.scene.SceneHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.managers.AccountManager;

public class Configuration extends HBox {

    // Instance
    private static Configuration instance;

    // Constants
    private Runnable onClose;

    private final Content content;
    private final Categories categories;

    // Private constructor
    private Configuration(Region parent) {

        // Initializing content
        Content.init(this);
        content = Content.getInstance();



        categories = new Categories(this);

        createButtons();

        // Adding everything into box
        getChildren().addAll(categories, content);
    }

    // Initialize
    public static void init(Region parent) {
        if(instance == null) {
            instance = new Configuration(parent);
        }
    }

    // Getting instance
    public static Configuration getInstance() {
        return instance;
    }

    public void createButtons() {

        // Defining category buttons
        Button close = UIFactory.confCategory(categories, "❌ Close");
        Button appearance = UIFactory.confCategory(categories, "\uD83C\uDFA8 Appearance");
        Button privacy = UIFactory.confCategory(categories, "\uD83D\uDD12 Privacy");
        Button logout = UIFactory.confLogOut(categories, "Log out");

        // Adding actions to buttons
        close.setOnAction(e -> { if(onClose != null) { onClose.run(); } });
        appearance.setOnAction(e -> select(Content.Categories.APPEARANCE));
        privacy.setOnAction(e -> select(Content.Categories.CREDENTIALS));
        logout.setOnAction(e -> {
            SessionHandler.getInstance().clear();
            AccountManager.getInstance().clearActive();
            SceneHandler.getInstance().switchOnToken();
        });

        // Adding everything into box
        categories.getChildren().addAll(close, appearance, privacy, logout);
    }

    private void select(Content.Categories categories) {
        content.refresh();
        content.update(categories);
    }

    // Refresh UI
    public void refresh() {
        select(Content.Categories.APPEARANCE);
    }

    // Getters and Setters
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

}