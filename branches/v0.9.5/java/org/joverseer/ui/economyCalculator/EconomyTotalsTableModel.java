package org.joverseer.ui.economyCalculator;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;


public class EconomyTotalsTableModel extends BaseEconomyTableModel {
    String[] columnHeaders = new String[] {"", "", "", "", "", ""};
    String[] rowHeaders1 = new String[] {
            "army maintenance",
            "pc maintenance",
            "char maintenance",
            "total maintenance",
            "pc losses"
            };

    String[] rowHeaders2 = new String[] {
            "tax rate",
            "tax revenue",
            "gold production",
            "market profits",
            "surplus"
            };

    String[] rowHeaders3 = new String[] {
            "orders cost",
            "",
            "starting gold",
            "final gold",
            ""
            };

    int[] columnWidths = new int[] {170, 64, 128, 64, 128, 64};

    public int getColumnCount() {
        return 6;
    }

    public int getRowCount() {
        return 5;
    }
    
    public String getColumnName(int column) {
        return columnHeaders[column];
    }

    public int getColumnWidth(int column) {
        return columnWidths[column];
    }
    
    public Class<?> getColumnClass(int column) {
        if (column == 0 || column == 2 || column == 4) return String.class;
        return Integer.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowHeaders1[rowIndex];
        }
        if (columnIndex == 2) {
            return rowHeaders2[rowIndex];
        }
        if (columnIndex == 4) {
            return rowHeaders3[rowIndex];
        }
        if (!Game.isInitialized(getGame())) return "";
        if (getGame().getTurn() == null) return "";
        NationEconomy ne = getNationEconomy();
        if (ne == null) return "";
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return "";
        if (!ecd.isInitialized()) return "";

        if (columnIndex == 1) {
            switch (rowIndex) {
                case 0:
                    return ne.getArmyMaintenance();
                case 1:
                    return ne.getPopMaintenance();
                case 2:
                    return ne.getCharMaintenance();
                case 3:
                    return ne.getTotalMaintenance();
                case 4:
                    //PC Losses
                    return computeLostGoldRevenue() + computeLostTaxRevenue();
            }
            return "";
        }
        if (columnIndex == 3) {
            switch (rowIndex) {
                case 0:
                    return ne.getTaxRate();
                case 1:
                    return getTaxRevenue();
                case 2:
                    return getGoldProduction();
                case 3:
                    // Market profits
                    return getMarketProfits();
                case 4:
                    // Surplus
                    return getSurplus();
            }
            return "";
        }
        if (columnIndex == 5) {
            switch (rowIndex) {
                case 0:
                    return ecd.getOrdersCost();
                case 2:
                    return ne.getReserve();
                case 3:
                    // final gold
                    return getTaxRevenue() + getMarketProfits() + getGoldProduction() - ne.getTotalMaintenance() - getOrdersCost() + ne.getReserve() - computeLostGoldRevenue() - computeLostTaxRevenue();
            }
            return "";
        }
        return "";
    }
    
    public int getTaxRevenue() {
        NationEconomy ne = getNationEconomy();
        return ne.getTaxBase() * 2500 * ne.getTaxRate() / 100;
    }
    
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 5 && rowIndex == 0) ||
                (columnIndex == 3 && rowIndex == 2);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 5 && rowIndex == 0) {
            setOrdersCost((Integer)aValue);
            fireTableDataChanged();
        }
        if (columnIndex == 3 && rowIndex == 2) {
            setGoldProduction((Integer)aValue);
            fireTableDataChanged();
        }
    }

    public int getSurplus() {
        NationEconomy ne = getNationEconomy();
        return getTaxRevenue() + getGoldProduction() - ne.getTotalMaintenance() + getMarketProfits();
    }

    
    public Integer getMarketProfits() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getMarketProfits();
    }
    
    public Integer getGoldProduction() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null || ecd.getGoldProduction() == null) {
            NationEconomy ne = getNationEconomy();
            return ne.getRevenue() - ne.getTaxBase() * 2500 * ne.getTaxRate() / 100; 
        }
        return ecd.getGoldProduction();
    }
    
    public void setGoldProduction(Integer goldProduction) {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return;
        ecd.setGoldProduction(goldProduction);
    }
    
    public Integer getOrdersCost() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getOrdersCost();
    }

    
    public void setOrdersCost(int ordersCost) {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return;
        ecd.setOrdersCost(ordersCost);
    }

    public int computeLostTaxRevenue() {
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        if (g.getTurn() == null) return 0;
        if (getSelectedNationNo() < 1) return 0;
        int lostTaxRevenue = 0;
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(getSelectedNationNo()) && pc.getLostThisTurn()) {
                int sz = 0;
                if (pc.getSize() == PopulationCenterSizeEnum.village) {
                    sz = 1;
                } else if (pc.getSize() == PopulationCenterSizeEnum.town) {
                    sz = 2;
                } else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
                    sz = 3;
                } else if (pc.getSize() == PopulationCenterSizeEnum.city) {
                    sz = 4;
                }
                lostTaxRevenue += sz * 2500 * getNationEconomy().getTaxRate() / 100; 
            }
            
        }
        return lostTaxRevenue;
    }

    public int computeLostGoldRevenue() {
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        if (g.getTurn() == null) return 0;
        if (getSelectedNationNo() < 1) return 0;
        int lostGoldRevenue = 0;
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(getSelectedNationNo()) && pc.getLostThisTurn()) {
                if (pc.getProduction(ProductEnum.Gold) != null) {
                    lostGoldRevenue += pc.getProduction(ProductEnum.Gold);
                }
            }
        }
        return lostGoldRevenue;
    }
    
    public int getTaxIncrease() {
        NationEconomy ne = getNationEconomy();
        if (ne == null) return 0;
        int finalGold = getTaxRevenue() + getMarketProfits() + getGoldProduction() - ne.getTotalMaintenance() - getOrdersCost() + ne.getReserve() - computeLostGoldRevenue() - computeLostTaxRevenue();
        if (finalGold >= 0) return 0;
        int newTaxRevenue = (computeLostGoldRevenue() - finalGold) / (getTaxRevenue() / ne.getTaxRate() - computeLostTaxRevenue() / ne.getTaxRate());  
        return newTaxRevenue;
    }
}
