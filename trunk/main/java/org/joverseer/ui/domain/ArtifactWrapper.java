package org.joverseer.ui.domain;

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

    public static Container getArtifactWrappers() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();

        MetadataSource ms = new MetadataSource();
        Container aws = new Container(new String[]{"number"});
        for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)g.getMetadata().getArtifacts().getItems()) {
            ArtifactWrapper aw = new ArtifactWrapper();
            aw.setNumber(ai.getNo());
            aw.setName(ai.getName());
            aw.setOwner(ai.getOwner());
            aw.setPower1(ai.getPower1());
            aw.setPower2(ai.getPower2());
            aw.setInfoSource(ms);
            aw.setAlignment(ai.getAlignment());
            aw.setTurnNo(0);
            aws.addItem(aw);
        }
        
        // loop over turns
        for (int i=0; i<=g.getCurrentTurn(); i++) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            for (Artifact a : (ArrayList<Artifact>)t.getContainer(TurnElementsEnum.Artifact).getItems()) {
                ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", a.getNumber());
                aw.setOwner(a.getOwner());
                aw.setHexNo(a.getHexNo());
                aw.setInfoSource(a.getInfoSource());
                aw.setTurnNo(t.getTurnNo());
                Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", aw.getOwner());
                if (c != null) {
                    aw.setNationNo(c.getNationNo());
                    if (c.getNationNo() == null || c.getNationNo() == 0) {
                        c = (Character)g.getMetadata().getCharacters().findFirstByProperty("id", c.getId());
                        if (c != null) {
                            aw.setNationNo(c.getNationNo());
                        }
                    }
                }
            }
            
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                for (Integer aid : c.getArtifacts()) {
                    ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", aid);
                    aw.setOwner(c.getName());
                    aw.setHexNo(c.getHexNo());
                    aw.setInfoSource(c.getInfoSource());
                    aw.setNationNo(c.getNationNo());
                    aw.setTurnNo(t.getTurnNo());
                }
            }
        }
        return aws;
    }
}
