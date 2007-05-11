package org.joverseer.ui.listviews;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.PopulationCenter;
import org.springframework.context.MessageSource;


public class NotesTableModel extends ItemTableModel {
    public static int iTarget = 1;
    public static int iText = 3;
    
    public NotesTableModel( MessageSource messageSource) {
        super(Note.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "targetDescription", "nationNo", "text", "persistent"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, Boolean.class};
    }

   
    
}
