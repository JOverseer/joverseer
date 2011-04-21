package org.joverseer.ui.listviews;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.springframework.context.MessageSource;

/**
 * Table model for ArtifactInfo objects
 * 
 * @author Marios Skounakis
 */
public class ArtifactInfoTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;

	public ArtifactInfoTableModel(MessageSource messageSource) {
		super(ArtifactInfo.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "no", "name", "alignment", "power1", "power2", "owner" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };
	}
}
