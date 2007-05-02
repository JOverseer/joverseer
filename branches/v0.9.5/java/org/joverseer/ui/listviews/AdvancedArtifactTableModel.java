package org.joverseer.ui.listviews;

import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.springframework.context.MessageSource;


public class AdvancedArtifactTableModel extends ItemTableModel {

    public AdvancedArtifactTableModel(MessageSource messageSource) {
        super(ArtifactWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"number", "name", "nationNo", "owner", "hexNo", "alignment", "power1", "power2", "turnNo", "infoSource"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{Integer.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, Integer.class, InfoSource.class};
    }

}
