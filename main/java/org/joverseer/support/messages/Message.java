package org.joverseer.support.messages;


public class Message {
    MessageTypeEnum type;
    String msg;
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public MessageTypeEnum getType() {
        return type;
    }
    
    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

    public Message(MessageTypeEnum type, String msg) {
        super();
        this.type = type;
        this.msg = msg;
    }
    
    
}
