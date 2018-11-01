package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.springframework.context.MessageSource;

/**
 * Table model for ArmySizeEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmySizeEstimatesTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;

	public ArmySizeEstimatesTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(ArmySizeEstimate.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "type", "size", "min", "max", "countKnown", "countUnknown" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
