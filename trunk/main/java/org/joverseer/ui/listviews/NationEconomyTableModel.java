package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 5:43:43 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationEconomyTableModel extends ItemTableModel {
    public NationEconomyTableModel(MessageSource messageSource) {
        super(Character.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"nationNo", "totalMaintenance", "revenue", "surplus", "reserve", "taxRate"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
    }

}
