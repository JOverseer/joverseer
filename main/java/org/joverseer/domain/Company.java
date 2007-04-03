package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.support.infoSources.InfoSource;


public class Company implements IHasMapLocation, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8506114031233392593L;
    int x;
    int y;
    String commander;
    ArrayList<String>members = new ArrayList<String>();
    InfoSource infoSource;
    
    public int getX() {
        return x;
    }

    
    public void setX(int x) {
        this.x = x;
    }

    
    public int getY() {
        return y;
    }

    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getHexNo() {
        return x * 100 + y;
    }
    
    public void setHexNo(int hexNo) {
        x = hexNo / 100;
        y = hexNo % 100;
    }
    
    public void addMember(String member) {
        members.add(member);
    }


    
    public String getCommander() {
        return commander;
    }


    
    public void setCommander(String commander) {
        this.commander = commander;
    }


    
    public ArrayList<String> getMembers() {
        return members;
    }


    
    public InfoSource getInfoSource() {
        return infoSource;
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
