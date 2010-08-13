package org.joverseer.support.infoSources.spells;

import java.util.ArrayList;

import org.joverseer.support.infoSources.InfoSource;

/**
 * Information derived from a spell (usually divination spells).
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * - the hex associated with the item
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromSpellInfoSource extends InfoSource {
    private static final long serialVersionUID = 1876576030752835266L;

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
    
    
    public String getDescription() {
    	String str = "";
    	str += getSpell() + " " + getCasterName();
    	for (InfoSource dis : getOtherInfoSources()) {
    		str += "," + dis.getDescription();
    	}
    	return str;
    }
    
    public boolean equals(Object obj) {
        if (this.getClass().isInstance(obj)) {
            DerivedFromSpellInfoSource is = (DerivedFromSpellInfoSource)obj;
            return is.getNationNo() == getNationNo() && is.getCasterName().equals(getCasterName()) && is.getHexNo() == getHexNo();
        }
        return super.equals(obj);
    }
    
    public boolean contains(Object obj) {
        if (this.equals(obj)) return true;
        for (Object ois : getOtherInfoSources()) {
            if (ois.equals(obj)) return true;
        }
        return false;
    }
}
