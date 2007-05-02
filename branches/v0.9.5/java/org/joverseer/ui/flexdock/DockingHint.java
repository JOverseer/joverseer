package org.joverseer.ui.flexdock;

import org.flexdock.docking.Dockable;


public class DockingHint {
    Dockable parent;
    String location;
    Float splitRatio;
    
    public DockingHint(Dockable parent, String location, Float splitRatio) {
        super();
        this.parent = parent;
        this.location = location;
        this.splitRatio = splitRatio;
    }

    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Dockable getParent() {
        return parent;
    }
    
    public void setParent(Dockable parent) {
        this.parent = parent;
    }
    
    public Float getSplitRatio() {
        return splitRatio;
    }
    
    public void setSplitRatio(Float splitRatio) {
        this.splitRatio = splitRatio;
    }
    
    
}
