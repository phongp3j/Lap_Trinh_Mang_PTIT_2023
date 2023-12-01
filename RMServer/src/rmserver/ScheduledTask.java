/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmserver;

import java.io.Serializable;

/**
 *
 * @author HI
 */
public class ScheduledTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appName;
    private long scheduleTime;

    public ScheduledTask(String appName, long scheduleTime) {
        this.appName = appName;
        this.scheduleTime = scheduleTime;
    }

    public String getAppName() {
        return appName;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }
}