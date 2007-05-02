package org.joverseer.ui.domain.mapItems;

import java.util.ArrayList;


public class HighlightHexesMapItem extends AbstractMapItem {
    ArrayList<Integer> hexesToHighlight = new ArrayList<Integer>();
    String description;
    
    public void setDescription(String descr) {
        this.description = descr;
    }
    
    public String getDescription() {
        return description;
    }

    
    public ArrayList<Integer> getHexesToHighlight() {
        return hexesToHighlight;
    }

    
    public void setHexesToHighlight(ArrayList<Integer> hexesToHighlight) {
        this.hexesToHighlight = hexesToHighlight;
    }
    
    public void addHex(int hexNo) {
        getHexesToHighlight().add(hexNo);
    }
}
