package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.springframework.context.MessageSource;

@Deprecated
public class ArtifactTableModel extends ItemTableModel {
    public ArtifactTableModel(MessageSource messageSource) {
        super(Artifact.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "number", "name", "owner", "infoSourceDescr"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class};
    }

}
