package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;
import org.joverseer.metadata.domain.ArtifactInfo;

/**
 * Table model for ArtifactInfo objects
 *  
 * @author Marios Skounakis
 */
public class ArtifactInfoTableModel extends ItemTableModel {
    public ArtifactInfoTableModel(MessageSource messageSource) {
        super(ArtifactInfo.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"no", "name", "alignment", "power1", "power2", "owner"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, String.class};
    }
}
