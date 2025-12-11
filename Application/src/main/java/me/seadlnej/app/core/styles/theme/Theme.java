package me.seadlnej.app.core.styles.theme;

public enum Theme {

    LIGHT("Light"), DARK("Dark");

    private final String title;
    Theme(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

}