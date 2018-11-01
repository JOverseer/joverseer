package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;

public class HexProductionTableModel extends ItemTableModel {
	private static final long serialVersionUID = -7154147184547454802L;
	public static final int iHexNo =0;
	public HexProductionTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(HexProductionWrapper.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "terrain", "climate", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts", "gold" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
