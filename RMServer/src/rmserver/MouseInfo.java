/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmserver;

/**
 *
 * @author admin
 */

import java.io.Serializable;

public class MouseInfo implements Serializable {

    private double sceneX, sceneY;
    private String eventType;
    private String button;

    public MouseInfo(MouseEvent mouseEvent) {
        sceneX = mouseEvent.getSceneX();
        sceneY = mouseEvent.getSceneY();
        eventType = mouseEvent.getEventType().getName();
        button = mouseEvent.getButton();
    }

    public double getSceneY() {
        return sceneY;
    }

    public double getSceneX() {
        return sceneX;
    }

    public String getEventType() {
        return eventType;
    }

    public String getButton() {
        return button;
    }
}
