package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;

public class HexProductionTableModel extends ItemTableModel {
	private static final long serialVersionUID = -7154147184547454802L;

	public HexProductionTableModel(MessageSource messageSource) {
		super(HexProductionWrapper.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "terrain", "climate", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts", "gold" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
