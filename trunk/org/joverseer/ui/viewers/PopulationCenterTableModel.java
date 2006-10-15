package org.joverseer.ui.viewers;

import org.springframework.richclient.table.BeanTableModel;
import org.springframework.context.MessageSource;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 5:11:04 μμ
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenterTableModel extends BeanTableModel {
    public PopulationCenterTableModel(MessageSource messageSource) {
        super(Character.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "size", "fortification", "loyalty"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Integer.class};
    }

    protected boolean isCellEditableInternal(Object object, int i) {
        return false;
    }
}
