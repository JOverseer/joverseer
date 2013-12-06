package org.joverseer.support.readers.pdf;


/**
 * Stores information about artifacts
 * 
 * @author Marios Skounakis
 */
public class ArtifactWrapper {
    int number;
    String name;
    int hexNo;
    String power;
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }

    
    public String getPower() {
        return this.power;
    }

    
    public void setPower(String power) {
        this.power = power;
    }
    
    
}       
