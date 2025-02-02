package com.example.shakiewakie.models;

public class Alarm {
    private int id;
    private String time;
    private boolean isEnabled;

    public Alarm(int id, String time, boolean isEnabled) {
        this.id = id;
        this.time = time;
        this.isEnabled = isEnabled;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
