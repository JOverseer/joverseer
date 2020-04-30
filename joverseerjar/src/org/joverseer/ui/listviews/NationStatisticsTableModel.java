package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for NationStatisticsWrapper objects
 *
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NationStatisticsTableModel extends ItemTableModel {

	public NationStatisticsTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(NationStatisticsWrapper.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "allegiance", "nationNo", "characters","charactersLimit", "charactersInCapital", "hostages", "commanders", "NPCsRecruited","NPCRecruitLimit","popCenters", "cities", "majorTowns", "towns", "villages", "camps", "taxBase", "armies", "navies", "warships", "transports", "armyEHI", "troopCount" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, Integer.class,Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
