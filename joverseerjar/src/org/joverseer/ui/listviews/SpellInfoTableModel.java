package org.joverseer.ui.listviews;

import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;

/**
 * Table model for Spell Info objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class SpellInfoTableModel extends ItemTableModel {
	public SpellInfoTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(SpellInfo.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "list", "number", "name", "difficulty", "orderNumber", "requiredInfo", "requirements", "description" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	}

}
