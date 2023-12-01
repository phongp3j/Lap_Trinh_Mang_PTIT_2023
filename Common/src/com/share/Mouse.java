package com.share;

import java.io.Serializable;

public class Mouse implements Serializable {
    private double sceneX, sceneY;
    private String eventType;
    private String button;

    public Mouse(double sceneX, double sceneY, String eventType, String button) {
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.eventType = eventType;
        this.button = button;
    }

    public double getSceneX() {
        return sceneX;
    }

    public double getSceneY() {
        return sceneY;
    }

    public String getEventType() {
        return eventType;
    }

    public String getButton() {
        return button;
    }
}
