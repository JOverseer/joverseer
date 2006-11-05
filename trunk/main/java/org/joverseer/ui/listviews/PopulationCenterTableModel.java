package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 5:11:04 μμ
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenterTableModel extends ItemTableModel {
    public PopulationCenterTableModel(MessageSource messageSource) {
        super(Character.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "size", "fortification", "loyalty"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Integer.class};
    }
}
