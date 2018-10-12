package org.joverseer.ui.support.controls;

import java.awt.Dimension;

import javax.swing.JComboBox;

import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

public class NationComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	// inject this
	GameHolder gh;
	
	public NationComboBox(GameHolder gh) {
		super();
		this.setPreferredSize(new Dimension(160, 24));
		this.gh = gh;
	}
	public Nation load(boolean autoFocusOnGameNation,boolean onlyImported) {
		this.removeAllItems();
		Game g = this.gh.getGame();
		if (!Game.isInitialized(g))
			return null;
		Turn t = g.getTurn(); 
		if (t == null)
			return null;

        Nation selectedNation = null;
		for (Nation n : g.getMetadata().getNations()) {
	        if (n.isActivePlayer()) {
	        	if (onlyImported) {
	        		NationEconomy ne = (NationEconomy) t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber()); //$NON-NLS-1$
	        		if (ne == null)
	        			continue;
	        	}
				this.addItem(n.getName());
				if (autoFocusOnGameNation && n.getNumber() == g.getMetadata().getNationNo()) {
	            	selectedNation = n;
				}
	        }
		}
        if (selectedNation == null) {
        	if (this.getItemCount() > 0) {
	            this.setSelectedIndex(0);
	        }
        } else {
        	this.setSelectedItem(selectedNation.getName());
        }
        return selectedNation;
	}
	public int getSelectedNationNo() {
		Game g = this.gh.getGame();
		if (this.getSelectedItem() == null)
			return -1;
		Nation n = g.getMetadata().getNationByName(this.getSelectedItem().toString());
		return n.getNumber();
	}
	public Nation getSelectedNation() {
        Game g = this.gh.getGame();
        if (this.getSelectedItem() == null)
            return null;
        Nation n = g.getMetadata().getNationByName(this.getSelectedItem().toString());
        return n;
	}

}
