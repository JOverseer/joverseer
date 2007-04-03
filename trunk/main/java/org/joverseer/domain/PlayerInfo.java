package org.joverseer.domain;

import java.io.Serializable;
import java.util.Date;


public class PlayerInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1488014589069610748L;
    int nationNo;
    String playerName;
    String accountNo;
    String secret;
    String dueDate;
    int turnVersion = 1;
    
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public String getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    
    public int getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }

    
    public int getTurnVersion() {
        return turnVersion;
    }

    
    public void setTurnVersion(int turnVersion) {
        this.turnVersion = turnVersion;
    }
    
    
    
    
}
