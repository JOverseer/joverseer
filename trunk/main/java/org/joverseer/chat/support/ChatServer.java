package org.joverseer.chat.support;

import org.joverseer.chat.support.ChatHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 1 Δεκ 2006
 * Time: 11:37:31 μμ
 * To change this template use File | Settings | File Templates.
 */

public class ChatServer {
    private int port = 9600;

    public void run() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(socket);
                handler.start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer cs = new ChatServer();
        cs.run();
    }
}
