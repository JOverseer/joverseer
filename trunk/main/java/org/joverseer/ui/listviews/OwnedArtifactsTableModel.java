package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.OwnedArtifact;
import org.springframework.context.MessageSource;


public class OwnedArtifactsTableModel extends ItemTableModel {
	int iPowers = 5;
	
	
    public OwnedArtifactsTableModel(MessageSource messageSource) {
        super(OwnedArtifact.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"number", "name", "nationNo", "owner", "hexNo", "power1", "power2"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class};  
    }

	    

}
