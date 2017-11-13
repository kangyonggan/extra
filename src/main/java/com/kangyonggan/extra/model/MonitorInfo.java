package com.kangyonggan.extra.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorInfo implements Serializable {

    private String app;
    private String type;
    private String packageName;
    private String className;
    private String methodName;
    private Long startTime;
    private Long endTime;
    private Object returnValue;
    private Object args[];

    public MonitorInfo() {
    }

    public MonitorInfo(String app, String type, String packageName, String className, String methodName, Long startTime, Long endTime, Object returnValue, Object[] args) {
        this.app = app;
        this.type = type;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    @Override
    public String toString() {
        return "MonitorInfo{" +
                "app='" + app + '\'' +
                ", type='" + type + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", returnValue=" + returnValue +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
