package com.kangyonggan.extra.util;

import com.kangyonggan.extra.model.MonitorInfo;
import com.kangyonggan.extra.model.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class MonitorUtil {

    private static List<Server> servers = new ArrayList();

    public static void monitor(String serversStr, String app, String type, String handlePackage, String packageName,
                               String className, String methodName, Object... args) {
        MonitorInfo monitorInfo = new MonitorInfo(app, type, handlePackage, packageName, className, methodName, args);

        if (servers.isEmpty()) {
            try {
                initServers(serversStr, monitorInfo);
            } catch (Exception e) {
                monitorInfo.error("Monitor Servers Init Exception", e);
                return;
            }
        }

        for (Server server : servers) {
            // Send To Server
            if (server.send(monitorInfo, 3)) {
                break;
            }
            monitorInfo.error("Method Information Send to [" + server.getIp() + ":" + server.getPort() + "] Failure");
        }
    }

    private static void initServers(String serversStr, MonitorInfo monitorInfo) throws Exception {
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
                    monitorInfo.error("Init Server Exception", e);
                }
            }
        }
    }

}
