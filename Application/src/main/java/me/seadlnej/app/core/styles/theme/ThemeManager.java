package me.seadlnej.app.core.styles.theme;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private static final List<Runnable> listeners = new ArrayList<>();

    public static void toggle() {
        Theme newTheme = (Colors.getTheme() == Theme.DARK)
                ? Theme.LIGHT
                : Theme.DARK;

        Colors.setTheme(newTheme);

        listeners.forEach(Runnable::run);
    }

    public static void change(Theme theme) {
        Colors.setTheme(theme);
        listeners.forEach(Runnable::run);
    }

    public static void onChange(Runnable callback) {
        listeners.add(callback);
    }

    public static boolean isDark() {
        return Colors.getTheme() == Theme.DARK;
    }

}