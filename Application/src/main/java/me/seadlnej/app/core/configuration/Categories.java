package me.seadlnej.app.core.configuration;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;

import java.awt.*;

public class Categories extends VBox {

    public Categories(Region parent) {

        setStyle("-fx-background-color: " + Colors.Primary() + "; " +
                "-fx-border-color: transparent " + Colors.Secondary() + " transparent transparent;" +
                "-fx-border-width: 0 3 0 0;");

        setPadding(new Insets(10));

        prefHeightProperty().bind(parent.heightProperty());
        prefWidthProperty().bind(parent.widthProperty().multiply(0.25));

        ThemeManager.onChange(() -> setStyle(getStyle() + "-fx-background-color: " + Colors.Primary() + ";" +
                "-fx-border-color: transparent " + Colors.Secondary() + " transparent transparent;"));

    }


}