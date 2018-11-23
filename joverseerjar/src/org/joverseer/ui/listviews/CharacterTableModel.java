package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.springframework.context.MessageSource;

/**
 * Table model for Character objects
 * 
 * @author Marios Skounakis
 */
public class CharacterTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;
	final static int iHex = 0;
	public CharacterTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(Character.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "name", "nationNo", "command", "commandTotal", "agent", "agentTotal", "emmisary", "emmisaryTotal", "mage", "mageTotal", "stealth", "stealthTotal", "challenge", "health", "infoSource", "orderResults", "maintenance" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, InfoSource.class, String.class, Integer.class };
	}
}
