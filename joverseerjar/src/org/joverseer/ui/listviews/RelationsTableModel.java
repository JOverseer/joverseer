package org.joverseer.ui.listviews;

import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * Table model for NationRelation objects
 * 
 * @author Marios Skounakis
 */
public class RelationsTableModel extends ItemTableModel {
	public RelationsTableModel(MessageSource messageSource) {
		super(NationRelations.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "nationNo", "allegiance", "eliminated", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo", "nationNo" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	}

	@Override
	public String[] createColumnNames() {
		String[] colNames = new String[28];
		colNames[0] = "Nation";
		colNames[1] = "Allegiance";
		colNames[2] = "Eliminated";
		for (int i = 1; i < 26; i++) {
			colNames[i + 2] = "N" + i;
		}
		return colNames;
	}

	@Override
	public String getColumnName(int arg0) {
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g == null || !Game.isInitialized(g) || arg0 < 3)
			return super.getColumnName(arg0);
		return g.getMetadata().getNationByNum(arg0 - 2).getShortName();
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (i < 2)
			return super.getValueAtInternal(object, i);
		if (i == 2) {
			return ((NationRelations) object).getEliminated() ? "x" : "";
		}
		NationRelations nr = (NationRelations) object;
		if (nr == null)
			return "";
		if (i - 2 == nr.getNationNo())
			return "";
		if (nr.getRelationsFor(i - 2) == null) {
			return "";
		}
		switch (nr.getRelationsFor(i - 2)) {
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

	@Override
	protected void setValueAtInternal(Object value, Object object, int i) {
		if (i < 2)
			return;
		if (i - 2 == ((NationRelations) object).getNationNo())
			return;
		NationRelations nr = (NationRelations) object;
		if (value.toString().equals("F")) {
			nr.setRelationsFor(i - 2, NationRelationsEnum.Friendly);
		} else if (value.toString().equals("T")) {
			nr.setRelationsFor(i - 2, NationRelationsEnum.Tolerated);
		} else if (value.toString().equals("N")) {
			nr.setRelationsFor(i - 2, NationRelationsEnum.Neutral);
		} else if (value.toString().equals("D")) {
			nr.setRelationsFor(i - 2, NationRelationsEnum.Disliked);
		} else if (value.toString().equals("H")) {
			nr.setRelationsFor(i - 2, NationRelationsEnum.Hated);
		}
	}

	@Override
	protected boolean isCellEditableInternal(Object object, int i) {
		return i >= 3 && !getValueAtInternal(object, i).equals("");
	}

}
