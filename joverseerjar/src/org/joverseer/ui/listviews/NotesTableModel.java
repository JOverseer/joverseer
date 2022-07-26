package org.joverseer.ui.listviews;

import org.joverseer.JOApplication;
import org.joverseer.domain.Note;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.springframework.context.MessageSource;

/**
 * Table model for Note objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class NotesTableModel extends ItemTableModel {
	public static final int iHexNo =0;
	public static final int iTarget = 1;
	public static final int iText = 4;
	public static final int iTags = 3;

	public NotesTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(Note.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "targetDescription", "nationNo", "tags", "text", "persistent" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Boolean.class };
	}

	// protected boolean isCellEditableInternal(Object object, int i) {
	// return i == iText || i == iTags;
	// }

	@Override
	protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
		super.setValueAtInternal(arg0, arg1, arg2);
		JOApplication.publishEvent(LifecycleEventsEnum.NoteUpdated, this, this);

	}

}
