package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;
import org.joverseer.metadata.domain.Artifact;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Οκτ 2006
 * Time: 7:33:29 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArtifactTableModel extends ItemTableModel {
    public ArtifactTableModel(MessageSource messageSource) {
        super(Artifact.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"no", "name", "power1", "power2", "owner"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class};
    }
}
