package org.joverseer.ui.listviews;

import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.springframework.context.MessageSource;

/**
 * Table model for ArmySizeEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmySizeEstimatesTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;

	public ArmySizeEstimatesTableModel(MessageSource messageSource) {
		super(ArmySizeEstimate.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "type", "size", "min", "max", "countKnown", "countUnknown" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
