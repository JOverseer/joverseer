package org.joverseer.tools.armySizeEstimator;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;

/**
 * Helper class used to estimate the number of men (or ships, in case of navies) 
 * that correspond to a give army size (e.g. small, large, etc). 
 * 
 * @author Marios Skounakis
 *
 */
public class ArmySizeEstimate {
    public static String ARMY_TYPE = "Army";
    public static String NAVY_TYPE = "Navy";
    ArmySizeEnum size;
    Integer min = null;
    Integer max = null;
    int countKnown = 0;
    int countUnknown = 0;
    String type;
 
    public ArmySizeEstimate(String type, ArmySizeEnum size) {
        super();
        this.size = size;
        this.type = type;
    }
    
    public void addArmy(Army a) {
        if (a.getSize() != getSize()) return;
        if (a.isNavy() && !this.type.equals(NAVY_TYPE)) return;
        if (!a.isNavy() && !this.type.equals(ARMY_TYPE)) return;
        int troops = a.computeNumberOfMen();
        if (troops == 0) {
        	troops = a.getTroopCount();
        }
        if (a.isNavy()) {
            troops = a.computeNumberOfShips();
        }
        if (troops > 0) {
            if (this.min == null) {
                this.min = troops;
            }
            if (this.max == null) {
                this.max = troops;
            }
            this.min = Math.min(this.min, troops);
            this.max = Math.max(this.max, troops);
            this.countKnown++;
        } else {
            this.countUnknown++;
        }
    }

    public int getCountKnown() {
        return this.countKnown;
    }
    
    public void setCountKnown(int countKnown) {
        this.countKnown = countKnown;
    }
    
    public int getCountUnknown() {
        return this.countUnknown;
    }
    
    public void setCountUnknown(int countUknown) {
        this.countUnknown = countUknown;
    }
    
    public Integer getMax() {
        return this.max;
    }
    
    public void setMax(Integer max) {
        this.max = max;
    }
    
    public Integer getMin() {
        return this.min;
    }
    
    public void setMin(Integer min) {
        this.min = min;
    }
    
    public ArmySizeEnum getSize() {
        return this.size;
    }
    
    public void setSize(ArmySizeEnum size) {
        this.size = size;
    }

    
    public String getType() {
        return this.type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    
}
