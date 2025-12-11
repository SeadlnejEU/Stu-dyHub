package me.seadlnej.app.providers;


import javafx.scene.control.Button;

public class ButtonValue implements ValueProvider {

    private final Button button;

    public ButtonValue(Button button) {
        this.button = button;
    }

    @Override
    public String getValue() {
        return button.getText();
    }

}