package org.joverseer.orders.checks;

import org.joverseer.domain.Order;


public class ValidSpellCheck extends AbstractCheck {
    String validSpells;

    
    public String getValidSpells() {
        return validSpells;
    }

    
    public void setValidSpells(String validSpells) {
        this.validSpells = validSpells;
    }

    public boolean check(Order o) {
        String[] spells = validSpells.split(",");
        for (String sp : spells) {
            if (sp.trim().equals(o.getParameter(getParamNo()))) return true;
        }
        return false;
    }


    public String getMessage() {
        return "Spell {0} was not a valid spell for this order.";
    }


    public ValidSpellCheck() {
        super();
    }


    public ValidSpellCheck(int paramNo, String spells) {
        super(paramNo);
        this.validSpells = spells;
    }

    
}
