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
    int nationNo = 7;
    
    protected NationEconomy getNationEconomy() {
        if (!Game.isInitialized(getGame())) return null;
        if (getGame().getTurn() == null) return null;
        Turn t = game.getTurn();
        Container nes = t.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy ne = (NationEconomy) nes.findFirstByProperty("nationNo", getNationNo());
        return ne;
    }
    
    protected EconomyCalculatorData getEconomyCalculatorData() {
        if (!Game.isInitialized(getGame())) return null;
        if (getGame().getTurn() == null) return null;
        Turn t = game.getTurn();
        Container nes = t.getContainer(TurnElementsEnum.EconomyCalucatorData);
        EconomyCalculatorData ecd = (EconomyCalculatorData) nes.findFirstByProperty("nationNo", getNationNo());
        if (ecd == null) {
            ecd = new EconomyCalculatorData();
            ecd.setNationNo(getNationNo());
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
    
    public void setGame(Game g) {
        game = g;
    }
    
    protected Integer getNationNo() {
        return nationNo;
    }
    
    protected void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }
}
