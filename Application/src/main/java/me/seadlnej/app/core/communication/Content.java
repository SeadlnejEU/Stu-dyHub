package me.seadlnej.app.core.communication;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.core.communication.modules.ChatListView;
import me.seadlnej.app.core.communication.modules.ConversationView;
import me.seadlnej.app.core.communication.modules.ToolBar;
import me.seadlnej.app.core.configuration.Configuration;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

public class Content extends VBox {

    // Instance
    private static Content instance;

    // Constants
    private final ToolBar toolBar;
    private final ChatListView chatList;
    private final ConversationView conversation;

    // Private constructor
    private Content(Region parent) {

        // Default styling
        super(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: " + Colors.Secondary() + ";");
        ThemeManager.onChange(() -> setStyle("fx-background-color: " + Colors.Secondary() + ";"));

        //
        toolBar = new ToolBar(this);

        HBox wrapper = new HBox(10);
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        chatList = new ChatListView(wrapper);

        // Initializing conversation view
        ConversationView.init(this);
        conversation = ConversationView.getInstance();

        wrapper.getChildren().addAll(chatList, conversation);
        getChildren().addAll(toolBar, wrapper);

    }

    // Initialization
    public static void init(Region parent) {
        if(instance == null) {
            instance = new Content(parent);
        }
    }

    // Getting instance
    public static Content getInstance() {
        return instance;
    }

    public void refresh() {
        chatList.refresh();
    }

    // Getters and Setter
    public ChatListView getChatList() {
        return chatList;
    }

    public ConversationView getConversation() {
        return conversation;
    }
}