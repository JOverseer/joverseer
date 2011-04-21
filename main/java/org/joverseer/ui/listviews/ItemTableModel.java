package org.joverseer.ui.listviews;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Base class for Item table models.
 * 
 * Basically provides a central control for how nation columns are displayed
 * (number of short name)
 * 
 * @author Marios Skounakis
 */
public abstract class ItemTableModel extends BeanTableModel {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ItemTableModel(Class aClass, MessageSource messageSource) {
		super(aClass, messageSource);
		setRowNumbers(false);
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		try {
			String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs");
			if (pval.equals("number"))
				return super.getValueAtInternal(object, i);
			Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			if (game == null)
				return "";
			if (IBelongsToNation.class.isInstance(object) && getColumnPropertyNames()[i].equals("nationNo")) {
				GameMetadata gm = game.getMetadata();
				Integer nationNo = ((IBelongsToNation) object).getNationNo();
				if (nationNo == null)
					return "";
				return gm.getNationByNum(nationNo).getShortName();
			}
			return super.getValueAtInternal(object, i);
		} catch (Exception exc) {
			return "";
		}

	}

	@Override
	protected boolean isCellEditableInternal(Object object, int i) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
	}

}
