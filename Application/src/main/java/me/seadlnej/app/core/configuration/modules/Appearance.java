package me.seadlnej.app.core.configuration.modules;


import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.seadlnej.app.core.styles.UIFactory;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.Theme;
import me.seadlnej.app.core.styles.theme.ThemeManager;

import java.util.HashMap;
import java.util.Map;

public class Appearance extends VBox {

    public Appearance() {

        super(10);



        getChildren().addAll(themeBox());

    }

    public BadgeBox themeBox() {

        BadgeBox theme = new BadgeBox("Theme", "Applies to whole application.");

        HashMap<String, String> options = new HashMap<>();
        options.put("light", "Light");
        options.put("dark", "Dark");

        String initial = Colors.getTheme().toString();

        VBox drop = UIFactory.dropdownBtn(initial, options, selected -> {
            switch (selected) {
                case "light": ThemeManager.change(Theme.LIGHT);
                    break;
                case "dark": ThemeManager.change(Theme.DARK);
                    break;
                default: break;
            }
        });

        theme.addSetting(drop);

        return theme;
    }

}