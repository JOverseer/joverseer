package org.joverseer.orders.me.orderProcessors.spells;

import java.util.Arrays;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellDifficultyEnum;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.orders.AbstractOrderProcessor;
import org.joverseer.orders.OrderUtils;


public abstract class AbstractSpellOrderProcessor extends AbstractOrderProcessor {
    
    
    public AbstractSpellOrderProcessor() {
        super();
    }

    public int getSpellNo(Order o) {
        return Integer.parseInt(o.getParameter(0));
    }

    public String getSpellTarget(Order o) {
        return o.getParameter(1);
    }
    
    public boolean spellCastOutcome(Character c, int spellNo) {
        GameMetadata gm = OrderUtils.getGame().getMetadata();
        SpellInfo si = (SpellInfo)gm.getSpells().findFirstByProperty("number", spellNo);
        int difficultyFactor = 0;
        if (si.getDifficultyLevel() == SpellDifficultyEnum.Easy) {
            difficultyFactor += 60;
        } 
        if (si.getDifficultyLevel() == SpellDifficultyEnum.Average) {
            difficultyFactor += 30;
        } 
        if (si.getDifficultyLevel() == SpellDifficultyEnum.Hard) {
            difficultyFactor += 0;
        } 
        int mageProficiency = 0;
        for (SpellProficiency sp : c.getSpells()) {
            if (sp.getSpellId() == spellNo) {
                mageProficiency = sp.getProficiency();
            }
        }
        int artifactBonus = 0;
        // TODO continue here
        return true;
        
    }
}
