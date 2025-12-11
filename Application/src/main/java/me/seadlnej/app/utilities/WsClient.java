package me.seadlnej.app.utilities;

import javafx.application.Platform;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class WsClient {

    private StompSession session;
    private final WebSocketStompClient stompClient;
    private final List<Runnable> pendingSubscriptions = new ArrayList<>();

    public WsClient(String url) {
        SockJsClient sockJsClient = new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))
        );

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter()); // string converter

        stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession stompSession, StompHeaders connectedHeaders) {
                session = stompSession;
                System.out.println("[WS] Connected!");
                pendingSubscriptions.forEach(Runnable::run);
                pendingSubscriptions.clear();
            }
        });
    }

    public void send(String destination, Object payload) {
        if (session != null && session.isConnected()) {
            session.send(destination, payload);
        } else {
            System.out.println("[WS] Cannot send, session not connected.");
        }
    }

    public void subscribe(String topic, Consumer<String> callback) {
        Runnable subscribeTask = () -> {
            session.subscribe(topic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class; // String payload
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Platform.runLater(() -> {
                        System.out.println("[WS] Message received on topic " + headers.getDestination());
                        callback.accept(payload != null ? payload.toString() : "null");
                    });
                }
            });
            System.out.println("[WS] Subscribed to topic: " + topic);
        };

        if (session != null && session.isConnected()) {
            subscribeTask.run();
        } else {
            pendingSubscriptions.add(subscribeTask);
        }
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }
}
