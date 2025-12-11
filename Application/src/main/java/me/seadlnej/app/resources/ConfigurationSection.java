package me.seadlnej.app.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationSection {

    private final Map<String, Object> map;

    public ConfigurationSection(Map<String, Object> map) {
        this.map = map;
    }

    public Set<String> getKeys(boolean deep) {
        return map.keySet();
    }

    public Object get(String key) {
        return map.get(key);
    }

    public String getString(String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return v != null ? Integer.parseInt(v.toString()) : null; }
        catch (Exception e) { return null; }
    }

    public Boolean getBoolean(String key) {
        Object v = map.get(key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v != null) return Boolean.parseBoolean(v.toString());
        return null;
    }

    public List<String> getStringList(String key) {
        Object v = map.get(key);
        if (v instanceof List) {
            List<?> list = (List<?>) v;
            List<String> result = new ArrayList<>();
            for (Object obj : list) result.add(obj.toString());
            return result;
        }
        return new ArrayList<>();
    }

    public ConfigurationSection getSection(String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return new ConfigurationSection((Map<String, Object>) value);
        }
        return null;
    }

    public Map<String, Object> getRawMap() {
        return map;
    }
}