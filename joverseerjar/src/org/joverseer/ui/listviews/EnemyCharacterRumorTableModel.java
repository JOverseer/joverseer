package org.joverseer.ui.listviews;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for Enemy Character Rumors
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class EnemyCharacterRumorTableModel extends ItemTableModel {
	public EnemyCharacterRumorTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(EnemyCharacterRumorWrapper.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "name", "nationNo", "turnNo", "lastTurnNo", "actionCount", "startChar", "reportedTurns", "inactiveReason" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class };
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (getColumnPropertyNames()[i].equals("startChar")) {
			EnemyCharacterRumorWrapper w = (EnemyCharacterRumorWrapper) object;
			return w.getStartChar() ? "yes" : "";
		}
		return super.getValueAtInternal(object, i);
	}

}
