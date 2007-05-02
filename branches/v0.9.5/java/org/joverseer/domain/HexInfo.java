package org.joverseer.domain;

import java.util.ArrayList;
import java.io.Serializable;


public class HexInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4414153702938844460L;
    int x;
    int y;
    boolean visible;
    boolean hasPopulationCenter;
    ClimateEnum climate;

    ArrayList nationSources = new ArrayList();

    public boolean getHasPopulationCenter() {
        return hasPopulationCenter;
    }

    public void setHasPopulationCenter(boolean hasPopulationCenter) {
        this.hasPopulationCenter = hasPopulationCenter;
    }

    public ArrayList getNationSources() {
        return nationSources;
    }

    public void setNationSources(ArrayList nationSources) {
        this.nationSources = nationSources;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

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

    public void setHexNo(int hexNo) {
        setX(hexNo / 100);
        setY(hexNo % 100);
    }

    public int getHexNo() {
        return getX()*100 + getY();
    }
    
    

    
    public ClimateEnum getClimate() {
        return climate;
    }

    
    public void setClimate(ClimateEnum climate) {
        this.climate = climate;
    }

    public void merge(HexInfo hi) {
        if (hi.getHexNo() != getHexNo()) {
            throw new RuntimeException("incompatible HexInfos due to hex no");
        }
        if (getNationSources().size() > 0) {
            if (hi.getVisible() != getVisible())
                throw new RuntimeException("incompatible HexInfos due to visible");

            if (hi.getHasPopulationCenter() != getHasPopulationCenter()) {
                // either one is true, make true
                // (prolly talking about a hidden pc here)
                setHasPopulationCenter(true);
            }
        } else {
            setVisible(hi.getVisible());
            setHasPopulationCenter(hi.getHasPopulationCenter());
        }
        if (!getNationSources().contains(hi.getNationSources())) {
            getNationSources().add(hi.getNationSources());
        }
    }

    public boolean removeNationSource(int nationNo) {
        getNationSources().remove(nationNo);
        return getNationSources().size() > 0;
    }


}
