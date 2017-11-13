package com.kangyonggan.extra.core.model;

import java.lang.reflect.Method;

public class MonitorHandleInfo {

    private Method method;

    private Object object;

    public MonitorHandleInfo(Method method, Object object) {
        this.method = method;
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "MonitorHandleInfo{" +
                ", method=" + method +
                ", object=" + object +
                '}';
    }
}
