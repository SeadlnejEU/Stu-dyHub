package me.seadlnej.app.handlers.api;

import me.seadlnej.app.utilities.JSON;

import java.util.Map;

public interface RestApiHandler {
    ApiHandler put(String key, Object value);
    JSON post(String endpoint, String token);
    JSON post(String endpoint);
    Map<String, Object> getData();
}