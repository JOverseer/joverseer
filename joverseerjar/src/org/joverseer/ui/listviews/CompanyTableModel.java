package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.CompanyWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for companies
 * 
 * @author Marios Skounakis
 */
public class CompanyTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;
	public static final int iHexNo=0;

	public CompanyTableModel(MessageSource messageSource) {
		super(CompanyWrapper.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "nationNo", "commander", "memberStr" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, String.class };
	}

}
