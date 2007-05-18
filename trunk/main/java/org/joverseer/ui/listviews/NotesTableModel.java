package org.joverseer.ui.listviews;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;


public class NotesTableModel extends ItemTableModel {
    public static int iTarget = 1;
    public static int iText = 4;
    public static int iTags = 3;
    
    public NotesTableModel( MessageSource messageSource) {
        super(Note.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "targetDescription", "nationNo", "tags", "text", "persistent"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, Boolean.class};
    }

    
//    protected boolean isCellEditableInternal(Object object, int i) {
//        return i == iText || i == iTags;
//    }

    protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
        super.setValueAtInternal(arg0, arg1, arg2);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.NoteAddedOrUpdated.toString(), this, this));

    }

    
    
}
