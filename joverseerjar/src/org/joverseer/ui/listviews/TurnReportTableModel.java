package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.TurnReportItem;
import org.springframework.context.MessageSource;

@SuppressWarnings("serial")
public class TurnReportTableModel extends ItemTableModel {

	public TurnReportTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(TurnReportItem.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "hexNo", "description" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, Integer.class, String.class };
	}
}
