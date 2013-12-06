package org.joverseer.ui.economyCalculator;

import java.util.ArrayList;

import javax.swing.JTable;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;

/**
 * Table model for the Economy Totals table for the Economy Calculator
 * 
 * Provides various methods for calculating fields of the Economy Totals table
 * 
 * @author Marios Skounakis
 */
public class EconomyTotalsTableModel extends BaseEconomyTableModel {
    String[] columnHeaders = new String[] {"", "", "", "", "", ""};
    // "row headers", they go into column 0 of the table
    String[] rowHeaders1 = new String[] {
            "army maintenance",
            "pc maintenance",
            "char maintenance",
            "total maintenance",
            "pc losses"
            };

    // "row headers 2", they go into column 2 of the table
    String[] rowHeaders2 = new String[] {
            "tax rate",
            "tax revenue",
            "gold production",
            "market profits",
            "surplus"
            };

    //  "row headers 3", they go into column 4 of the table
    String[] rowHeaders3 = new String[] {
            "orders cost",
            "",
            "starting gold",
            "final gold",
            ""
            };

    int[] columnWidths = new int[] {170, 64, 128, 64, 128, 64};

    JTable table;
    
    public void setTable(JTable table) {
    	this.table = table;
    }
    
    @Override
	public int getColumnCount() {
        return 6;
    }

    @Override
	public int getRowCount() {
        return 5;
    }
    
    @Override
	public String getColumnName(int column) {
        return this.columnHeaders[column];
    }

    public int getColumnWidth(int column) {
        return this.columnWidths[column];
    }
    
    @Override
	public Class<?> getColumnClass(int column) {
        if (column == 0 || column == 2 || column == 4) return String.class;
        return Integer.class;
    }

