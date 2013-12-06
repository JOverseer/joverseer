package org.joverseer.domain;

import java.io.Serializable;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;

/**
 * Srores information about an artifact. It only stores current reported information such
 * as the owner, the hex, and the number. The powers are stored in the ArtifactInfo class.
 * 
 * Note that currently only artifacts NOT owned by friendly chars are stored 
 * using this class. This includes aretifacts that:
 * - have been located with LA/LAT spells
 * - have been hidden by a friendly nation
 * 
 * @author Marios Skounakis
 *
 */
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
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
        this.x = hexNo / 100;
        this.y = hexNo % 100;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getOwner() {
        return (this.owner == null ? "" : this.owner);
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    
    public InfoSource getInfoSource() {
        return this.infoSource;
    }

    
    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    @Override
	public int getX() {
        return this.x;
    }

    
    public void setX(int x) {
        this.x = x;
    }

    
    @Override
	public int getY() {
        return this.y;
    }

    
    public void setY(int y) {
        this.y = y;
    }
    
    
	public String getInfoSourceDescr() {
        String txt = "";
        if (DerivedFromSpellInfoSource.class.isInstance(this.infoSource)) {
            txt = ((DerivedFromSpellInfoSource)this.infoSource).getSpell() + " - " + ((DerivedFromSpellInfoSource)this.infoSource).getHexNo() + " - " + ((DerivedFromSpellInfoSource)this.infoSource).getCasterName();
            for (InfoSource is : ((DerivedFromSpellInfoSource)this.infoSource).getOtherInfoSources()) {
                if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                    txt += ", " + ((DerivedFromSpellInfoSource)is).getSpell() + " - " + ((DerivedFromSpellInfoSource)this.infoSource).getHexNo() + " - " + ((DerivedFromSpellInfoSource)is).getCasterName();
                }
            }
        }
        return txt;
    }
    
    public void setInfoSourceDescr() {
        
    }
    
}
