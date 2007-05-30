package org.joverseer.ui.chat;


public class ChatConnection {
    String myIP;
    int myPort;
    String username;
    String peerIP;
    int peerPort;
    
    public int getMyPort() {
        return myPort;
    }
    
    public void setMyPort(int port) {
        this.myPort = port;
    }
    
    public String getMyIP() {
        return myIP;
    }
    
    public void setMyIP(String server) {
        this.myIP = server;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    
    public String getPeerIP() {
        return peerIP;
    }

    
    public void setPeerIP(String peerIP) {
        this.peerIP = peerIP;
    }

    
    public int getPeerPort() {
        return peerPort;
    }

    
    public void setPeerPort(int peerPort) {
        this.peerPort = peerPort;
    }
    
    
    
}
