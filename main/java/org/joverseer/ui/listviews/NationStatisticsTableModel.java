package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.springframework.context.MessageSource;

public class NationStatisticsTableModel extends ItemTableModel {

	public NationStatisticsTableModel(MessageSource messageSource) {
		super(NationStatisticsWrapper.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"nationNo", 
				"characters", "charactersInCapital", "commanders", 
				"popCenters", "cities", "majorTowns", "towns", "villages", "camps", "taxBase", 
				"armies", "navies", "warships", "transports", "armyEHI", "troopCount"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, 
				Integer.class, Integer.class, Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
	}
	

}
