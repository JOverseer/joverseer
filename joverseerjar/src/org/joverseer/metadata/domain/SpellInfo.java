package org.joverseer.metadata.domain;

import java.io.Serializable;


/**
 * Background information for game spells. It holds:
 * - the spell name
 * - the spell number
 * - the order number that must be used to cast the spell
 * - information for casting the spell (required info, requirements, description)
 * - the spell difficulty
 * - the spell list name
 * 
 * @author Marios Skounakis
 *
 */
public class SpellInfo implements Serializable {
    private static final long serialVersionUID = 2027799051162482988L;
    String name;
    Integer number;
    Integer orderNumber;
    String requiredInfo;
    String requirements;
    String difficulty;
    String description;
    String list;
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Integer getNumber() {
        return this.number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public Integer getOrderNumber() {
        return this.orderNumber;
    }
    
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public String getRequiredInfo() {
        return this.requiredInfo;
    }
    
    public void setRequiredInfo(String requiredInfo) {
        this.requiredInfo = requiredInfo;
    }
    
    public String getRequirements() {
        return this.requirements;
    }
    
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
    
    
    
    public String getList() {
		return this.list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public SpellDifficultyEnum getDifficultyLevel() {
        if (getDifficulty().equals("E")) return SpellDifficultyEnum.Easy;
        if (getDifficulty().equals("A")) return SpellDifficultyEnum.Average;
        if (getDifficulty().equals("H")) return SpellDifficultyEnum.Hard;
        return null;
    }
}
