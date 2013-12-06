package org.joverseer.validation;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.rules.Rules;
import org.springframework.rules.constraint.property.AbstractPropertyConstraint;
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
        rules.add("hexNo", gte(0));
        rules.add("command", gte(0));
        rules.add("commandTotal", gte(0));
        rules.add("emmisary", gte(0));
        rules.add("emmisaryTotal", gte(0));
        rules.add("mage", gte(0));
        rules.add("mageTotal", gte(0));
        rules.add("agent", gte(0));
        rules.add("agentTotal", gte(0));
        rules.add("stealth", gte(0));
        rules.add("stealthTotal", gte(0));
        rules.add("challenge", gte(0));
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
    	rules.add(new CapitalConstraint());
    	return rules;
    }

    
    public class CapitalConstraint extends AbstractPropertyConstraint {

		@Override
		public boolean isDependentOn(String propertyName) {
			return "capital".equals(propertyName) || "size".equals(propertyName);
		}

		@Override
		protected boolean test(PropertyAccessStrategy arg0) {
			boolean capital = (Boolean)arg0.getPropertyValue("capital");
			PopulationCenterSizeEnum size = (PopulationCenterSizeEnum)arg0.getPropertyValue("size");
			if (!capital) return true;
			if (capital && size.equals(PopulationCenterSizeEnum.city)) return true;
			if (capital && size.equals(PopulationCenterSizeEnum.majorTown)) return true;
			return false;
		}

		@Override
		public String toString() {
			return "can be true only if size is City or Major Town";
		}

		@Override
		public String getPropertyName() {
			return "capital";
		}
		
		
    	
    }
    
    
    
    private Rules createArmyRules() {
    	Rules rules = new Rules(Army.class);
    	rules.add("commanderName", required());
    	rules.add("nation", required());
    	rules.add("hexNo", gte(0));
    	return rules;
    }
}