    @Override
	public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return this.rowHeaders1[rowIndex];
        }
        if (columnIndex == 2) {
            return this.rowHeaders2[rowIndex];
        }
        if (columnIndex == 4) {
            return this.rowHeaders3[rowIndex];
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
                    return getTaxRate();
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
                    return getFinalGold();
            }
            return "";
        }
        return "";
    }
    
    protected void select(int rowIndex, int columnIndex) {
    	try {
    		this.table.setColumnSelectionInterval(columnIndex, columnIndex);
    		this.table.setRowSelectionInterval(rowIndex, rowIndex);
    	}
    	catch (Exception exc) {
    		// do nothing
    	}
    }
    
    /**
     * Computes the final gold for the nation
     * @return
     */
    public int getFinalGold() {
        NationEconomy ne = getNationEconomy();
        return getTaxRevenue() + getMarketProfits() + getGoldProduction() - ne.getTotalMaintenance() - getOrdersCost() + ne.getReserve() - computeLostGoldRevenue() - computeLostTaxRevenue();
    }
    
    /**
     * Computes the tax revenue for the nation
     * @return
     */
    public int getTaxRevenue() {
        NationEconomy ne = getNationEconomy();
        return ne.getTaxBase() * 2500 * getTaxRate() / 100;
    }
    
    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 5 && rowIndex == 0) ||
                (columnIndex == 3 && rowIndex == 2) ||
                (columnIndex == 3 && rowIndex == 0);
    }

    @Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 5 && rowIndex == 0) {
            setOrdersCost((Integer)aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (columnIndex == 3 && rowIndex == 2) {
            setGoldProduction((Integer)aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (columnIndex == 3 && rowIndex == 0) {
            setTaxRate((Integer)aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
    }

    /**
     * Returns the surplus for the nation
     */
    public int getSurplus() {
        NationEconomy ne = getNationEconomy();
        return getTaxRevenue() + getGoldProduction() - ne.getTotalMaintenance() + getMarketProfits();
    }

    
    public Integer getMarketProfits() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getMarketProfits();
    }
    
    /**
     * Computes the gold production for the nation, unless it has been specicically set
     * by the user
     * @return
     */
    public Integer getGoldProduction() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null || ecd.getGoldProduction() == null) {
            NationEconomy ne = getNationEconomy();
            // hack to compute gold production if needed
            // gold production = total revenue - calculated tax revenue
            if (ne.getGoldProduction()==0 && 
                    ne.getRevenue() - ne.getTaxBase() * 2500 * ne.getTaxRate() / 100 != 0) {
                ne.setGoldProduction(ne.getRevenue() - ne.getTaxBase() * 2500 * ne.getTaxRate() / 100);
            }
            return ne.getGoldProduction();
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
    
    public void setTaxRate(int newTaxRate) {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return;
        ecd.setTaxRate(newTaxRate);
    }
    
    /**
     * Gets the tax rate from the nation economy, unless it has been specifically set by the user
     * 
     * @return
     */
    public int getTaxRate() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null || ecd.getTaxRate() == null) {
            NationEconomy ne = getNationEconomy();
            return ne.getTaxRate();
        }
        return ecd.getTaxRate();
    }

    public int computeLostTaxRevenue() {
        if (getNationNo() < 1) return 0;
        return computeLostTaxRevenue(getNationNo());
    }
    
    /**
     * Computes the expected lost tax revenue based on the pop centers marked as expected to be lost
     * for the given nation
     */
    public static int computeLostTaxRevenue(int nationNo) {
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        if (g.getTurn() == null) return 0;
        int lostTaxRevenue = 0;
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nationNo) && pc.getLostThisTurn()) {
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
                NationEconomy ne = (NationEconomy)g.getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", nationNo);
                EconomyCalculatorData ecd = (EconomyCalculatorData)g.getTurn().getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", nationNo);
                if (ecd == null || ecd.getTaxRate() == null) {
                    lostTaxRevenue += sz * 2500 * ne.getTaxRate() / 100;
                } else {
                    lostTaxRevenue += sz * 2500 * ecd.getTaxRate() / 100; 
                }
            }
            
        }
        return lostTaxRevenue;
    }

    public int computeNewTaxBase() {
        if (getNationNo() < 1) return 0;
        return computeNewTaxBase(getNationNo());
    }
    
    /**
     * Computes the new tax base for the given nation based on the pop centers
     * marked as expected to be lost this turn
     */
    public static int computeNewTaxBase(int nationNo) {
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        if (g.getTurn() == null) return 0;
        int newTaxBase = 0;
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nationNo) && !pc.getLostThisTurn()) {
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
                newTaxBase += sz;
            }
            
        }
        return newTaxBase;
    }

    public int computeLostGoldRevenue() {
        if (getNationNo() < 1) return 0;
        return computeLostGoldRevenue(getNationNo());
    }
    
    /**
     * Computes the lost gold revenue for the given nation based on the pop centers
     * marked as expected to be lost this turn
     */
    public static int computeLostGoldRevenue(int nationNo) {
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        if (g.getTurn() == null) return 0;
        int lostGoldRevenue = 0;
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nationNo) && pc.getLostThisTurn()) {
                if (pc.getProduction(ProductEnum.Gold) != null) {
                    lostGoldRevenue += pc.getProduction(ProductEnum.Gold);
                }
            }
        }
        return lostGoldRevenue;
    }
    
    /**
     * Computes the tax increase 
     * Returns 0 if no tax increase will occur 
     */
    public int getTaxIncrease() {
        NationEconomy ne = getNationEconomy();
        if (ne == null) return 0;
        int finalGold = getTaxRevenue() + getMarketProfits() + getGoldProduction() - ne.getTotalMaintenance() + ne.getReserve() - computeLostGoldRevenue() - computeLostTaxRevenue();
        if (finalGold >= 0) return 0;
        //double newTaxRate = Math.round((double)computeLostGoldRevenue() - (double)finalGold) / ((double)getTaxRevenue() / (double)ne.getTaxRate() - (double)computeLostTaxRevenue() / (double)ne.getTaxRate());
        double newTaxRate = Math.round((double)-finalGold / (double)2500 / (double)computeNewTaxBase()* 100d);
        return (int)newTaxRate;
    }
    
    /**
     * Computes the amount of gold that must be bought in order to incur the given
     * tax increase 
     */
    public int getBuyAmountForTaxIncrease(int newTaxRate) {
        NationEconomy ne = getNationEconomy();
        if (ne == null) return 0;
        double finalGold = Math.round(
                        ((double)getTaxRevenue() - (double)computeLostTaxRevenue()) * (double)newTaxRate / (double)getTaxRate() 
                        + (double)getGoldProduction() - (double)computeLostGoldRevenue() -
                        (double)ne.getTotalMaintenance() + (double)ne.getReserve());
        return (int)finalGold;
    }
    
    public void updateMarketFromOrders() {
    	getEconomyCalculatorData().updateMarketFromOrders();
    }
}
