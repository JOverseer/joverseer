package org.joverseer.ui.listviews;

import org.joverseer.domain.NationEconomy;
import org.joverseer.ui.domain.ProductLineWrapper;
import org.springframework.context.MessageSource;


public class NationProductionTableModel extends ItemTableModel {
    public NationProductionTableModel(MessageSource messageSource) {
        super(ProductLineWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"idx", "nationNo", "descr", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] {Integer.class, String.class, String.class, 
                            Integer.class, Integer.class, Integer.class, Integer.class, 
                            Integer.class, Integer.class, Integer.class};
    }
}
