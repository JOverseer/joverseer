package org.joverseer.orders.me.orderProcessors.spells;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Turn;
import org.joverseer.orders.OrderUtils;
import org.joverseer.orders.checks.CharacterExistsCheck;
import org.joverseer.orders.checks.CharacterHasSpellCheck;
import org.joverseer.orders.checks.CharacterInSameHexCheck;
import org.joverseer.orders.checks.RequiredParameterNumberCheck;
import org.joverseer.orders.checks.RequiresSkillCheck;
import org.joverseer.orders.checks.ValidSpellCheck;


public class HealingSpellOrderProcessor extends AbstractSpellOrderProcessor {
    public HealingSpellOrderProcessor() {
        super();
        addCheck(new RequiredParameterNumberCheck(2));
        addCheck(new RequiresSkillCheck(RequiresSkillCheck.MAGE_SKILL));
        addCheck(new ValidSpellCheck(0, "2, 4, 6, 8"));
        addCheck(new CharacterExistsCheck(1));
        addCheck(new CharacterHasSpellCheck(0, 1));
        addCheck(new CharacterInSameHexCheck(1));
    }
    
    @Override
	public boolean appliesTo(Character c, int orderNo) {
        return getOrder(c, orderNo).getOrderNo() == 120;
    }
    
    

    @Override
	public void processOrderImpl(Turn t, Character c, int orderNo) {
        Order o = getOrder(c, orderNo);
        String targetId = getSpellTarget(o);
        int spellNo = getSpellNo(o);
        Character targetCharacter = OrderUtils.getCharacterFromId(targetId);
        int healAmt = 0;
        switch (spellNo) {
            case 2:
                healAmt = 20;
                break;
            case 4:
                healAmt = 35;
                break;
            case 6:
                healAmt = 50;
                break;
            case 8:
                healAmt = 100;
                break;
        }
        int trueHealAmt = Math.min(100 - targetCharacter.getHealth(), healAmt);
        targetCharacter.setHealth(targetCharacter.getHealth() + trueHealAmt);
        OrderUtils.appendOrderResult(c, String.format("{0} healed {1} for {2} health points.", c.getName(), targetCharacter.getName(), trueHealAmt));
    }

}
