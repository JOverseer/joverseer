package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.NationEconomyPotential;
import org.springframework.context.MessageSource;

/**
 * Table model for NationEconomyPotential objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NationEconomyPotentialTableModel extends ItemTableModel {

	public NationEconomyPotentialTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(NationEconomyPotential.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "surplus", "reserve", "total", "oneNatSell", "oneNatSellProduct", "twoNatSells", "twoNatSellProduct", "charsInCapital" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	}

	public NationEconomyPotential getNewPotential() {
		return new NationEconomyPotential();
	}

}
