package org.joverseer.ui.listviews;

import org.joverseer.domain.NationMessage;
import org.springframework.context.MessageSource;

/**
 * Table model for NationMessage objects
 * 
 * @author Marios Skounakis
 */
public class NationMessageTableModel extends ItemTableModel {
	public NationMessageTableModel(MessageSource messageSource) {
		super(NationMessage.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "message" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class };
	}
}
