package org.joverseer.domain;


public class ArmySizeEstimate {
    ArmySizeEnum size;
    Integer min = null;
    Integer max = null;
    int countKnown = 0;
    int countUnknown = 0;

    public ArmySizeEstimate(ArmySizeEnum size) {
        super();
        this.size = size;
    }
    
    public void addArmy(Army a) {
        if (a.getSize() == getSize()) {
            int troops = a.computeNumberOfMen();
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

    
}
