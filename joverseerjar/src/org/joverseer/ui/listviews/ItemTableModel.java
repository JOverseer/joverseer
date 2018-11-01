package org.joverseer.ui.listviews;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Base class for Item table models.
 * 
 * Basically provides a central control for how nation columns are displayed
 * (number or short name)
 * 
 * @author Marios Skounakis
 */
public abstract class ItemTableModel extends BeanTableModel {

	private static final long serialVersionUID = 1L;
	// dependencies
	protected GameHolder gameHolder;
	protected PreferenceRegistry preferenceRegistry;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}

	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	public PreferenceRegistry getPreferenceRegistry() {
		return this.preferenceRegistry;
	}

	public void setPreferenceRegistry(PreferenceRegistry preferenceRegistry) {
		this.preferenceRegistry = preferenceRegistry;
	}

	public ItemTableModel(Class aClass, MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(aClass, messageSource);
		setRowNumbers(false);
		this.gameHolder = gameHolder;
		this.preferenceRegistry = preferenceRegistry;
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		try {
			String pval = this.preferenceRegistry.getPreferenceValue("listviews.showNationAs");
			if (pval.equals("number"))
				return super.getValueAtInternal(object, i);
			Game game = this.gameHolder.getGame();
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
