package me.seadlnej.app.core.styles.theme;

import me.seadlnej.app.resources.Resources;

public class Colors {

    // Primary theme
    private static Theme theme = Theme.DARK;

    // Constants
    private static final String gradientPrimary;
    private static final String gradientSecondary;

    // Loading gradient color
    static {
        String gp = Resources.getConfiguration().getString("Colors.Gradient-Primary");
        gradientPrimary = (gp == null || gp.isBlank()) ? "#5c8bd7" : gp;

        String gs = Resources.getConfiguration().getString("Colors.Gradient-Secondary");
        gradientSecondary = (gs == null || gs.isBlank()) ? "#65cbbc" : gs;
    }

    // User or profile status colors
    public static String Online() {
        return "#5dd255";
    }

    public static String Offline() {
        return "#b6cfd8";
    }

    public static String Away() {
        return "#ffd200";
    }

    public static String Busy() {
        return "#b6cfd8";
    }

    public static String DnD() {
        return "#c72d25";
    }

    // Basic UI components colors
    public static String Primary() {
        return switch (theme) {
            case LIGHT -> "#fafafc";
            case DARK -> "#2a2b35";
        };
    }

    public static String Secondary() {
        return switch (theme) {
            case LIGHT -> "#ffffff";
            case DARK -> "#1a1a23";
        };
    }

    public static String Tertiary() {
        return switch (theme) {
            case LIGHT -> "#eaeaed";
            case DARK -> "#5a5b63";
        };
    }

    public static String Bar() {
        return switch (theme) {
            case LIGHT -> "#fafafa";
            case DARK -> "#2a2b35";
        };
    }

    public static String Header() {
        return switch (theme) {
            case LIGHT -> "#3a3b44";
            case DARK -> "#fafafc";
        };
    }

    public static String Description() {
        return switch (theme) {
            case LIGHT -> "#aaaaaf";
            case DARK -> "#aaaaaf";
        };
    }

    public static String GradientPrimary() {
        return gradientPrimary;
    }

    public static String GradientSecondary() {
        return gradientSecondary;
    }

    public static String LinearGradient() {
        return "linear-gradient(from 0% 0% to 50% 100%, " + gradientPrimary + ", " + gradientSecondary + ")";
    }

    public static String LinearGradient(int fromX, int fromY, int toX, int toY) {
        return "linear-gradient(from " + fromX + "% " + fromY + "% to " +
                toX + "% " + toY + "%, " + gradientPrimary + ", " + gradientSecondary + ")";
    }

    public static Theme getTheme() {
        return theme;
    }

    public static void setTheme(Theme theme) {
        Colors.theme = theme;
    }

}