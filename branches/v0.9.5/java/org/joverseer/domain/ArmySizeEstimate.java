package org.joverseer.domain;


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
        if (a.isNavy() && !type.equals(NAVY_TYPE)) return;
        if (!a.isNavy() && !type.equals(ARMY_TYPE)) return;
        int troops = a.computeNumberOfMen();
        if (troops == 0) {
        	troops = a.getTroopCount();
        }
        if (a.isNavy()) {
            troops = a.computeNumberOfShips();
        }
        if (troops > 0) {
            if (min == null) {
                min = troops;
            }
            if (max == null) {
                max = troops;
            }
            min = Math.min(min, troops);
            max = Math.max(max, troops);
            countKnown++;
        } else {
            countUnknown++;
        }
    }

    public int getCountKnown() {
        return countKnown;
    }
    
    public void setCountKnown(int countKnown) {
        this.countKnown = countKnown;
    }
    
    public int getCountUnknown() {
        return countUnknown;
    }
    
    public void setCountUnknown(int countUknown) {
        this.countUnknown = countUknown;
    }
    
    public Integer getMax() {
        return max;
    }
    
    public void setMax(Integer max) {
        this.max = max;
    }
    
    public Integer getMin() {
        return min;
    }
    
    public void setMin(Integer min) {
        this.min = min;
    }
    
    public ArmySizeEnum getSize() {
        return size;
    }
    
    public void setSize(ArmySizeEnum size) {
        this.size = size;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    
}
