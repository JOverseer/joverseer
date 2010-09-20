package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.springframework.context.MessageSource;

/**
 * Table model for Enemy Character Rumors
 * 
 * @author Marios Skounakis
 */
public class EnemyCharacterRumorTableModel extends ItemTableModel {
    public EnemyCharacterRumorTableModel(MessageSource messageSource) {
        super(EnemyCharacterRumorWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"name", "nationNo", "turnNo", "lastTurnNo", "actionCount", "startChar", "reportedTurns", "inactiveReason"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class};
    }

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (getColumnPropertyNames()[i].equals("startChar")) {
			EnemyCharacterRumorWrapper w = (EnemyCharacterRumorWrapper)object;
			return w.getStartChar() ? "yes" : "";
		}
		return super.getValueAtInternal(object, i);
	}

    
}
