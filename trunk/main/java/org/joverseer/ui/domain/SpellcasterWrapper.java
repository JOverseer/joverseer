package org.joverseer.ui.domain;

import java.util.HashMap;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;


public class SpellcasterWrapper implements IHasMapLocation, IBelongsToNation {
    String character;
    int hexNo;
    int artifactBonus;
    Integer nationNo;
    Integer mageRank;
    HashMap<Integer, Integer> proficiency = new HashMap<Integer, Integer>();
    
    public String getCharacter() {
        return character;
    }
    
    public void setCharacter(String character) {
        this.character = character;
    }
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public void setProficiency(int spellNo, int proficiency) {
        this.proficiency.put(spellNo, proficiency);
    }
    
    public Integer getProficiency(int spellNo) {
        return proficiency.get(spellNo);
    }

    public int getX() {
        return getHexNo() / 100;
    }

    public int getY() {
        return getHexNo() % 100;
    }
    
    public HashMap<Integer, Integer> getProficiencies() {
        return proficiency;
    }

    
    public int getArtifactBonus() {
        return artifactBonus;
    }

    
    public void setArtifactBonus(int artifactBonus) {
        this.artifactBonus = artifactBonus;
    }

    
    public Integer getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public Integer getMageRank() {
        return mageRank;
    }

    
    public void setMageRank(Integer mageRank) {
        this.mageRank = mageRank;
    }

    
    
}
