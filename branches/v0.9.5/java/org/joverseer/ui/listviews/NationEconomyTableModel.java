package org.joverseer.ui.listviews;

import org.joverseer.domain.NationEconomy;
import org.springframework.context.MessageSource;


public class NationEconomyTableModel extends ItemTableModel {
    public NationEconomyTableModel(MessageSource messageSource) {
        super(NationEconomy.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"nationNo", "totalMaintenance", "revenue", "surplus", "reserve", "taxRate"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
    }

}
