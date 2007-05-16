package org.joverseer.ui.listviews;

import java.util.Date;

import org.joverseer.domain.PlayerInfo;
import org.springframework.context.MessageSource;


public class PlayerInfoTableModel extends ItemTableModel {

    public PlayerInfoTableModel(MessageSource messageSource) {
        super(PlayerInfo.class, messageSource);
    }

    
    protected String[] createColumnPropertyNames() {
        return new String[]{"nationNo", "playerName", "accountNo", 
                            "turnVersion", "lastOrderFile", "ordersSentOn"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, 
                            Integer.class, String.class, Date.class};
    }
}
