package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.TurnReportItem;
import org.springframework.context.MessageSource;

public class TurnReportTableModel extends ItemTableModel {

	public TurnReportTableModel(MessageSource messageSource) {
		super(TurnReportItem.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"nationNo", "hexNo", "description"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, Integer.class, String.class};
	}
}
