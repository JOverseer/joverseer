package org.joverseer.validation;

import org.joverseer.domain.Character;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;


public class JOverseerRulesSource extends DefaultRulesSource {

    public JOverseerRulesSource() {
        addRules(createCharacterRules());
    }

    private Rules createCharacterRules() {
        Rules rules = new Rules(Character.class);
        rules.add("name", required());
        rules.add("nation", required());
        return rules;
    }

}
