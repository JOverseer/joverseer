package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.TrackCharacterInfo;
import org.springframework.context.MessageSource;

/**
 * Table model for TrackCharacterInfo objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class TrackCharacterTableModel extends ItemTableModel {

	public static final int iHexNo=1;
	public TrackCharacterTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(TrackCharacterInfo.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "turnNo", "hexNo", "info" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, Integer.class, String.class };
	}

}
