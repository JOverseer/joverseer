package org.joverseer.ui.domain;

import java.util.HashMap;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;

/**
 * Wraps information about spell casters for the spellcaster list view
 * 
 * @author Marios Skounakis
 */
public class SpellcasterWrapper implements IHasMapLocation, IBelongsToNation {
    String character;
    int hexNo;
    int artifactBonus;
    Integer nationNo;
    Integer mageRank;
    HashMap<Integer, Integer> proficiency = new HashMap<Integer, Integer>();
    
    public String getCharacter() {
        return this.character;
    }
    
    public void setCharacter(String character) {
        this.character = character;
    }
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public void setProficiency(int spellNo, int proficiency) {
        this.proficiency.put(spellNo, proficiency);
    }
    
    public Integer getProficiency(int spellNo) {
        return this.proficiency.get(spellNo);
    }

    @Override
	public int getX() {
        return getHexNo() / 100;
    }

    @Override
	public int getY() {
        return getHexNo() % 100;
    }
    
    public HashMap<Integer, Integer> getProficiencies() {
        return this.proficiency;
    }

    
    public int getArtifactBonus() {
        return this.artifactBonus;
    }

    
    public void setArtifactBonus(int artifactBonus) {
        this.artifactBonus = artifactBonus;
    }

    
    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }

    
    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public Integer getMageRank() {
        return this.mageRank;
    }

    
    public void setMageRank(Integer mageRank) {
        this.mageRank = mageRank;
    }

    
    
}
