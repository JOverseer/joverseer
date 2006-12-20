package org.joverseer.ui.listviews;

import org.joverseer.domain.PopulationCenter;
import org.springframework.context.MessageSource;


public class PopulationCenterTableModel extends ItemTableModel {
    public PopulationCenterTableModel(MessageSource messageSource) {
        super(PopulationCenter.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "size", "fortification", "loyalty"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Integer.class};
    }
}
