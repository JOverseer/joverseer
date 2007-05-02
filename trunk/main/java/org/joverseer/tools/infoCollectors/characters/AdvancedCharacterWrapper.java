package org.joverseer.tools.infoCollectors.characters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.domain.CompanyWrapper;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;

public class AdvancedCharacterWrapper implements IHasMapLocation, IBelongsToNation, IHasTurnNumber {

    String name;
    int hexNo;
    Integer nationNo;
    int turnNo;
    InfoSource infoSource;
    String id;
    
    boolean isStartChar = false;

    HashMap<String, CharacterAttributeWrapper> attributes = new HashMap<String, CharacterAttributeWrapper>();

    ArrayList<ArtifactWrapper> artifacts = new ArrayList<ArtifactWrapper>();

    Company company;
    Army army;
    
    CharacterDeathReasonEnum deathReason; 
    
    String orderResults;
    
    
    public Army getArmy() {
		return army;
	}
    
    

	public CharacterDeathReasonEnum getDeathReason() {
		return deathReason;
	}



	public void setDeathReason(CharacterDeathReasonEnum deathReason) {
		this.deathReason = deathReason;
	}



	public void setArmy(Army army) {
		this.army = army;
	}

	public HashMap<String, CharacterAttributeWrapper> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, CharacterAttributeWrapper> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(CharacterAttributeWrapper value) {
        CharacterAttributeWrapper cw = getAttribute(value.getAttribute());
        if (cw != null && cw.getTurnNo() > value.getTurnNo())
            return;
        getAttributes().put(value.getAttribute(), value);
    }

    public void setAttributeMax(CharacterAttributeWrapper value) {
        CharacterAttributeWrapper cw = getAttribute(value.getAttribute());
        if (cw != null) {
            if (Integer.class.isInstance(cw.getValue()) && Integer.class.isInstance(value.getValue())) {
                Integer oldValue = (Integer) cw.getValue();
                Integer newValue = (Integer) value.getValue();
                if (newValue > oldValue) {
                    getAttributes().put(value.getAttribute(), value);
                }
                return;
            }
        }
        getAttributes().put(value.getAttribute(), value);
    }

    public CharacterAttributeWrapper getAttribute(String attribute) {
        return getAttributes().get(attribute);
    }
    
    

    public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getHexNo() {
        return hexNo;
    }

    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
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

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public String getOrderResults() {
        return orderResults;
    }

    public void setOrderResults(String orderResults) {
        this.orderResults = orderResults;
    }

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public CharacterAttributeWrapper getCommand() {
        return getAttribute("command");
    }

    public CharacterAttributeWrapper getAgent() {
        return getAttribute("agent");
    }

    public CharacterAttributeWrapper getEmmisary() {
        return getAttribute("emmisary");
    }

    public CharacterAttributeWrapper getMage() {
        return getAttribute("mage");
    }

    public CharacterAttributeWrapper getStealth() {
        return getAttribute("stealth");
    }

    public CharacterAttributeWrapper getChallenge() {
        return getAttribute("challenge");
    }

    public CharacterAttributeWrapper getHealth() {
        return getAttribute("health");
    }

    public ArrayList<ArtifactWrapper> getArtifacts() {
        return artifacts;
    }

    private ArtifactWrapper getArtifact(int i) {
        Collections.sort(artifacts, new Comparator() {

            public int compare(Object o1, Object o2) {
                try {
                    return ((ArtifactWrapper) o1).getNumber() - ((ArtifactWrapper) o2).getNumber();
                } catch (Exception exc) {
                }
                ;
                return 0;
            }
        });
        if (artifacts.size() > i) {
            return artifacts.get(i);
        }
        return null;
    }

    public ArtifactWrapper getA0() {
        return getArtifact(0);
    }

    public ArtifactWrapper getA1() {
        return getArtifact(1);
    }

    public ArtifactWrapper getA2() {
        return getArtifact(2);
    }

    public ArtifactWrapper getA3() {
        return getArtifact(3);
    }

    public ArtifactWrapper getA4() {
        return getArtifact(4);
    }

    public ArtifactWrapper getA5() {
        return getArtifact(5);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return getHexNo() / 100;
    }

    public int getY() {
        return getHexNo() % 100;
    }
    
    public String getTravellingWith() {
    	if (getCompany() != null) {
    		CompanyWrapper cw = new CompanyWrapper(getCompany());
			return "Company: " + cw.getCommander() + " - " + cw.getMemberStr();
    	};
    	if (getArmy() != null) {
    		String chars = "";
    		for (String c : (ArrayList<String>)getArmy().getCharacters()) {
    			chars += (chars.equals("") ? "" : ", ") + c;
    		}
    		return "Army: " + getArmy().getCommanderName() + (chars.equals("") ? "" : " - " + chars); 
    	}
    	return "";
    }



    
    public boolean getStartChar() {
        return isStartChar;
    }



    
    public void setStartChar(boolean isStartChar) {
        this.isStartChar = isStartChar;
    }
 

}
