package com.kangyonggan.extra.core.model;

import com.alibaba.fastjson.JSONObject;
import com.kangyonggan.extra.core.util.MonitorUtil;
import com.kangyonggan.extra.core.util.StringUtil;

import java.io.OutputStream;
import java.net.Socket;

public class Server {

    private String ip;

    private Integer port;

    private Socket socket;

    private OutputStream out;

    private boolean isRuning;

    private long lastSendTime;

    private Object lock;

    private long heartbeatInterval = 30000;

    public Server(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        lock = new Object();
        init();
    }

    private void init() {
        getConnect();

        // checkConnectThread
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(heartbeatInterval);
                    } catch (Exception e) {
                        MonitorUtil.error("Check Connect Exception When Sleep", e);
                    }

                    checkConnect();
                }
            }
        }.start();

        // sendPackageThread
        new Thread() {
            public void run() {
                while (true) {
                    MonitorInfo monitor = MonitorUtil.getMonitorInfo();
                    if (monitor == null) {
                        try {
                            synchronized (lock) {
                                lock.wait();
                            }
                        } catch (InterruptedException e) {
                            MonitorUtil.error("Send Package Thread Wait Exception", e);
                        }
                    } else {
                        send(monitor, 1);
                    }
                }
            }
        }.start();
    }

    public void send(MonitorInfo monitorInfo, int retryCount) {
        if (!isRuning) {
            getConnect();
        }

        if (!isRuning) {
            MonitorUtil.error("Send Package Thread Wait Exception", monitorInfo);
            // put back to queue
//            MonitorUtil.putMonitorInfo(monitorInfo);
            return;
        }

        try {
            byte body[] = JSONObject.toJSONString(monitorInfo).getBytes();
            out.write(StringUtil.leftPad(String.valueOf(body.length), 8, "0").getBytes());
            out.write(body);
            out.flush();

            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            MonitorUtil.error("Send Monitor Info Exception", e);
            isRuning = false;
            if (retryCount > 0) {
                send(monitorInfo, --retryCount);
            }

            // put back to queue
//            MonitorUtil.putMonitorInfo(monitorInfo);
        }
    }

    private void checkConnect() {
        if (System.currentTimeMillis() - lastSendTime < heartbeatInterval) {
            return;
        }
        try {
            out.write("00000000".getBytes());
            out.flush();

            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            MonitorUtil.error("Check Connect Exception When Send Heartbeat", e);
            getConnect();
        }
    }

    public void getConnect() {
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            isRuning = true;
        } catch (Exception e) {
            MonitorUtil.error("Get Connect Exception", e);
            isRuning = false;
        }
    }

    public void unlock() {
        try {
            synchronized (lock) {
                lock.notifyAll();
            }
        } catch (Exception e) {
            MonitorUtil.error("Unlock Exception", e);
        }
    }
}
