package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;
import org.joverseer.domain.NationMessage;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Οκτ 2006
 * Time: 7:28:08 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationMessageTableModel extends ItemTableModel {
    public NationMessageTableModel(MessageSource messageSource) {
        super(NationMessage.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"nationNo", "message"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class};
    }
}
