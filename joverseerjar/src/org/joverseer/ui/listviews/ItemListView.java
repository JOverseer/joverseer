package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;

/**
 * Base class for Item List Views.
 *
 * It specializes BaseItemListView for the cases that the items to be shown are
 * either TurnElementEnum items that are held within the turn or items that can
 * be accessed using a property of the game metadata
 *
 * @author Marios Skounakis
 */
public abstract class ItemListView extends BaseItemListView {
	TurnElementsEnum turnElementType = null;
	String metadataProperty;

	public ItemListView(TurnElementsEnum turnElementType, Class<?> tableModelClass) {
		super(tableModelClass);
		this.turnElementType = turnElementType;
	}

	public ItemListView(String metadataProperty, Class<?> tableModelClass) {
		super(tableModelClass);
		this.metadataProperty = metadataProperty;
		this.turnElementType = null;
	}

	@Override
	protected void setItems() {
		if (this.turnElementType != null) {
			Game g = this.gameHolder.getGame();
			if (!Game.isInitialized(g))
				return;
			Container<?> items = g.getTurn().getContainer(this.turnElementType);
			ArrayList<Object> filteredItems = new ArrayList<Object>();
			this.applyFilter(filteredItems, items);
			this.tableModel.setRows(filteredItems);
		} else {
			Game g = this.gameHolder.getGame();
			if (!Game.isInitialized(g))
				return;
			GameMetadata gm = g.getMetadata();
			try {
				Container<?> items = (Container<?>) PropertyUtils.getProperty(gm, this.metadataProperty);
				ArrayList<Object> filteredItems = new ArrayList<Object>();
				this.applyFilter(filteredItems, items);
				this.tableModel.setRows(filteredItems);
			} catch (Exception exc) {
				// todo fix
				this.tableModel.setRows(new ArrayList<Object>());
			}
		}
		// tableModel.fireTableDataChanged();
	}
}
