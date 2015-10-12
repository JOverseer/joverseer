package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.NationEconomyPotential;
import org.springframework.context.MessageSource;

/**
 * Table model for NationEconomyPotential objects
 * 
 * @author Marios Skounakis
 */
public class NationEconomyPotentialTableModel extends ItemTableModel {

	public NationEconomyPotentialTableModel(MessageSource messageSource) {
		super(NationEconomyPotential.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "surplus", "reserve", "total", "oneNatSell", "oneNatSellProduct", "twoNatSells", "twoNatSellProduct", "charsInCapital" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	}

	public NationEconomyPotential getNewPotential() {
		return new NationEconomyPotential();
	}

}
