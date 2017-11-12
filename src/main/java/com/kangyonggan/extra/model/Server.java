package com.kangyonggan.extra.model;

import com.alibaba.fastjson.JSONObject;
import com.kangyonggan.extra.util.MonitorUtil;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Server {

    private String ip;

    private Integer port;

    private Socket socket;

    private BufferedWriter write;

    private boolean isRuning;

    private long lastSendTime;

    private Object lock;

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
                        Thread.sleep(30000);
                    } catch (Exception e) {
                        MonitorUtil.error("Check Connect Exception When Sleep", e);
                    }

                    checkConnect();
                    System.out.println("check connect");
                }
            }
        }.start();

        // sendPackageThread
        new Thread() {
            public void run() {
                while (true) {
                    MonitorInfo monitor = MonitorUtil.getMonitorInfo();
                    System.out.println("take a monitor info " + monitor);
                    if (monitor == null) {
                        try {
                            synchronized (lock) {
                                System.out.println("wait");
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
            MonitorUtil.putMonitorInfo(monitorInfo);
            return;
        }

        try {
            write.write(JSONObject.toJSONString(monitorInfo));

            write.flush();
            socket.shutdownOutput();

            System.out.println("send success");
            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            MonitorUtil.error("Send Monitor Info Exception", e);
            isRuning = false;
            if (retryCount > 0) {
                send(monitorInfo, --retryCount);
            }

            // put back to queue
            MonitorUtil.putMonitorInfo(monitorInfo);
        }
    }

    private void checkConnect() {
        if (System.currentTimeMillis() - lastSendTime < 28000) {
            return;
        }
        try {
            write.write("00000000");

            write.flush();
            socket.shutdownOutput();

            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            MonitorUtil.error("Check Connect Exception When Send Heartbeat", e);
            getConnect();
        }
    }

    public void getConnect() {
        try {
            socket = new Socket(ip, port);
            write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            isRuning = true;
            System.out.println("get a connect success");
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
            System.out.println("unlock success");
        } catch (Exception e) {
            MonitorUtil.error("Unlock Exception", e);
        }
    }
}
