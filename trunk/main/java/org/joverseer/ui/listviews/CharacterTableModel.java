package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 9:27:10 μμ
 * To change this template use File | Settings | File Templates.
 */
public class CharacterTableModel extends ItemTableModel {
    public CharacterTableModel(MessageSource messageSource) {
        super(Character.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "command", "commandTotal", "agent", "agentTotal", "emmisary", "emmisaryTotal", "mage", "mageTotal", "stealth", "stealthTotal", "challenge", "health" };
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class , Integer.class};
    }
}
