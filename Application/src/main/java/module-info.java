module me.seadlnej.app.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires java.management;
    requires spring.web;
    requires java.net.http;
    requires spring.boot;
    requires com.fasterxml.jackson.databind;
    requires org.yaml.snakeyaml;
    requires jakarta.annotation;
    requires Java.WebSocket;
    requires slf4j.api;
    requires spring.context;
    requires org.apache.tomcat.embed.websocket;
    requires spring.messaging;
    requires spring.websocket;
    requires spring.core;

    // Otvorené pre FXML reflexiu
    opens me.seadlnej.app to javafx.fxml;

    // Export hlavného balíka, aby bol dostupný von
    exports me.seadlnej.app;

    // Otvorené balíky pre Tomcat WebSocket (rieši IllegalAccessException)
    opens me.seadlnej.app.utilities to org.apache.tomcat.websocket;
    exports me.seadlnej.app.utilities;
}
