package org.joverseer.domain;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration of character death reasons.
 * 
 * If a character is not dead, it must have the "NotDead" enumeration value.
 * 
 * The "Dead" value represents a variety of death causes (combat, overrun, encounter, etc)
 * 
 * @author Marios Skounakis
 *
 */
public enum CharacterDeathReasonEnum {
    NotDead,
    Assassinated,
    Cursed,
    Executed,
    Dead,
    Challenged,
    Missing;

    public String getRenderString() {
 	   return UIUtils.enumToString(this);
    }
}
