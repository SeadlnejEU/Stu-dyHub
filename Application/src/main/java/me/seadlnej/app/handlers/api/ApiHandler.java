package me.seadlnej.app.handlers.api;

import me.seadlnej.app.handlers.connection.ConnectionHandler;
import me.seadlnej.app.providers.ButtonValue;
import me.seadlnej.app.providers.PasswordValue;
import me.seadlnej.app.providers.TextValue;
import me.seadlnej.app.utilities.JSON;

import java.util.HashMap;
import java.util.Map;

public class ApiHandler implements RestApiHandler {

    private Map<String, Object> data;

    public ApiHandler() {
        data = new HashMap<>();
    }

    @Override
    public ApiHandler put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public JSON convert() {

        JSON json = new JSON();

        for(Map.Entry<String, Object> entry : data.entrySet()) {

            Object value = entry.getValue();

            if (value instanceof TextValue) {
                json.put(entry.getKey(), ((TextValue) value).getValue());
            } else if (value instanceof ButtonValue) {
                json.put(entry.getKey(), ((ButtonValue) value).getValue());
            } else if (value instanceof PasswordValue) {
                json.put(entry.getKey(), ((PasswordValue) value).getValue());
            } else if (value instanceof JSON) {
                json.putJson(entry.getKey(), (JSON) value);
            } else if(value instanceof  Long) {
                json.put(entry.getKey(), value);
            } else if (value instanceof Number || value instanceof Boolean) {
                json.put(entry.getKey(), value);
            } else if (value == null) {
                json.put(entry.getKey(), null);
            } else {
                json.put(entry.getKey(), value.toString());
            }

        }

        return json;

    }

    @Override
    public JSON post(String endpoint) {

        JSON json = new JSON();

        String response = ConnectionHandler.getInstance().post(endpoint, convert().toString());
        json.parseString(response);

        return json;
    }

    @Override
    public JSON post(String endpoint, String token) {

        JSON json = new JSON();
        if(token != null && !token.isEmpty()) { data.put("token", token); }

        String response = ConnectionHandler.getInstance().post(endpoint, convert().toString());
        json.parseString(response);

        return json;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

}
