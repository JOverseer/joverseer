package org.joverseer.ui.domain.mapItems;

import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.HexInfoHistory;
import org.joverseer.ui.support.Messages;

/**
 * Holds information about the Hex Info Turn Report
 * 
 * @author Marios Skounakis
 *
 */
public class HexInfoTurnReportMapItem extends AbstractMapItem {
    
    private static final long serialVersionUID = -3977172376914475299L;
	HashMap hexes;
    
    public HexInfoTurnReportMapItem() {
    	Game g = GameHolder.instance().getGame();
    	this.hexes = new HashMap();
    	for (Hex h : (ArrayList<Hex>)g.getMetadata().getHexes()) {
    		int i = HexInfoHistory.getLatestHexInfoTurnNoForHex(h.getHexNo());
    		this.hexes.put(h.getHexNo(), i);
    	}
    }
    
	public HashMap getHexes() {
		return this.hexes;
	}

	@Override
	public String getDescription() {
		return Messages.getString("HexInfoTurnReportMapItem.HexInfoReport"); //$NON-NLS-1$
	}

	@Override
	public boolean isEquivalent(AbstractMapItem mi) {
		return (mi instanceof HexInfoTurnReportMapItem)
				&& (this.hexes == ((HexInfoTurnReportMapItem)mi).hexes); 
	}

}
