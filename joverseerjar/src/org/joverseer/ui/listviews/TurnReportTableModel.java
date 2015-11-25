package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.TurnReportItem;
import org.springframework.context.MessageSource;

public class TurnReportTableModel extends ItemTableModel {

	public TurnReportTableModel(MessageSource messageSource) {
		super(TurnReportItem.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "hexNo", "description" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, Integer.class, String.class };
	}
}
