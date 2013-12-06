package org.joverseer.orders.checks;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.joverseer.orders.OrderUtils;


public class CharacterInSameHexCheck extends AbstractCheck {
    @Override
	public boolean check(Order o) {
        Character c = (Character)OrderUtils.getCharacterFromId(o.getParameter(getParamNo()));
        return (c.getHexNo() == o.getCharacter().getHexNo());
    }

    @Override
	public String getMessage() {
        return "Character {0} was not in the same hex.";
    }

    public CharacterInSameHexCheck() {
        super();
    }

    public CharacterInSameHexCheck(int paramNo) {
        super(paramNo);
    }

    
}
