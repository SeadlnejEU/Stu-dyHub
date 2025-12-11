package me.seadlnej.server.dto;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    private boolean success;
    private Map<String, Object> data = new HashMap<>();

    public ApiResponse() {}

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.data.put("message", message);
    }

    public boolean isSuccess() {
        return success;
    }

    public ApiResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Object getData(String key) {
        if(!data.containsKey(key)) {
            return null;
        }
        return data.get(key);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ApiResponse setData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public ApiResponse deleteData(String key) {
        data.remove(key);
        return this;
    }

}