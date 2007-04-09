package org.joverseer.ui.listviews;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.SpellInfo;
import org.springframework.context.MessageSource;


public class SpellInfoTableModel extends ItemTableModel {
    public SpellInfoTableModel(MessageSource messageSource) {
        super(SpellInfo.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"list", "number", "name", "difficulty", "orderNumber", "requiredInfo", "requirements", "description"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
    }

}
