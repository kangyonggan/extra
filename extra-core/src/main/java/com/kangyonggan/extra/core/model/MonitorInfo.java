package com.kangyonggan.extra.core.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author kangyonggan
 * @since 3/30/18
 */
public class MonitorInfo implements Serializable {

    private String app;

    private String type;

    private String description;

    private Long methodStartTime;

    private Long methodEndTime;

    private Boolean hasReturnValue;

    private Object returnValue;

    private Object[] args;

    public MonitorInfo() {

    }

    public MonitorInfo(String app, String type, String description, Long methodStartTime, Long methodEndTime, Boolean hasReturnValue, Object returnValue, Object... args) {
        this.app = app;
        this.type = type;
        this.description = description;
        this.methodStartTime = methodStartTime;
        this.methodEndTime = methodEndTime;
        this.hasReturnValue = hasReturnValue;
        this.returnValue = returnValue;
        this.args = args;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMethodStartTime() {
        return methodStartTime;
    }

    public void setMethodStartTime(Long methodStartTime) {
        this.methodStartTime = methodStartTime;
    }

    public Long getMethodEndTime() {
        return methodEndTime;
    }

    public void setMethodEndTime(Long methodEndTime) {
        this.methodEndTime = methodEndTime;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Boolean getHasReturnValue() {
        return hasReturnValue;
    }

    public void setHasReturnValue(Boolean hasReturnValue) {
        this.hasReturnValue = hasReturnValue;
    }

    @Override
    public String toString() {
        return "MonitorInfo{" +
                "app='" + app + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", methodStartTime=" + methodStartTime +
                ", methodEndTime=" + methodEndTime +
                ", hasReturnValue=" + hasReturnValue +
                ", returnValue=" + returnValue +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
