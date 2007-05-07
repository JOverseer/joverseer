package org.joverseer.ui.listviews;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.PopulationCenter;
import org.springframework.context.MessageSource;


public class NotesTableModel extends ItemTableModel {
    public static int iTarget = 1;
    
    public NotesTableModel( MessageSource messageSource) {
        super(Note.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "target", "nationNo", "text", "persistent"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, Boolean.class};
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i == iTarget) {
            Note n = (Note)object;
            if (n.getTarget() == null) return "";
            if (Integer.class.isInstance(n.getTarget())) {
                return n.getTarget().toString();
            } else if (Character.class.isInstance(n.getTarget())) {
                return ((Character)n.getTarget()).toString();
            } else if (PopulationCenter.class.isInstance(n.getTarget())) {
                return ((PopulationCenter)n.getTarget()).toString();
            } else if (Army.class.isInstance(n.getTarget())) {
                return ((Army)n.getTarget()).toString();
            }
        }
        return super.getValueAtInternal(object, i);
    }
    
    
    

}
