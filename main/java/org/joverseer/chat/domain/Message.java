package org.joverseer.chat.domain;

import java.io.Serializable;


public class Message implements Serializable {
    private static final long serialVersionUID = -1833593097480940088L;
    Object contents;
    User user;
    boolean system;
    
    public Object getContents() {
        return contents;
    }
    
    public void setContents(Object contents) {
        this.contents = contents;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    
    public boolean isSystem() {
        return system;
    }

    
    public void setSystem(boolean system) {
        this.system = system;
    }
    
    
}
