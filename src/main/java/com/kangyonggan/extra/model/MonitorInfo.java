package com.kangyonggan.extra.model;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorInfo {

    private String app;
    private String type;
    private String handlePackage;
    private String packageName;
    private String className;
    private String methodName;
    private Object args[];
    private MonitorHandleInfo monitorHandleInfo;

    public MonitorInfo(String app, String type, String handlePackage, String packageName, String className, String methodName, Object[] args) {
        this.app = app;
        this.type = type;
        this.handlePackage = handlePackage;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.monitorHandleInfo = MonitorHandleInfoFactory.getInstance().getMonitorHandleInfo(handlePackage);
    }

    public void error(String msg, Exception e) {
        if (monitorHandleInfo != null) {
            try {
                monitorHandleInfo.getMethod().invoke(monitorHandleInfo.getObject(), msg, e, this);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void error(String msg) {
        error(msg, null);
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

    public String getHandlePackage() {
        return handlePackage;
    }

    public void setHandlePackage(String handlePackage) {
        this.handlePackage = handlePackage;
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

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public MonitorHandleInfo getMonitorHandleInfo() {
        return monitorHandleInfo;
    }

    public void setMonitorHandleInfo(MonitorHandleInfo monitorHandleInfo) {
        this.monitorHandleInfo = monitorHandleInfo;
    }

    @Override
    public String toString() {
        return "MonitorInfo{" +
                "app='" + app + '\'' +
                ", type='" + type + '\'' +
                ", handlePackage='" + handlePackage + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", monitorHandleInfo=" + monitorHandleInfo +
                '}';
    }
}
