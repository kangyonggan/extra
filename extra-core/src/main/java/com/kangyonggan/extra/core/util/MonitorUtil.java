package com.kangyonggan.extra.core.util;

import com.kangyonggan.extra.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorUtil {

    private static List<Server> servers = new ArrayList();
    private static LinkedBlockingDeque<MonitorInfo> queue = new LinkedBlockingDeque();
    private static MonitorHandleInfo monitorHandle;

    public static Object monitor(String serversStr, String app, String type, String description, String handlePackage, String packageName,
                                 String className, String methodName, Long startTime, Object returnValue, Object... args) {
        MonitorInfo monitor = new MonitorInfo(app, type, description, packageName, className, methodName, startTime, System.currentTimeMillis(), getReturnType(returnValue), getArgTypes(args));
        initHandle(handlePackage);
        initServers(serversStr, monitor);

        queue.addLast(monitor);
        for (Server server : servers) {
            server.unlock();
        }

        return returnValue;
    }

    private static void initHandle(String handlePackage) {
        if (monitorHandle == null) {
            synchronized (MonitorHandleInfoFactory.class) {
                if (monitorHandle == null) {
                    monitorHandle = MonitorHandleInfoFactory.getInstance().getMonitorHandleInfo(handlePackage);
                }
            }
        }
    }

    private static void initServers(String serversStr, MonitorInfo monitorInfo) {
        if (servers.isEmpty()) {
            synchronized (servers) {
                if (!servers.isEmpty()) {
                    return;
                }
                for (String arr : serversStr.split(",")) {
                    try {
                        String serverArr[] = arr.split(":");
                        Server server = new Server(serverArr[0], Integer.parseInt(serverArr[1]));

                        servers.add(server);
                    } catch (Exception e) {
                        error("Monitor Server [" + arr + "] Init Exception", e);
                    }
                }
            }
        }
    }

    public static void error(String msg, Exception e) {
        error(msg, e, null);
    }

    public static void error(String msg, MonitorInfo monitor) {
        error(msg, null, monitor);
    }

    public static void error(String msg, Exception e, MonitorInfo monitor) {
        if (monitorHandle != null) {
            try {
                monitorHandle.getMethod().invoke(monitorHandle.getObject(), msg, e, monitor);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static MonitorInfo getMonitorInfo() {
        try {
            return queue.takeFirst();
        } catch (InterruptedException e) {
            error("Get Monitor Info Exception", e);
        }

        return null;
    }

    private static String getReturnType(Object returnValue) {
        if (returnValue == null) {
            return Constants.RETURN_VOID;
        }
        return returnValue.getClass().getName();
    }

    private static String[] getArgTypes(Object... args) {
        String argTypes[] = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass().getName();
        }

        return argTypes;
    }

}
