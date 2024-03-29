package org.joverseer.orders.checks;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.orders.OrderUtils;


public class CharacterHasSpellCheck extends AbstractCheck  {
    int charParamNo;
    
    @Override
	public boolean check(Order o) {
        Integer spellId = Integer.parseInt(o.getParameter(getParamNo()));
        Character c = (Character)OrderUtils.getCharacterFromId(o.getParameter(getCharParamNo()));
        return (c.findSpellMatching(spellId) != null);
    }

    @Override
	public String getMessage() {
        return "The character was unable to cast spell {0} because it is not a known spell.";
    }

    public CharacterHasSpellCheck() {
        super();
    }

    public CharacterHasSpellCheck(int paramNo, int charParamNo) {
        super(paramNo);
        this.charParamNo = charParamNo;
    }

    
    public int getCharParamNo() {
        return this.charParamNo;
    }

    
    public void setCharParamNo(int charParamNo) {
        this.charParamNo = charParamNo;
    }
    
    
}
