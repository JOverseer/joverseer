package org.joverseer.orders.checks;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.orders.OrderUtils;


public class CharacterExistsCheck extends AbstractCheck  {
    @Override
	public boolean check(Order o) {
        Character c = (Character)OrderUtils.getCharacterFromId(o.getParameter(getParamNo()));
        return c != null;
    }

    @Override
	public String getMessage() {
        return "Character {0} was not found.";
    }

    public CharacterExistsCheck() {
        super();
    }

    public CharacterExistsCheck(int paramNo) {
        super(paramNo);
    }
    
    
}
