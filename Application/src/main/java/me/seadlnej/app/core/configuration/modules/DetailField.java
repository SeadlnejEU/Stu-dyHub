package me.seadlnej.app.core.configuration.modules;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

public class DetailField extends VBox {

    private final Label label;
    private final TextField textField;
    private final Label errorLabel;


    public DetailField(String header, String value) {

        super(5); // Padding between boxes
        setAlignment(Pos.CENTER_LEFT);

        label = new Label(header);
        label.setTextFill(Paint.valueOf(Colors.Header()));
        label.setFont(Font.font("Inter", FontWeight.BOLD, 12));

        textField = new TextField(value);
        textField.setPrefWidth(240);
        applyStyle();

        errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("#ff0000"));
        errorLabel.setFont(Font.font("Inter", 10));
        errorLabel.setVisible(false);

        ThemeManager.onChange(() -> {
            label.setTextFill(Paint.valueOf(Colors.Header()));
            applyStyle();
        });

        getChildren().addAll(label, textField, errorLabel);
    }

    private void applyStyle() {
        textField.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent transparent " + Colors.Description() + " transparent;" +
                "-fx-border-width: 0 0 2 0; -fx-text-fill: " + Colors.Description() + ";"
        );
    }

    public String getValue() {
        return textField.getText();
    }

    public void setValue(String value) {
        textField.setText(value);
    }

    public TextField getTextField() {
        return textField;
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }

    public void setError(String message) {
        if (message == null || message.isBlank()) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        } else {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    public boolean hasError() {
        return errorLabel.isVisible();
    }
}