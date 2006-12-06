package org.joverseer.chat.support;

import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 2 Дек 2006
 * Time: 12:05:29 рм
 * To change this template use File | Settings | File Templates.
 */
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
            listener.stop ();
        }
    }

}
