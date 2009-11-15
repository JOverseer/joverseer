package org.joverseer.ui.listviews;

import org.joverseer.support.infoSources.InfoSource;
import org.springframework.context.MessageSource;

/**
 * Table model for Character objects
 * @author Marios Skounakis
 */
public class CharacterTableModel extends ItemTableModel {
    public CharacterTableModel(MessageSource messageSource) {
        super(Character.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "command", "commandTotal", "agent", "agentTotal", "emmisary", "emmisaryTotal", "mage", "mageTotal", "stealth", "stealthTotal", "challenge", "health", "infoSource", "orderResults", "maintenance"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class , Integer.class, InfoSource.class, String.class, Integer.class};
    }
}
