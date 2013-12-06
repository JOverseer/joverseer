package org.joverseer.ui.economyCalculator;

import javax.swing.table.AbstractTableModel;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;

/**
 * Base table model for the Economy Calculator table models
 * 
 * @author Marios Skounakis
 */
public abstract class BaseEconomyTableModel extends AbstractTableModel {
	Game game = null;
	int nationNo = 7;

	protected NationEconomy getNationEconomy() {
		if (!Game.isInitialized(getGame()))
			return null;
		if (getGame().getTurn() == null)
			return null;
		Turn t = this.game.getTurn();
		Container<NationEconomy> nes = t.getNationEconomies();
		NationEconomy ne = nes.findFirstByProperty("nationNo", getNationNo());
		return ne;
	}

	protected EconomyCalculatorData getEconomyCalculatorData() {
		if (!Game.isInitialized(getGame()))
			return null;
		if (getGame().getTurn() == null)
			return null;
		Turn t = this.game.getTurn();
		Container<EconomyCalculatorData> nes = t.getEconomyCalculatorData();
		EconomyCalculatorData ecd = nes.findFirstByProperty("nationNo", getNationNo());
		if (ecd == null) {
			ecd = new EconomyCalculatorData();
			ecd.setNationNo(getNationNo());
			nes.addItem(ecd);
		}
		return ecd;
	}

	protected Game getGame() {
		if (this.game == null) {
			this.game = GameHolder.instance().getGame();
		}
		return this.game;
	}

	public void setGame(Game g) {
		this.game = g;
	}

	protected Integer getNationNo() {
		return this.nationNo;
	}

	protected void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}
}
