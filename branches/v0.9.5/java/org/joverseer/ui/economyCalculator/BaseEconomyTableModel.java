package org.joverseer.ui.economyCalculator;

import javax.swing.table.AbstractTableModel;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;


public abstract class BaseEconomyTableModel extends AbstractTableModel {
    Game game = null;
    int selectedNationNo = 7;
    
    protected NationEconomy getNationEconomy() {
        if (!Game.isInitialized(getGame())) return null;
        if (getGame().getTurn() == null) return null;
        Turn t = game.getTurn();
        Container nes = t.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy ne = (NationEconomy) nes.findFirstByProperty("nationNo", getSelectedNationNo());
        return ne;
    }
    
    protected EconomyCalculatorData getEconomyCalculatorData() {
        if (!Game.isInitialized(getGame())) return null;
        if (getGame().getTurn() == null) return null;
        Turn t = game.getTurn();
        Container nes = t.getContainer(TurnElementsEnum.EconomyCalucatorData);
        EconomyCalculatorData ecd = (EconomyCalculatorData) nes.findFirstByProperty("nationNo", getSelectedNationNo());
        if (ecd == null) {
            ecd = new EconomyCalculatorData();
            ecd.setNationNo(getSelectedNationNo());
            nes.addItem(ecd);
        }
        return ecd;
    }

    protected Game getGame() {
        if (game == null) {
            game = GameHolder.instance().getGame();
        }
        return game;
    }
    
    protected Integer getSelectedNationNo() {
        return selectedNationNo;
    }
    
    protected void setSelectedNationNo(int nationNo) {
        selectedNationNo = nationNo;
    }
}
