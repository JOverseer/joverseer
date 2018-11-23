package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.ChangedPCInfo;
import org.springframework.context.MessageSource;

public class ChangedPCTableModel extends ItemTableModel {

	private static final long serialVersionUID = 1L;

	public ChangedPCTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(ChangedPCInfo.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "nationNo", "name", "size", "reason" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class };
	}

}