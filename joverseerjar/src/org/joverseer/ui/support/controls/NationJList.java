package org.joverseer.ui.support.controls;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;

import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

/**
 * An extension of JList which loads in the gameholders nations into the JList
 * allowing for a selection of multiple nations.
 * 
 * @author Sam Terrett
 */
public class NationJList extends JList {

	private static final long serialVersionUID = 1L;
	GameHolder gh;
	
	public NationJList(GameHolder gh) {
		super();
		//this.setPreferredSize(new Dimension(160, 24));
		this.gh = gh;
	}
	/**
	 * @param autoFocusOnGameNation
	 * @param onlyImported
	 * @return
	 */
	public Nation load(boolean autoFocusOnGameNation,boolean onlyImported) {
		return this.load(autoFocusOnGameNation, onlyImported, -1);
	}
	
	public Nation load(boolean autoFocusOnGameNation,boolean onlyImported,int initNationNo) {
		Game g = this.gh.getGame();
		if (!Game.isInitialized(g))
			return null;
		Turn t = g.getTurn(); 
		if (t == null)
			return null;

        Nation selectedNation=null;
        ArrayList<String> nationNames = new ArrayList<String>();

		for (Nation n : g.getMetadata().getNations()) {
	        if (n.isActivePlayer()) {
	        	if (onlyImported) {
	        		NationEconomy ne = (NationEconomy) t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber()); //$NON-NLS-1$
	        		if (ne == null)
	        			continue;
	        	}
				nationNames.add(n.getName());
				if (autoFocusOnGameNation && (n.getNumber() == g.getMetadata().getNationNo())) {
	            	selectedNation = n;
				}
	        }
		}
		String nationNamesComplete[] = nationNames.toArray(new String[nationNames.size()]);
		this.setListData(nationNamesComplete);
        if (selectedNation == null) {
	        this.setSelectedIndex(0);

        }
        return selectedNation;
	}
	public int[] getSelectedNationNos() {
		Game g = this.gh.getGame();

		String[] nationsStr = getSelectedNations();
		if (nationsStr == null) {
			return null;
		}
		
		int[] nationNos = new int[nationsStr.length];
		for (int i = 0; i < nationsStr.length; i++) {
			nationNos[i] = g.getMetadata().getNationByName(nationsStr[i]).getNumber();
		}
		return nationNos;
		
	}
	public String[] getSelectedNations() {
        Game g = this.gh.getGame();
        if (this.getSelectedValuesList().size() == 0)
            return null;
        List<String> ns = this.getSelectedValuesList();

        String[] nsNew = new String[ns.size()];
        for (int i = 0; i < ns.size(); i++) {
        	nsNew[i] = ns.get(i);
        }

        return nsNew;
	}
}
