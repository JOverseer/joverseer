package org.joverseer.support.infoSources.spells;

import java.util.ArrayList;

import org.joverseer.support.infoSources.InfoSource;


public class DerivedFromSpellInfoSource extends InfoSource {
    ArrayList<InfoSource> otherInfoSources = new ArrayList<InfoSource>(); 
    
    int nationNo;
    String casterName;
    int hexNo;
    
    public String getCasterName() {
        return casterName;
    }
    
    public void setCasterName(String casterName) {
        this.casterName = casterName;
    }
    
    public int getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    
    public int getHexNo() {
        return hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public void addInfoSource(InfoSource is) {
        otherInfoSources.add(is);
    }
    
    public ArrayList<InfoSource> getOtherInfoSources() {
        return otherInfoSources;
    }
    
    public String getSpell() {
        return null;
    }
}
