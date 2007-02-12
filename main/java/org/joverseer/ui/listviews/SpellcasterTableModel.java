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
        return new String[] {"character", "hexNo", "nationNo", "mageRank", "artifactBonus", "spell", "spell", "spell", "spell", 
                                "spell", "spell", "spell", "spell"}; 
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, String.class, String.class, Integer.class, Integer.class, 
                                Integer.class, Integer.class, Integer.class, Integer.class,
                                Integer.class, Integer.class, Integer.class, Integer.class};
    }
    
    public String[] createColumnNames() {
        String[] colNames = new String[13];
        colNames[0] = "Character";
        colNames[1] = "Hex";
        colNames[2] = "Nation";
        colNames[3] = "Mage Rank";
        colNames[4] = "Bonus";
        for (int i=0; i<8; i++) {
            colNames[i + getSpellStartI()] = "Spell " + (i + 1);
        }
        return colNames;
    }
    
    public String getColumnName(int arg0) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g) || arg0 < getSpellStartI()) return super.getColumnName(arg0);
        if (arg0 - getSpellStartI() < spells.size()) {
            return spellDescrs.get(arg0 - getSpellStartI());
        }
        return "";
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i <  getSpellStartI()) return super.getValueAtInternal(object, i);
        if (i - getSpellStartI() < spells.size()) {
            SpellcasterWrapper sw = (SpellcasterWrapper)object;
            return sw.getProficiency(spells.get(i - getSpellStartI()));
        }
        return "";
    }
    
    public ArrayList<Integer> getSpells() {
        return spells;
    }

    public ArrayList<String> getSpellDescrs() {
        return spellDescrs;
    }

    int getSpellStartI() {
        return 5;
    }
}
