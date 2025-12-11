package me.seadlnej.app.resources;

import me.seadlnej.app.Main;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlFile {

    private File file;
    private Map<String, Object> data;
    private Yaml yaml;

    public YamlFile(String path, String name) {

        this.file = new File(path + name);
        this.data = new HashMap<>();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);

        if (!file.exists()) {

            file.getParentFile().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        load();
    }

    // ---------------------------------------------------------------
    // LOADING
    // ---------------------------------------------------------------
    public void load() {
        try (InputStream input = new FileInputStream(file)) {
            Object obj = yaml.load(input);
            if (obj instanceof Map) {
                this.data = (Map<String, Object>) obj;
            } else {
                this.data = new LinkedHashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // SAVING
    // ---------------------------------------------------------------
    public void save() {
        try (Writer writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // PATH NAVIGATION
    // ---------------------------------------------------------------

    private Map<String, Object> navigateToParent(String path, boolean create) {
        String[] parts = path.split("\\.");
        Map<String, Object> section = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = section.get(parts[i]);

            if (!(next instanceof Map)) {
                if (!create) return null;

                // create new section
                Map<String, Object> newSection = new LinkedHashMap<>();
                section.put(parts[i], newSection);
                section = newSection;
            } else {
                section = (Map<String, Object>) next;
            }
        }
        return section;
    }

    private String getLastKey(String path) {
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    // ---------------------------------------------------------------
    // SECTION HANDLING
    // ---------------------------------------------------------------

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        Object v = get(path);
        if (v instanceof Boolean) {
            return (Boolean) v;
        } else if (v instanceof String) {
            // Ak je uložené ako string "true"/"false"
            return Boolean.parseBoolean((String) v);
        } else {
            return defaultValue;
        }
    }

    public ConfigurationSection getSection(String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> section = data;

        for (String p : parts) {
            Object next = section.get(p);
            if (!(next instanceof Map)) return new ConfigurationSection(new HashMap<>());
            section = (Map<String, Object>) next;
        }
        return new ConfigurationSection(section);
    }

    // ---------------------------------------------------------------
    // GETTERS (path support)
    // ---------------------------------------------------------------

    public String getString(String path) {
        Object v = get(path);
        return v != null ? v.toString() : null;
    }

    public Object get(String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> section = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = section.get(parts[i]);
            if (!(next instanceof Map)) return null;
            section = (Map<String, Object>) next;
        }
        return section.get(parts[parts.length - 1]);
    }

    // ---------------------------------------------------------------
    // SETTER (path support)
    // ---------------------------------------------------------------

    public void set(String path, Object value) {
        Map<String, Object> parent = navigateToParent(path, true);
        parent.put(getLastKey(path), value);
        save();
    }

    // ---------------------------------------------------------------
    // UTILS
    // ---------------------------------------------------------------

    public boolean contains(String path) {
        return get(path) != null;
    }

    public Set<String> getRootKeys() {
        return data.keySet();
    }

    public Map<String, Object> getRawData() {
        return data;
    }
}