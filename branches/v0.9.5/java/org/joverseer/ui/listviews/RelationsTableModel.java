package org.joverseer.ui.listviews;

import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;


public class RelationsTableModel extends ItemTableModel {
    public RelationsTableModel(MessageSource messageSource) {
        super(NationRelations.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"nationNo", "allegiance", 
                             "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", 
                             "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", 
                             "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", 
                             "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", 
                             "nationNo", "nationNo", "nationNo", "nationNo", "nationNo"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, String.class, 
                                String.class, String.class, String.class, String.class, String.class, 
                                String.class, String.class, String.class, String.class, String.class, 
                                String.class, String.class, String.class, String.class, String.class, 
                                String.class, String.class, String.class, String.class, String.class, 
                                String.class, String.class, String.class, String.class, String.class};
    }
    
    public String[] createColumnNames() {
        String[] colNames = new String[27];
        colNames[0] = "Nation";
        colNames[1] = "Allegiance";
        for (int i=1; i<26; i++) {
            colNames[i+1] = "N" + i;
        }
        return colNames;
    }
    
    public String getColumnName(int arg0) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g) || arg0 < 2) return super.getColumnName(arg0);
        return g.getMetadata().getNationByNum(arg0 - 1).getShortName();
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i < 2) return super.getValueAtInternal(object, i);
        if (i-1 == ((NationRelations)object).getNationNo()) return "";
        switch (((NationRelations)object).getRelationsFor(i-1)) {
            case Friendly:
                return "F";
            case Tolerated:
                return "T";
            case Neutral:
                return "N";
            case Disliked:
                return "D";
            case Hated:
                return "H";
        }
        return "";
    }
    
}
