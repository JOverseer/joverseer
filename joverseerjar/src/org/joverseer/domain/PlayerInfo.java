package org.joverseer.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Stores information about a player
 * 
 * In addition to information read from the xml turn, it stores the latest 
 * turn version, file, and date for the orders of this nation/player.
 *  
 * @author Marios Skounakis
 */
public class PlayerInfo implements IBelongsToNation, Serializable {
    private static final long serialVersionUID = -1488014589069610748L;
    int nationNo;
    String playerName;
    String accountNo;
    String secret;
    String dueDate;
    int turnVersion = 1;
    String lastOrderFile = null;
    Date ordersSentOn = null;
    
    public String getAccountNo() {
        return this.accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public String getDueDate() {
        return this.dueDate;
    }
    
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    
    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }
    
    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getSecret() {
        return this.secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }

    
    public int getTurnVersion() {
        return this.turnVersion;
    }

    
    public void setTurnVersion(int turnVersion) {
        this.turnVersion = turnVersion;
    }

    
    public String getLastOrderFile() {
        return this.lastOrderFile;
    }

    
    public void setLastOrderFile(String lastOrderFile) {
        this.lastOrderFile = lastOrderFile;
    }

    
    public Date getOrdersSentOn() {
        return this.ordersSentOn;
    }

    
    public void setOrdersSentOn(Date ordersSentOn) {
        this.ordersSentOn = ordersSentOn;
    }
    
    
    
    
}
