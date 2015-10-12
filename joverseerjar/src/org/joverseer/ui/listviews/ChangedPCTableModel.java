package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.ChangedPCInfo;
import org.springframework.context.MessageSource;

public class ChangedPCTableModel extends ItemTableModel {

	private static final long serialVersionUID = 1L;

	public ChangedPCTableModel(MessageSource messageSource) {
		super(ChangedPCInfo.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "nationNo", "name", "size", "reason" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class };
	}

}