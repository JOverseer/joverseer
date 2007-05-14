package org.joverseer.chat.domain;


public class Message {
    static String DELIM = "###";
    String contents;
    User user;
    MessageTypeEnum type;
    
    public String getContents() {
        return contents;
    }
    
    public void setContents(String contents) {
        this.contents = contents;
    }
    
    public MessageTypeEnum getType() {
        return type;
    }
    
    public void setType(MessageTypeEnum type) {
        this.type = type;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public static Message messageFromString(String str) {
        String[] parts = str.split(DELIM);
        if (parts.length != 3) {
            return null;
        }
        Message msg = new Message();
        msg.setUser(new User(parts[0]));
        for (MessageTypeEnum t : MessageTypeEnum.values()) {
            if (parts[1].equals(t.toString())) {
                msg.setType(t);
            }
        }
        msg.setContents(parts[2]);
        return msg;
    }
    
    public static String stringFromMessage(Message msg) {
        return msg.getUser().getUsername() + DELIM + msg.getType().toString() + DELIM + msg.getContents(); 
    }
}
