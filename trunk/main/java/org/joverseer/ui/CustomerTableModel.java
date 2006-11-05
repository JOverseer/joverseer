package org.joverseer.ui;

import org.springframework.richclient.table.BeanTableModel;
import org.springframework.context.MessageSource;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 2:32:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTableModel extends BeanTableModel {
    public CustomerTableModel(MessageSource messageSource) {
        super(Customer.class, messageSource);
        setRowNumbers(false);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] { "firstName", "lastName" , "name"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, String.class , String.class};
    }

    protected boolean isCellEditableInternal(Object object, int i) {
        return false;
    }

}
