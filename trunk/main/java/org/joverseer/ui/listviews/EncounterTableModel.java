package org.joverseer.ui.listviews;

import org.joverseer.domain.Encounter;
import org.springframework.context.MessageSource;


public class EncounterTableModel extends ItemTableModel {
    public EncounterTableModel(MessageSource messageSource) {
        super(Encounter.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "character", "description"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class};
    }

}
