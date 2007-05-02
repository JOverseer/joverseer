package org.joverseer.metadata.orders;

import java.io.Serializable;
import org.joverseer.domain.Character;


public class OrderMetadata implements Serializable {
    String name;
    String code;
    int number;
    String difficulty;
    String requirement;
    String parameters;
    String skillRequirement;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    
    public String getSkillRequirement() {
        return skillRequirement;
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
        return false;
    }

}
