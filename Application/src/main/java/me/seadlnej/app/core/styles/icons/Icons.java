package me.seadlnej.app.core.styles.icons;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.Theme;
import me.seadlnej.app.core.styles.theme.ThemeManager;

public enum Icons {

    Contacts("contacts.png"),
    Groups("groups");


    private final String path;

    Icons(String path) {
        this.path = path;
    }

    public ImageView getImage(double width, double height) {

        ImageView image = new ImageView(new Image(getPath()));
        image.setFitWidth(width);
        image.setFitHeight(height);

        ThemeManager.onChange(() -> image.setImage(new Image(getPath())));

        return image;

    }

    public String getPath() {
        String folder = ThemeManager.isDark() ? "dark" : "light";
        return "/icons/" + folder + "/" + path;
    }

}