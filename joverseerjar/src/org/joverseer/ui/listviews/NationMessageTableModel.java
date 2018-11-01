package org.joverseer.ui.listviews;

import org.joverseer.domain.NationMessage;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;

/**
 * Table model for NationMessage objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NationMessageTableModel extends ItemTableModel {
	public NationMessageTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(NationMessage.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "message" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class };
	}
}
