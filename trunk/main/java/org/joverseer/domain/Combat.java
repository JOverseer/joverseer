package org.joverseer.domain;

import java.io.Serializable;
import java.util.HashMap;


public class Combat implements IHasMapLocation, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 9195835736979973465L;
    int x;
    int y;
    
    HashMap<Integer, String> narrations = new HashMap<Integer, String>();
    
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    
    public void setHexNo(int hexNo) {
        x = hexNo / 100;
        y = hexNo % 100;
    }
    
    public int getHexNo() {
        return x * 100 + y;
    }

    public HashMap<Integer, String> getNarrations() {
        return narrations;
    }
    
    public String getNarrationForNation(int nationNo) {
        return narrations.get(nationNo);
    }
    
    public void addNarration(int nationNo, String narration) {
        narrations.put(nationNo, narration);
    }
    
}
