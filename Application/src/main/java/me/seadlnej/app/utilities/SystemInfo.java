package me.seadlnej.app.utilities;

import me.seadlnej.app.Main;

public class SystemInfo {

    // Enum class for OSTYpes
    public enum OSType {
        Windows, Linux, Mac, Other
    }

    // Constants
    private final OSType osType;
    private final String defaultPath;

    // Construcctor
    public SystemInfo() {
        this.osType = detectOSType();
        this.defaultPath = determineDefaultPath();
    }

    // Detect and save os type
    private OSType detectOSType() {
        String osString = System.getProperty("os.name").toLowerCase();
        if (osString.contains("win")) return OSType.Windows;
        if (osString.contains("nux") || osString.contains("nix")) return OSType.Linux;
        if (osString.contains("mac")) return OSType.Mac;
        return OSType.Other;
    }

    // Give path for specific os
    private String determineDefaultPath() {
        switch (osType) {
            case Windows:
                return System.getenv("APPDATA") + "\\StudyHub\\";
            case Linux:
                return System.getProperty("user.home") + "/.studyhub/";
            case Mac:
                return System.getProperty("user.home") + "/Library/Application Support/StudyHub/";
            default:
                return System.getProperty("user.home") + "/StudyHub/";
        }
    }

    // Getter and Setters
    public OSType getOsType() {
        return osType;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

}