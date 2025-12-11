package me.seadlnej.app.handlers.status;

import java.util.ArrayList;
import java.util.List;

public class StatusHandler {

    // Singleton instance
    private static final StatusHandler INSTANCE = new StatusHandler();

    private static Status status = Status.ONLINE; //Default status
    private final List<Runnable> listeners = new ArrayList<>();

    // Constructor
    private StatusHandler() {}

    // Singleton instance
    public static StatusHandler getInstance() {
        return INSTANCE;
    }

    // Nastavenie statusu a spustenie listenerov
    public void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
            notifyListeners();
        }
    }

    // Registrovanie Runnable listenera
    public void addListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // Odregistrovanie listenera
    public synchronized void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    // Spustenie všetkých listenerov
    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
    // Getters and setters;
    public static Status getStatus() {
        return status;
    }

}