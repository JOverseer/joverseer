package org.joverseer.ui.support.controls;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

/**
 * A specific version of my DualJListSelector which loads in the gameholders nations into the JLists
 * allowing for a selection of multiple nations.
 * 
 * @author Sam Terrett
 */
public class NationDualListSelector extends DualJListSelector {
	private static final long serialVersionUID = 1L;
	GameHolder gh;
	
	public NationDualListSelector(GameHolder gh) {
		super(false);
		this.gh = gh;
	}
	
	/**
	 * @param autoFocusOnGameNation
	 * @param onlyImported
	 * @return
	 */
	public void load(boolean autoFocusOnGameNation,boolean onlyImported) {
		this.load(autoFocusOnGameNation, onlyImported, null);
	}
	
	public void load(boolean autoFocusOnGameNation,boolean onlyImported, int[] nationNums) {
		Game g = this.gh.getGame();
		if (!Game.isInitialized(g))
			return;
		Turn t = g.getTurn(); 
		if (t == null)
			return;

		String[] selectedNation;
		if(nationNums == null) selectedNation= new String[1];
		else selectedNation = new String[nationNums.length];
		int index = 0;
		
        ArrayList<String> nationNames = new ArrayList<String>();

		for (Nation n : g.getMetadata().getNations()) {
	        if (n.isActivePlayer()) {
	        	if (onlyImported) {
	        		NationEconomy ne = (NationEconomy) t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber()); //$NON-NLS-1$
	        		if (ne == null)
	        			continue;
	        	}
				
				if ((autoFocusOnGameNation && (n.getNumber() == g.getMetadata().getNationNo())) || (nationNums != null && arrayContainsInt(nationNums, n.getNumber().intValue()))) {
	            	selectedNation[index++] = n.getName();
				}
				else nationNames.add(n.getName());
	        }
		}
		String nationNamesComplete[] = nationNames.toArray(new String[nationNames.size()]);

		this.setSelectorItems(nationNamesComplete, selectedNation);
		
	}
	
	/**
	 * Checks if inputed int array contains an integer value
	 */
	private boolean arrayContainsInt(int[] inp, int toCheck) {
		if (inp == null) return false;
		for ( int i : inp) {
			if (i == toCheck) {
				return true;
			}
		}
		return false;
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
        if (this.getSelectedItems().size() == 0) return null;
        
        Object[] nss = this.getSelectedItems().toArray();
        String[] ns = new String[nss.length];
        
        for (int i = 0; i < nss.length; i++) {
        	ns[i] = nss[i].toString();
        }

        return ns;
	}    
}
