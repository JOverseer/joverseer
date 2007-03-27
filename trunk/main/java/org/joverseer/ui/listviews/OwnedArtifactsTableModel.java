package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Order;
import org.joverseer.ui.domain.OwnedArtifact;
import org.springframework.context.MessageSource;


public class OwnedArtifactsTableModel extends ItemTableModel {
    public OwnedArtifactsTableModel(MessageSource messageSource) {
        super(OwnedArtifact.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"number", "name", "nationNo", "owner", "hexNo"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

}
