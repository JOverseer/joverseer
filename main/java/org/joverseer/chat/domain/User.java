package org.joverseer.chat.domain;

import java.io.Serializable;


public class User implements Serializable {
    private static final long serialVersionUID = 23971189333842676L;
    String username;
    
    
    public User(String username) {
        super();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    
}
