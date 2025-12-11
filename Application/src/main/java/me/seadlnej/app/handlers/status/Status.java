package me.seadlnej.app.handlers.status;

public enum Status {
    ONLINE("Online", "#4CAF50"), AWAY("Away", "#FFC107"),
    BUSY("Busy", "#F44336"), DND("DnD", "#9E9E9E");

    private String value;
    private String color;

    Status(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}