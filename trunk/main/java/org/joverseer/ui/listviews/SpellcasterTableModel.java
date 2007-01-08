package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.SpellcasterWrapper;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;


public class SpellcasterTableModel extends ItemTableModel {
    ArrayList<Integer> spells = new ArrayList<Integer>();
    ArrayList<String> spellDescrs = new ArrayList<String>();
    
    public SpellcasterTableModel(MessageSource messageSource) {
        super(SpellcasterWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"character", "hexNo", "nationNo", "artifactBonus", "spell", "spell", "spell", "spell", 
                                "spell", "spell", "spell", "spell"}; 
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, String.class, String.class, Integer.class, 
                                Integer.class, Integer.class, Integer.class, Integer.class,
                                Integer.class, Integer.class, Integer.class, Integer.class};
    }
    
    public String[] createColumnNames() {
        String[] colNames = new String[12];
        colNames[0] = "Character";
        colNames[1] = "Hex";
        colNames[2] = "Nation";
        colNames[3] = "Bonus";
        for (int i=1; i<9; i++) {
            colNames[i+3] = "Spell " + i;
        }
        return colNames;
    }
    
    public String getColumnName(int arg0) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g) || arg0 < 4) return super.getColumnName(arg0);
        if (arg0-4 < spells.size()) {
            return spellDescrs.get(arg0-4);
        }
        return "";
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i < 4) return super.getValueAtInternal(object, i);
        if (i-4 < spells.size()) {
            SpellcasterWrapper sw = (SpellcasterWrapper)object;
            return sw.getProficiency(spells.get(i-4));
        }
        return "";
    }
    
    public ArrayList<Integer> getSpells() {
        return spells;
    }

    public ArrayList<String> getSpellDescrs() {
        return spellDescrs;
    }
}
