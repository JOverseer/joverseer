package org.joverseer.ui.listviews;

import org.joverseer.domain.ArmySizeEstimate;
import org.springframework.context.MessageSource;


public class ArmySizeEstimatesTableModel extends ItemTableModel {
    public ArmySizeEstimatesTableModel(MessageSource messageSource) {
        super(ArmySizeEstimate.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"type", "size", "min", "max", "countKnown", "countUnknown"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class};
    }

}
