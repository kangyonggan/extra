package com.kangyonggan.extra.model;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Server extends Thread {

    private String ip;

    private Integer port;

    private Socket socket;

    private InputStream in;

    private OutputStream out;

    private boolean isRuning;

    public Server(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        init();
    }

    private void init() {
        getConnect();
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(30000);
                checkConnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean send(MonitorInfo monitorInfo, int retryCount) {
        if (!isRuning) {
            checkConnect();
        }

        if (!isRuning) {
            return false;
        }

        try {
            String json = JSON.toJSONString(monitorInfo);
            out.write(json.getBytes());
            out.flush();
            socket.shutdownOutput();

            return response();
        } catch (Exception e) {
            isRuning = false;
            if (retryCount > 0) {
                return send(monitorInfo, --retryCount);
            }
            return false;
        }
    }

    private boolean response() throws Exception {
        byte[] buff = new byte[1024];
        int len;
        StringBuilder resp = new StringBuilder();
        while ((len = in.read(buff)) != -1) {
            resp.append(new String(buff, 0, len));
        }

        return "success".equals(resp.toString());
    }

    private void checkConnect() {
        try {
            out.write("00000000".getBytes());
            out.flush();
            socket.shutdownOutput();

            if (!response()) {
                getConnect();
            }
        } catch (Exception e) {
            getConnect();
        }
    }

    public void getConnect() {
        try {
            socket = new Socket(ip, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            isRuning = true;
        } catch (Exception e) {
            isRuning = false;
        }
    }
}
