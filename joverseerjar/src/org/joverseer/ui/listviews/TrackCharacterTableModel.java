package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.TrackCharacterInfo;
import org.springframework.context.MessageSource;

/**
 * Table model for TrackCharacterInfo objects
 * 
 * @author Marios Skounakis
 */
public class TrackCharacterTableModel extends ItemTableModel {

	public TrackCharacterTableModel(MessageSource messageSource) {
		super(TrackCharacterInfo.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "turnNo", "hexNo", "info" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, Integer.class, String.class };
	}

}
