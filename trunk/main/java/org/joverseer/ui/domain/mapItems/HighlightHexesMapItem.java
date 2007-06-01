package org.joverseer.ui.domain.mapItems;

import java.util.ArrayList;


/**
 * Handles the drawing of a list of highlighted hexes. Used to draw the various hightlight
 * tools.
 * 
 * @author Marios Skounakis
 */
public class HighlightHexesMapItem extends AbstractMapItem {
    private static final long serialVersionUID = -3111521381662784087L;
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
