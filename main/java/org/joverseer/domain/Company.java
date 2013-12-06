package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.support.infoSources.InfoSource;


/**
 * Stores company information for the turn.
 * 
 * @author Marios Skounakis
 *
 */
public class Company implements IHasMapLocation, Serializable {
    private static final long serialVersionUID = 8506114031233392593L;
    int x;
    int y;
    String commander;
    ArrayList<String>members = new ArrayList<String>();
    InfoSource infoSource;
    
    @Override
	public int getX() {
        return this.x;
    }

    
    public void setX(int x) {
        this.x = x;
    }

    
    @Override
	public int getY() {
        return this.y;
    }

    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getHexNo() {
        return this.x * 100 + this.y;
    }
    
    public void setHexNo(int hexNo) {
        this.x = hexNo / 100;
        this.y = hexNo % 100;
    }
    
    public void addMember(String member) {
        this.members.add(member);
    }


    
    public String getCommander() {
        return this.commander;
    }


    
    public void setCommander(String commander) {
        this.commander = commander;
    }


    
    public ArrayList<String> getMembers() {
        return this.members;
    }


    
    public InfoSource getInfoSource() {
        return this.infoSource;
    }


    
    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }
    
    public String getMemberStr() {
        String ret = "";
        for (String m : getMembers()) {
            ret += (!ret.equals("") ? ", " : "") + m;
        }
        return ret;
    }
}
