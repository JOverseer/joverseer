package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.ProductLineWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for Nation Production objects
 * 
 * @author Marios Skounakis
 */
public class NationProductionTableModel extends ItemTableModel {
	public NationProductionTableModel(MessageSource messageSource) {
		super(ProductLineWrapper.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "idx", "nationNo", "descr", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}
}
