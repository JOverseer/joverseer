package org.joverseer.validation;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;


public class JOverseerRulesSource extends DefaultRulesSource {

    public JOverseerRulesSource() {
        addRules(createCharacterRules());
        addRules(createPopCenterRules());
        addRules(createArmyRules());
    }

    private Rules createCharacterRules() {
        Rules rules = new Rules(Character.class);
        rules.add("name", required());
        rules.add("nation", required());
        return rules;
    }
    
    private Rules createPopCenterRules() {
    	Rules rules = new Rules(PopulationCenter.class);
    	rules.add("name", required());
    	rules.add("size", required());
    	rules.add("fortification", required());
    	rules.add("harbor", required());
    	rules.add("loyalty", gte(0));
    	rules.add("hexNo", gte(0));
    	return rules;
    }

    private Rules createArmyRules() {
    	Rules rules = new Rules(Army.class);
    	rules.add("commanderName", required());
    	rules.add("nation", required());
    	rules.add("hexNo", gte(0));
    	return rules;
    }
}
