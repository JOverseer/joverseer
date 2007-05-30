package org.joverseer.support.readers.pdf;

/**
 * Stores information about anchored ships
 * 
 * @author Marios Skounakis
 */
public class AnchoredShipsWrapper {
    int number;
    String type;
    int hexNo;
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    
}
