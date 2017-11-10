package com.kangyonggan.extra.util;

import com.kangyonggan.extra.model.MonitorInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorUtil {

    private static String servers;
    private static List<Map<String, Object>> serverList;
    private static Method method;
    private static Object object;

    public static void monitor(String app, String type, String handlePackage, String packageName,
                               String className, String methodName, Object... args) {
        if (StringUtil.isEmpty(servers) && initHandle(handlePackage)) {
            MonitorInfo monitorInfo = new MonitorInfo(app, type, packageName, className, methodName, args);
            try {
                initServers();
            } catch (Exception e) {
                try {
                    method.invoke(object, "Monitor Servers Init Exception!", e, monitorInfo);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                return;
            }

            for (Map<String, Object> server : serverList) {
                try {
                    // Send To Server
                    boolean success = send(server, monitorInfo);
                    if (success) {
                        break;
                    }
                } catch (Exception e) {
                    try {
                        method.invoke(object, "Method Information Send to [" + server.get("ip") + ":" + server.get("port") + "] Failure!", e, monitorInfo);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private static boolean send(Map<String, Object> server, MonitorInfo monitorInfo) {
        // TODO
        return false;
    }

    private static boolean initHandle(String handlePackage) {
        try {
            Class clazz = Class.forName(handlePackage);
            method = clazz.getDeclaredMethod("error", Exception.class, MonitorInfo.class);
            object = clazz.newInstance();
        } catch (Exception e1) {
            return false;
        }

        return true;
    }

    private static synchronized void initServers() throws Exception {
        if (StringUtil.isNotEmpty(servers)) {
            return;
        }

        servers = PropertiesUtil.getMonitorServers();
        serverList = new ArrayList();
        for (String server : servers.split(",")) {
            Map<String, Object> map = new HashMap(1);
            String serverArr[] = server.split(":");
            map.put("ip", serverArr[0]);
            map.put("port", Integer.parseInt(serverArr[1]));

            serverList.add(map);
        }
    }

}
