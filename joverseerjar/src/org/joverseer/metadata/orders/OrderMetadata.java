package org.joverseer.metadata.orders;

import java.io.Serializable;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

/**
 * Order metadata. It holds information about orders:
 * - the order name
 * - the order code
 * - the order number
 * - the order difficulty
 * - the requirements for executing the order
 * - the parameters required to fill in for this order
 * - the skill requirements for this order
 * 
 * @author Marios Skounakis
 *
 */
public class OrderMetadata implements Serializable {
    private static final long serialVersionUID = -1253155095462805602L;
	String name;
    String code;
    int number;
    String difficulty;
    String requirement;
    String parameters;
    String skillRequirement;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getRequirement() {
        return this.requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    
    public String getSkillRequirement() {
        return this.skillRequirement;
    }

    
    public void setSkillRequirement(String skillRequirement) {
        this.skillRequirement = skillRequirement;
    }
    
    public boolean charHasRequiredSkill(Character c) {
        if (getSkillRequirement().equals("M") ||
                getSkillRequirement().equals("Move")) {
            return true;
        }
        if (getSkillRequirement().equals("CM") ||
                getSkillRequirement().equals("CS")) {
            return c.getCommand() > 0;
        }
        if (getSkillRequirement().equals("AM") ||
                getSkillRequirement().equals("AS")) {
            return c.getAgent() > 0;
        }
        if (getSkillRequirement().equals("EM") ||
                getSkillRequirement().equals("ES")) {
            return c.getEmmisary() > 0;
        }
        if (getSkillRequirement().equals("MM") ||
                getSkillRequirement().equals("MS")) {
            return c.getMage() > 0;
        }
        if (getSkillRequirement().equals("ECS")) {
        	return c.getEmmisary() > 0 && c.getCommand() > 0;
        }
        return false;
    }
    
    public boolean orderAllowedDueToScoutingSNA(Character c) {
    	if (this.number == 925 || this.number == 905 || this.number == 910 || this.number == 915 || this.number == 920 || this.number == 930) {
    		Nation n = c.getNation();
    		if (n == null) return false;
    		if (n.hasSna(SNAEnum.ScoutReconAt50)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean orderAllowedForGameType() {
    	if (this.number == 942 || this.number == 960 || this.number == 965) {
    		Game g = GameHolder.instance().getGame();
    		return g.getMetadata().getGameType().equals(GameTypeEnum.gameFA) ||
    			g.getMetadata().getGameType().equals(GameTypeEnum.gameKS);
    	}
    	return true;
    }

}
