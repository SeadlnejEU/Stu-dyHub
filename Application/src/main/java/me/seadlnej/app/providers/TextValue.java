package me.seadlnej.app.providers;

import javafx.scene.control.TextField;

public class TextValue implements ValueProvider {

    private final TextField field;

    public TextValue(TextField field) {
        this.field = field;
    }

    @Override
    public String getValue() { return field.getText(); }

}