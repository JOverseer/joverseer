package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for NationStatisticsWrapper objects
 * 
 * @author Marios Skounakis
 */
public class NationStatisticsTableModel extends ItemTableModel {

	public NationStatisticsTableModel(MessageSource messageSource) {
		super(NationStatisticsWrapper.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"allegiance", "nationNo", 
				"characters", "charactersInCapital", "hostages", "commanders", 
				"popCenters", "cities", "majorTowns", "towns", "villages", "camps", "taxBase", 
				"armies", "navies", "warships", "transports", "armyEHI", "troopCount"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, String.class,  
				Integer.class, Integer.class, Integer.class, Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
	}
	

}
