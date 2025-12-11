package me.seadlnej.app.core.modules.titlebar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.handlers.status.Status;
import me.seadlnej.app.handlers.status.StatusHandler;

import java.util.HashMap;
import java.util.Map;

public class StatusButtons extends HBox {

    private HashMap<Status, Button> buttons = new HashMap<>();

    public StatusButtons() {

        buttons.put(Status.ONLINE, create("Online", "#4CAF50", Status.ONLINE));
        buttons.put(Status.DND, create("DnD", "#9E9E9E", Status.DND));
        buttons.put(Status.AWAY, create("Away", "#FFC107", Status.AWAY));
        buttons.put(Status.BUSY, create("Busy", "#F44336", Status.BUSY));

        for(Map.Entry<Status, Button> entry : buttons.entrySet()) {
            getChildren().add(entry.getValue());
        }

        refresh();

    }

    private Button create(String text, String color, Status status) {

        Button button = new Button(text);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        button.setStyle("-fx-background-color: " + Colors.Tertiary() + "; " +
                "-fx-text-fill: " + color + ";");

        HBox.setMargin(button, new Insets(5));

        button.setOnAction(e -> {


            refresh();
        });

        return button;
    }

    private void refresh() {

        for(Map.Entry<Status, Button> entry : buttons.entrySet()) {
            if(StatusHandler.getStatus().equals(entry.getKey())) {
                entry.getValue().setStyle(entry.getValue().getStyle() + "-fx-background-color: " + Colors.Primary() + ";");
            }
        }

    }

}
