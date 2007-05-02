
package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.TrackCharacterInfo;
import org.springframework.context.MessageSource;

public class TrackCharacterTableModel extends ItemTableModel {

    public TrackCharacterTableModel(MessageSource messageSource) {
        super(TrackCharacterInfo.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"turnNo", "hexNo", "info"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{Integer.class, Integer.class, String.class};
    }
    

}
