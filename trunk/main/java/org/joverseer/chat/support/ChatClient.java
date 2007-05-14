package org.joverseer.chat.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.joverseer.chat.domain.Message;
import org.joverseer.chat.domain.MessageTypeEnum;
import org.joverseer.chat.domain.User;


public class ChatClient implements Runnable {

    protected DataInputStream i;
    protected DataOutputStream o;
    
    ArrayList<MessageReceiver> receivers = new ArrayList<MessageReceiver>();

    User user;
    
    protected Thread listener;

    public ChatClient(User user, InputStream i, OutputStream o) {
        this.i = new DataInputStream(new BufferedInputStream(i));
        this.o = new DataOutputStream(new BufferedOutputStream(o));
        listener = new Thread(this);
        this.user = user;
        listener.start();
    }
    
    public void addMessageReceiver(MessageReceiver mr) {
        receivers.add(mr);
    }

    public void run() {
        try {
            while (true) {
                String line = i.readUTF();
                if (line != null) {
                    System.out.println("client received " + line);
                    for (MessageReceiver mr : receivers) {
                        Message msg = Message.messageFromString(line);
                        mr.messageReceived(msg);
                    }
                }
                //Thread.sleep(100);

            }
        } catch (Exception ex) {
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
        sendMessage(msg, MessageTypeEnum.Text);
    }

    public void sendMessage(String msg, MessageTypeEnum type) {
        try {
            Message msgObj = new Message();
            msgObj.setUser(new User(user.getUsername()));
            msgObj.setContents(msg);
            msgObj.setType(type);
            System.out.println("client sending " + msg);
            o.writeUTF(Message.stringFromMessage(msgObj));
            o.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            listener.stop();
        }
    }
    
    public static ChatClient connect(String server, int port, User user) throws Exception {
        Socket s = new Socket(server, port);
        return new ChatClient(user, s.getInputStream(), s.getOutputStream());
    }

}
