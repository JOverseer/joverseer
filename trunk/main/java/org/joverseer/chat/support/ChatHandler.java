package org.joverseer.chat.support;

import java.net.Socket;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;


public class ChatHandler extends Thread {
    protected static Vector handlers = new Vector();

    protected Socket s;
    protected DataInputStream i;
    protected DataOutputStream o;

    public ChatHandler(Socket s) throws IOException {
        this.s = s;
        try {
            i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            o = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        }
        catch (Exception exc) {};
    }

    public void run() {
        try {
            handlers.addElement(this);
            while (true) {
                //String msg = i.readUTF();
                //String msg = (String)ChatUtils.readObject(i);
                Object obj = (Object)ChatUtils.readObject(i);
                System.out.println("handler received " + obj);
                broadcast(obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            handlers.removeElement(this);
            try {
                s.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected static void broadcast(Object message) {
        synchronized (handlers) {
            System.out.println("broadcasting " + message);
            Enumeration e = handlers.elements();
            while (e.hasMoreElements()) {
                ChatHandler c = (ChatHandler) e.nextElement();
                try {
                    synchronized (c.o) {
                        System.out.println("handler sending to client");
                        //c.o.writeUTF(message);
                        ChatUtils.writeObject(message, c.o);
                    }
                    c.o.flush();
                } catch (IOException ex) {
                    c.stop();
                    
                }
            }
        }
    }
}
