package me.seadlnej.app.utilities;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.managers.Account;
import me.seadlnej.app.managers.AccountManager;

import java.util.Objects;

public class Profile {

    private static Profile instance;

    private HBox box;
    private ImageView imageView;
    private VBox details;
    private Label name;
    private HBox statusBox;
    private Circle statusCircle;
    private Label statusLabel;

    private Popup popup;
    private final ApiHandler api = new ApiHandler();

    private Profile() { initUI(); initPopup(); }

    public static Profile getInstance() {
        if (instance == null) {
            instance = new Profile();
        }
        return instance;
    }

    // Creating default UI
    private void initUI() {

        // Getting active account
        Account account = AccountManager.getInstance().getActive();

        // Default main box styling
        box = new HBox(5);
        box.setPadding(new Insets(2, 6, 2, 6));
        box.setAlignment(Pos.CENTER_LEFT);
        String boxStyle = "-fx-background-radius: 4; -fx-border-radius: 4;";

        // Image view for circle
        imageView = new ImageView(account.getImage());
        imageView.setFitWidth(36);
        imageView.setFitHeight(36);
        imageView.setPreserveRatio(true);

        // Creating circle for image
        Circle clip = new Circle(18, 18, 18);
        imageView.setClip(clip);

        // Styling profile detail information's
        details = new VBox();
        details.setPrefHeight(36);
        details.setAlignment(Pos.CENTER_LEFT);

        // Name label for firstname and lastname
        name = new Label(account.getFirstname() + " " + account.getLastname());
        name.setFont(Font.font("Inter", 12));
        name.setTextFill(Color.valueOf(Colors.Header()));
        ThemeManager.onChange(() -> name.setTextFill(Color.valueOf(Colors.Header())));

        // Status box wrapper for circle and label
        statusBox = new HBox(3);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        // Status label colored circle
        statusCircle = new Circle(4);
        statusCircle.setFill(Paint.valueOf(Colors.Online()));
        statusCircle.setTranslateY((double) -((10 - 8) / 2));

        // Status label for texted status
        statusLabel = new Label("Online");
        statusLabel.setFont(Font.font("Inter", 10));
        statusLabel.setTextFill(Color.valueOf(Colors.Description()));
        ThemeManager.onChange(() -> statusLabel.setTextFill(Color.valueOf(Colors.Description())));

        // Adding everything into boxes
        statusBox.getChildren().addAll(statusCircle, statusLabel);
        details.getChildren().addAll(name, statusBox);

        // Setting box hover effect and action
        box.setOnMouseEntered(e -> box.setStyle(boxStyle + "-fx-background-color: " + Colors.Secondary() + ";"));
        box.setOnMouseExited(e -> box.setStyle(boxStyle + "-fx-background-color: transparent;"));

        box.setOnMouseClicked(e -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                double x = box.getParent().localToScreen(0, 0).getX();
                double y = box.localToScreen(0, 0).getY() + box.getHeight() + 10;
                VBox popupBox = (VBox) popup.getContent().get(0);
                popupBox.setPrefWidth(box.getParent().getBoundsInParent().getWidth());
                popup.show(box, x, y);
            }
        });

        // Adding everything into box
        box.getChildren().addAll(imageView, details);
    }

    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "offline" -> Colors.Offline();
            case "away" -> Colors.Away();
            case "busy" -> Colors.Busy();
            case "dnd" -> Colors.DnD();
            default -> Colors.Online();
        };
    }

    private void initPopup() {
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        VBox popupBox = new VBox(5);
        popupBox.setPadding(new Insets(5));
        popupBox.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox online = UIFactory.profStat("Online", Colors.Online());
        HBox away   = UIFactory.profStat("Away", Colors.Away());
        HBox busy   = UIFactory.profStat("Busy", Colors.Busy());
        HBox dnd    = UIFactory.profStat("Do Not Disturb", Colors.DnD());
        HBox offline= UIFactory.profStat("Offline", Colors.Offline());

        online.setOnMouseClicked(e -> changeStatus("Online"));
        away.setOnMouseClicked(e -> changeStatus("Away"));
        busy.setOnMouseClicked(e -> changeStatus("Busy"));
        dnd.setOnMouseClicked(e -> changeStatus("DnD"));
        offline.setOnMouseClicked(e -> changeStatus("Offline"));

        popupBox.getChildren().addAll(online, away, busy, dnd, offline);
        popup.getContent().add(popupBox);
    }

    private void changeStatus(String newStatus) {
        api.put("status", newStatus).post("profile/status", SessionHandler.getInstance().getToken());
        updateStatus(newStatus);
        popup.hide();
    }

    public void updateStatus(String newStatus) {
        statusLabel.setText(newStatus);
        statusCircle.setFill(Color.valueOf(getStatusColor(newStatus)));
    }

    public HBox getProfileBox() {
        return box;
    }

    public Image getProfileImage() {
        return imageView.getImage();
    }

    public String getFullname() {
        return name.getText();
    }

    public String getStatus() {
        return statusLabel.getText();
    }

    public void update(Account account) {

        // --- Update profile image ---
        if (account.getImage() != null && !account.getImage().isError()) {
            imageView.setImage(account.getImage());
        } else {
            imageView.setImage(new Image(Objects.requireNonNull(AccountManager.class.getResourceAsStream("/images/user.png"))));
        }

        // --- Update fullname ---
        String fullname = account.getFirstname() + " " + account.getLastname();
        name.setText(fullname);

    }

    // Getters and Setters

}