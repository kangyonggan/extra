package com.kangyonggan.extra.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MonitorHandleInfoFactory {

    private static MonitorHandleInfoFactory instance;

    private Map<String, MonitorHandleInfo> monitorHandleInfos;

    private MonitorHandleInfoFactory() {
        monitorHandleInfos = new HashMap();
    }

    public static MonitorHandleInfoFactory getInstance() {
        if (instance == null) {
            synchronized (MonitorHandleInfoFactory.class) {
                if (instance == null) {
                    instance = new MonitorHandleInfoFactory();
                }
            }
        }

        return instance;
    }

    public MonitorHandleInfo getMonitorHandleInfo(String handlePackage) {
        MonitorHandleInfo monitorHandleInfo = monitorHandleInfos.get(handlePackage);
        if (monitorHandleInfo == null) {
            try {
                synchronized (monitorHandleInfos) {
                    if (monitorHandleInfo == null) {
                        Class clazz = Class.forName(handlePackage);
                        Method method = clazz.getDeclaredMethod("error", String.class, Exception.class, MonitorInfo.class);
                        Object object = clazz.newInstance();

                        monitorHandleInfo = new MonitorHandleInfo(method, object);
                        monitorHandleInfos.put(handlePackage, monitorHandleInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return monitorHandleInfo;
    }
}
