package org.joverseer.domain;

import java.io.Serializable;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;


public class Artifact implements Serializable, IHasMapLocation {

    private static final long serialVersionUID = -5267111841358845294L;
    int number;
    String name;
    int hexNo;
    String owner;
    int x;
    int y;
    
    InfoSource infoSource;
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
        x = hexNo / 100;
        y = hexNo % 100;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getOwner() {
        return (owner == null ? "" : owner);
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    
    public InfoSource getInfoSource() {
        return infoSource;
    }

    
    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public int getX() {
        return x;
    }

    
    public void setX(int x) {
        this.x = x;
    }

    
    public int getY() {
        return y;
    }

    
    public void setY(int y) {
        this.y = y;
    }
    
    
    public String getInfoSourceDescr() {
        String txt = "";
        if (DerivedFromSpellInfoSource.class.isInstance(infoSource)) {
            txt = ((DerivedFromSpellInfoSource)infoSource).getSpell() + " - " + ((DerivedFromSpellInfoSource)infoSource).getHexNo() + " - " + ((DerivedFromSpellInfoSource)infoSource).getCasterName();
            for (InfoSource is : ((DerivedFromSpellInfoSource)infoSource).getOtherInfoSources()) {
                if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                    txt += ", " + ((DerivedFromSpellInfoSource)is).getSpell() + " - " + ((DerivedFromSpellInfoSource)infoSource).getHexNo() + " - " + ((DerivedFromSpellInfoSource)is).getCasterName();
                }
            }
        }
        return txt;
    }
    
    public void setInfoSourceDescr() {
        
    }
    
}
