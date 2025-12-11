package me.seadlnej.app.handlers.connection;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ConnectionHandler {

    private String address;
    private HttpClient client;

    private static ConnectionHandler connectionHadnler;

    public ConnectionHandler(String address) {
        this.address = address;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public static void init(String address) {
        if(connectionHadnler == null) {
            connectionHadnler = new ConnectionHandler(address);
        }
    }

    public Map<String, Object> get(String endpoint) {



        return null;
    }

    public String post(String endpoint, String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(address + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    // Getter and Setter
    public static ConnectionHandler getInstance() { return connectionHadnler; }

}