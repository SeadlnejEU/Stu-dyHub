package me.seadlnej.app.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSON {

    private final Map<String, Object> data = new HashMap<>();

    public String get(String key) {
        Object value = data.get(key);
        return value instanceof String ? (String) value : null;
    }

    public Object getObj(String key) {
        return data.get(key);
    }

    public JSON getJson(String key) {
        Object v = data.get(key);
        return v instanceof JSON ? (JSON) v : null;
    }

    public JSON put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public JSON putJson(String key, JSON json) {
        data.put(key, json);
        return this;
    }

    public JSON remove(String key) {
        data.remove(key);
        return this;
    }

    public boolean isSuccess() {
        if(data.containsKey("success")) {
            return (boolean) data.get("success");
        }
        return false;
    }

    public String getMessage() {
        if(data.containsKey("success")) {
            return (String) data.get("message");
        }
        return "Error occurred during communication with server";
    }

    // Parsing String to JSON
    public void parseString(String json) {
        if (json == null || json.isEmpty()) return;

        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {

            // --- Parse key ---
            if (json.charAt(i) == '"') i++;
            int keyStart = i;
            while (i < json.length() && json.charAt(i) != '"') i++;
            String key = json.substring(keyStart, i);
            i++; // preskoči koncový "

            // --- Preskoč ":"
            while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) i++;

            // --- Parse value ---
            if (i < json.length()) {
                char c = json.charAt(i);
                if (c == '"') { // string
                    i++;
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != '"') i++;
                    String value = json.substring(valStart, i);
                    data.put(key, value);
                    i++;
                } else if (c == '{') { // vnorený objekt
                    int brace = 1;
                    int valStart = i;
                    i++;
                    while (i < json.length() && brace > 0) {
                        if (json.charAt(i) == '{') brace++;
                        if (json.charAt(i) == '}') brace--;
                        i++;
                    }
                    String inner = json.substring(valStart, i);
                    JSON child = new JSON();
                    child.parseString(inner);
                    data.put(key, child);
                } else { // boolean alebo číslo
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                    String raw = json.substring(valStart, i).trim();

                    if ("true".equals(raw)) {
                        data.put(key, true);
                    } else if ("false".equals(raw)) {
                        data.put(key, false);
                    } else {
                        // pokus o číslo
                        try {
                            if (raw.contains(".") || raw.contains("e") || raw.contains("E")) {
                                data.put(key, Double.parseDouble(raw)); // desatinné číslo
                            } else {
                                data.put(key, Integer.parseInt(raw)); // celé číslo
                            }
                        } catch (NumberFormatException e) {
                            data.put(key, raw); // nebolo číslo, ulož ako string
                        }
                    }
                }
            }

            // preskoč čiaru
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            sb.append("\"").append(e.getKey()).append("\":");

            Object value = e.getValue();
            if (value instanceof JSON) {
                sb.append(value.toString());
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(value).append("\"");
            }

            if (it.hasNext()) sb.append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    public Map<String, Object> getData() {
        return data;
    }
}
