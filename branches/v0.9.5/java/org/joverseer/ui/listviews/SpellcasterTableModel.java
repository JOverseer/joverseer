package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.Character;
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
                                "spell", "spell", "spell", "spell", "orders"}; 
    }

    protected Class[] createColumnClasses() {
        return new Class[] { String.class, String.class, String.class, Integer.class, Integer.class, 
                                Integer.class, Integer.class, Integer.class, Integer.class,
                                Integer.class, Integer.class, Integer.class, Integer.class, String.class};
    }
    
    public String[] createColumnNames() {
        String[] colNames = new String[14];
        colNames[0] = "Character";
        colNames[1] = "Hex";
        colNames[2] = "Nation";
        colNames[3] = "Mage Rank";
        colNames[4] = "Bonus";
        for (int i=0; i<8; i++) {
            colNames[i + getSpellStartI()] = "Spell " + (i + 1);
        }
        colNames[13] = "Orders";
        return colNames;
    }
    
    public String getColumnName(int arg0) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g) || arg0 < getSpellStartI()) {
        	return super.getColumnName(arg0);
        }
        if (arg0 - getSpellStartI() < spells.size()) {
            return spellDescrs.get(arg0 - getSpellStartI());
        }
        if (arg0 == 13) {
        	return super.getColumnName(arg0);
        }
        return "";
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i <  getSpellStartI()) return super.getValueAtInternal(object, i);
        if (i - getSpellStartI() < spells.size()) {
            SpellcasterWrapper sw = (SpellcasterWrapper)object;
            return sw.getProficiency(spells.get(i - getSpellStartI()));
        }
        if (i == 13) {
        	// return Orders
        	SpellcasterWrapper sw = (SpellcasterWrapper)object;
        	Game g = GameHolder.instance().getGame();
        	Turn t = g.getTurn();
        	Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", sw.getCharacter());
        	if (c == null) return "";
        	String orders = "";
        	for (int j=0; j<2; j++) {
        		if (!c.getOrders()[j].isBlank()) {
        			orders += (orders.equals("") ? "" : ", ") + c.getOrders()[j].getNoAndCode() + " " + c.getOrders()[j].getParameters();
        		}
        	}
        	return orders;
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
