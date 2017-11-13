package com.kangyonggan.extra.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MonitorServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9917);

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream in = socket.getInputStream();
            byte buff[] = new byte[1024];
            int len;
            StringBuilder req = new StringBuilder();
            while ((len = in.read(buff)) != -1) {
                req.append(new String(buff, 0, len));
            }

            System.out.println("request:" + req);

            OutputStream out = socket.getOutputStream();

            out.write("success".getBytes());

            out.flush();
            socket.shutdownOutput();
            System.out.println("resped!");
        }
    }

}
