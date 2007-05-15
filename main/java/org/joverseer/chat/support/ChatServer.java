package org.joverseer.chat.support;

import org.joverseer.chat.support.ChatHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;



public class ChatServer implements Runnable {
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
    
    public ChatServer() {};
    
    public ChatServer(int p) {
        port = p;
    }
    
    

    
    public int getPort() {
        return port;
    }

    
    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        ChatServer cs = new ChatServer();
        cs.run();
    }
    
}
