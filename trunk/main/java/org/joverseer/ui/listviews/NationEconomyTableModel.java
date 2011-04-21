package org.joverseer.ui.listviews;

import org.joverseer.domain.NationEconomy;
import org.springframework.context.MessageSource;

/**
 * Table model for NationEconomy objects
 * 
 * @author Marios Skounakis
 */
public class NationEconomyTableModel extends ItemTableModel {
	public NationEconomyTableModel(MessageSource messageSource) {
		super(NationEconomy.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "armyMaintenance", "popMaintenance", "charMaintenance", "totalMaintenance", "goldProduction", "revenue", "surplus", "reserve", "taxRate" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
