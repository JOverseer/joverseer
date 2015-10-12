package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.SpellcasterWrapper;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * Table model for SpellcasterWrapper objects
 * 
 * @author Marios Skounakis
 */
public class SpellcasterTableModel extends ItemTableModel {

	ArrayList<Integer> spells = new ArrayList<Integer>();
	ArrayList<String> spellDescrs = new ArrayList<String>();

	public SpellcasterTableModel(MessageSource messageSource) {
		super(SpellcasterWrapper.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		String[] cols = new String[] { "character", "hexNo", "nationNo", "mageRank", "artifactBonus" };
		List<String> colList = new ArrayList<String>();
		colList.addAll(Arrays.asList(cols));
		for (int i = 0; i < 100; i++) {
			colList.add("spell");
		}
		return colList.toArray(new String[] {});
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		Class[] cols = new Class[] { String.class, String.class, String.class, Integer.class, Integer.class };
		List<Class> colList = new ArrayList<Class>();
		colList.addAll(Arrays.asList(cols));
		for (int i = 0; i < 100; i++) {
			colList.add(Integer.class);
		}
		return colList.toArray(new Class[] {});
	}

	@Override
	public String[] createColumnNames() {
		String[] colNames = new String[105];
		colNames[0] = "Character";
		colNames[1] = "Hex";
		colNames[2] = "Nation";
		colNames[3] = "Mage Rank";
		colNames[4] = "Bonus";
		for (int i = 0; i < 100; i++) {
			colNames[i + getSpellStartI()] = "Spell " + (i + 1);
		}
		// colNames[13] = "Orders";
		return colNames;
	}

	@Override
	public String getColumnName(int arg0) {
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g == null || !Game.isInitialized(g) || arg0 < getSpellStartI()) {
			return super.getColumnName(arg0);
		}
		if (arg0 - getSpellStartI() < this.spells.size()) {
			return this.spellDescrs.get(arg0 - getSpellStartI());
		}
		// if (arg0 == 13) {
		// return super.getColumnName(arg0);
		// }
		return "";
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (i < getSpellStartI())
			return super.getValueAtInternal(object, i);
		if (i - getSpellStartI() < this.spells.size()) {
			int spellId = this.spells.get(i - getSpellStartI());
			SpellcasterWrapper sw = (SpellcasterWrapper) object;
			if (spellId > 1000) {
				// spirit mastery health drop effect
				if (spellId == 1502) {
					return sw.getMageRank() / 3;
				} else {
					return sw.getMageRank() / 2;
				}
			} else {
				return sw.getProficiency(spellId);
			}
		}

		return "";
	}

	public ArrayList<Integer> getSpells() {
		return this.spells;
	}

	public ArrayList<String> getSpellDescrs() {
		return this.spellDescrs;
	}

	int getSpellStartI() {
		return 5;
	}
}
