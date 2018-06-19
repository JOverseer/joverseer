package org.joverseer.ui.listviews;

import org.joverseer.joApplication;
import org.joverseer.domain.Note;
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

	public NotesTableModel(MessageSource messageSource) {
		super(Note.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "targetDescription", "nationNo", "tags", "text", "persistent" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Boolean.class };
	}

	// protected boolean isCellEditableInternal(Object object, int i) {
	// return i == iText || i == iTags;
	// }

	@Override
	protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
		super.setValueAtInternal(arg0, arg1, arg2);
		joApplication.publishEvent(LifecycleEventsEnum.NoteUpdated, this, this);

	}

}
