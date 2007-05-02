package org.joverseer.tools.infoCollectors.artifacts;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.springframework.richclient.application.Application;


public class ArtifactWrapper implements IHasMapLocation, IBelongsToNation, IHasTurnNumber {
    int hexNo;
    Integer nationNo;
    int turnNo;
    
    String name;
    int number;
    String owner;
    String power1;
    String power2;
    InfoSource infoSource;
    String alignment;
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getPower1() {
        return power1;
    }
    
    public void setPower1(String power1) {
        this.power1 = power1;
    }
    
    public String getPower2() {
        return power2;
    }
    
    public void setPower2(String power2) {
        this.power2 = power2;
    }
    
    public int getTurnNo() {
        return turnNo;
    }
    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public int getX() {
        return getHexNo() / 100;
    }

    public int getY() {
        return getHexNo() % 100;
    }

    
    public InfoSource getInfoSource() {
        return infoSource;
    }

    
    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public String getAlignment() {
    	return alignment;
    }
    
    public void setAlignment(String alignment) {
    	this.alignment = alignment;
    }

    }
