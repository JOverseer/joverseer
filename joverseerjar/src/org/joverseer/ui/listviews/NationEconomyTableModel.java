package org.joverseer.ui.listviews;

import org.joverseer.domain.NationEconomy;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;

/**
 * Table model for NationEconomy objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NationEconomyTableModel extends ItemTableModel {
	public NationEconomyTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(NationEconomy.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "armyMaintenance", "popMaintenance", "charMaintenance", "totalMaintenance", "goldProduction", "revenue", "surplus", "reserve", "taxRate" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
