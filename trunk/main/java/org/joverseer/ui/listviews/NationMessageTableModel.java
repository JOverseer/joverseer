package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;
import org.joverseer.domain.NationMessage;


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
