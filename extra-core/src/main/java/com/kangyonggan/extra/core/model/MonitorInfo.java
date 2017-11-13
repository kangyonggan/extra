package com.kangyonggan.extra.core.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorInfo implements Serializable {

    private String app;
    private String type;
    private String description;
    private String packageName;
    private String className;
    private String methodName;
    private Long beginTime;
    private Long endTime;
    private Object returnValue;
    private Object args[];

    public MonitorInfo() {
    }

    public MonitorInfo(String app, String type, String description, String packageName, String className, String methodName, Long beginTime, Long endTime, Object returnValue, Object[] args) {
        this.app = app;
        this.type = type;
        this.description = description;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.beginTime = beginTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
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
                ", description='" + description + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", returnValue=" + returnValue +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
