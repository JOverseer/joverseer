package org.joverseer.chat.support;

import org.joverseer.chat.domain.Message;


public interface MessageReceiver {
    public void messageReceived(Message msg);
    public void messageReceived(String msg);
}
