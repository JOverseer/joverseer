package org.joverseer.ui.domain;

import org.joverseer.metadata.GameTypeEnum;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.Rules;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.factory.Constraints;

/**
 * Object to support the creation of a new game
 * 
 * Implements the PropertyConstraintProvider to support validation
 * 
 * @author Marios Skounakis
 */
public class NewGame implements PropertyConstraintProvider {
    GameTypeEnum gameType;
    Integer number;
    Integer nationNo;
    Rules rules = null;
    String additionalNations;
    boolean newXmlFormat;
    
    
    
    public boolean getNewXmlFormat() {
		return newXmlFormat;
	}

	public void setNewXmlFormat(boolean newXmlFormat) {
		this.newXmlFormat = newXmlFormat;
	}

	public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
    
    

    public String getAdditionalNations() {
		return additionalNations;
	}

	public void setAdditionalNations(String additionalNations) {
		this.additionalNations = additionalNations;
	}

	public PropertyConstraint getPropertyConstraint(String string) {
        if (rules == null) {
            rules = new Rules();
            rules.add("nationNo", Constraints.instance().and(Constraints.instance().gt(0), Constraints.instance().lt(26)));
            rules.add("number", Constraints.instance().gt(0));
        }
        return rules.getPropertyConstraint(string);
    }
}
