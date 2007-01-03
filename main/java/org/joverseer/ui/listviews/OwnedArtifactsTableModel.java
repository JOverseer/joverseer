package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Order;
import org.springframework.context.MessageSource;


public class OwnedArtifactsTableModel extends ItemTableModel {
    public OwnedArtifactsTableModel(MessageSource messageSource) {
        super(Artifact.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"number", "name", "owner", "hexNo"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

}
