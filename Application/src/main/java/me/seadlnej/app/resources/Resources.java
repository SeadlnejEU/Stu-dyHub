package me.seadlnej.app.resources;

import me.seadlnej.app.Main;
import me.seadlnej.app.utilities.SystemInfo;

public class Resources {

    private static YamlFile accounts;
    private static YamlFile configuration;
    private static final SystemInfo sysInfo = new SystemInfo();

    public static void load() {
        accounts = new YamlFile(sysInfo.getDefaultPath(), "accounts.yml");
        configuration = new YamlFile(sysInfo.getDefaultPath(), "configuration.yml");
    }

    public static void save() {
        accounts.save();
        configuration.save();
    }

    public static YamlFile getAccounts() { return accounts; }
    public static YamlFile getConfiguration() { return configuration; }

}