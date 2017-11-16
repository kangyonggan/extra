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
    private String returnType;
    private String argTypes[];

    public MonitorInfo() {
    }

    public MonitorInfo(String app, String type, String description, String packageName, String className, String methodName, Long beginTime, Long endTime, String returnType, String[] argTypes) {
        this.app = app;
        this.type = type;
        this.description = description;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.returnType = returnType;
        this.argTypes = argTypes;
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

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String[] argTypes) {
        this.argTypes = argTypes;
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
                ", returnType='" + returnType + '\'' +
                ", argTypes=" + Arrays.toString(argTypes) +
                '}';
    }
}
