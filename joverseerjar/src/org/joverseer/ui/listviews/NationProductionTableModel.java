package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.ProductLineWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for Nation Production objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NationProductionTableModel extends ItemTableModel {
	public NationProductionTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(ProductLineWrapper.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "idx", "nationNo", "descr", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}
}
