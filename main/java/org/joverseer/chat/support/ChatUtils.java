package org.joverseer.chat.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.joverseer.chat.domain.Message;
import org.joverseer.chat.domain.User;
import org.joverseer.domain.Order;


public class ChatUtils {
    public static Object readObject(DataInputStream s) {
        try {
            byte[] in = new byte[10000];
            int i = s.read(in, 0, 10000);
            String str = new String(in, 0, i);
            System.out.println("Read message with size " + i + " " + str);
            
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(in, 0, i));
            Object ret = is.readObject();
            is.close();
            return ret;
        }
        catch (Exception exc) {
            exc.printStackTrace();
        };
        return null;
    }
    
    public static Object writeObject(Object obj, DataOutputStream s) {
        try {
            byte[] out = new byte[10000];
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            ObjectOutputStream is = new ObjectOutputStream(outs);
            is.writeObject(obj);
            is.close();
            out = outs.toByteArray();
            s.write(out, 0, out.length);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        };
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        ChatServer cs = new ChatServer();
        Thread t = new Thread(cs);
        t.start();
        
        ChatClient cc = ChatClient.connect("wks2633", 9600, new User("mscoon"));
        cc.addMessageReceiver(new MessageReceiver() {

            public void messageReceived(Message msg) {
                System.out.println(msg.getContents().toString());
            }
            
        });
        
        cc.sendMessage("test");
        Order o = new Order(new org.joverseer.domain.Character());
        o.setOrderNo(100);
        cc.sendMessage(o);
    }
}
