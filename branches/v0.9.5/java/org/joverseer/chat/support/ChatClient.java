package org.joverseer.chat.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ChatClient implements Runnable {

    protected DataInputStream i;
    protected DataOutputStream o;

    protected Thread listener;

    public ChatClient(String title, InputStream i, OutputStream o) {
        this.i = new DataInputStream(new BufferedInputStream(i));
        this.o = new DataOutputStream(new BufferedOutputStream(o));
        listener = new Thread(this);
        listener.start();
    }

    public void run() {
        try {
            while (true) {
                String line = i.readUTF();
                // todo generate message and consume
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            listener = null;
            try {
                o.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) {
        try {
            o.writeUTF(msg);
            o.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            listener.stop();
        }
    }

}
