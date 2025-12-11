package me.seadlnej.app.providers;

import javafx.scene.control.PasswordField;

public class PasswordValue implements ValueProvider {

    private final PasswordField field;

    public PasswordValue(PasswordField field) {
        this.field = field;
    }

    @Override
    public String getValue() { return field.getText(); }

}