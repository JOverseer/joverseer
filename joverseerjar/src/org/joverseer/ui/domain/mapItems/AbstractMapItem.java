package org.joverseer.ui.domain.mapItems;

import java.io.Serializable;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;

/**
 * Abstract class that forms the base for all volatile (i.e. drawable, non
 * static) items that must be drawn on the map.
 * 
 * @author Marios Skounakis
 */
public abstract class AbstractMapItem implements Serializable {

	private static final long serialVersionUID = -687886243020263837L;

	public abstract String getDescription();
	
	public abstract boolean isEquivalent(AbstractMapItem mi);

	public static void add(AbstractMapItem mapItem) {
		Turn t = getTurn();
		if (t == null)
			return;
		Container<AbstractMapItem> mapItems = t.getMapItems();
		mapItems.addItem(mapItem);
	}

	public static void remove(AbstractMapItem mapItem) {
		Turn t = getTurn();
		if (t == null)
			return;
		Container<AbstractMapItem> mapItems = t.getMapItems();
		mapItems.removeItem(mapItem);
	}
	public static void toggle(AbstractMapItem mapItem) {
		Turn t = getTurn();
		if (t == null)
			return;
		Container<AbstractMapItem> mapItems = t.getMapItems();
		// we only remove the first found for simplicity and to avoid any iterating-over-a-changing-container issues.
		for (AbstractMapItem mi: mapItems) {
			if (mi.isEquivalent(mapItem)) {
				mapItems.removeItem(mi);
				return;	// note this is 1/2 normal exit points
			}
		}
		mapItems.addItem(mapItem);
	}
	protected static Turn getTurn() {
		Game g = GameHolder.instance().getGame();
		if (g == null || !Game.isInitialized(g))
			return null;
		return g.getTurn();
		
	}
}
