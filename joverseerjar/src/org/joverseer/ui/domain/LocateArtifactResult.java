package org.joverseer.ui.domain;

import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;

/**
 * Wraps information from LAs and LATs to show in appropriate table
 * 
 * @author Marios Skounakis
 */
public class LocateArtifactResult implements IHasMapLocation, IHasTurnNumber {

    int hexNo;
    String spellName;
    String caster;
    int artifactNo;
    String artifactName;
    String artifactPowers;
    int turnNo;
    String owner;

    public String getArtifactName() {
        return this.artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public int getArtifactNo() {
        return this.artifactNo;
    }

    public void setArtifactNo(int artifactNo) {
        this.artifactNo = artifactNo;
    }

    public String getArtifactPowers() {
        return this.artifactPowers;
    }

    public void setArtifactPowers(String artifactPowers) {
        this.artifactPowers = artifactPowers;
    }

    public String getCaster() {
        return this.caster;
    }

    public void setCaster(String caster) {
        this.caster = caster;
    }

    public int getHexNo() {
        return this.hexNo;
    }

    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    public String getSpellName() {
        return this.spellName;
    }

    public void setSpellName(String spellName) {
        this.spellName = spellName;
    }

    @Override
	public int getTurnNo() {
        return this.turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    @Override
	public int getX() {
        return getHexNo() / 100;
    }

    @Override
	public int getY() {
        return getHexNo() % 100;
    }


    public String getOwner() {
        return this.owner;
    }


    public void setOwner(String owner) {
        this.owner = owner;
    }


}
