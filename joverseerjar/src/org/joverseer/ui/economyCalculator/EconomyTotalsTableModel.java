package org.joverseer.ui.economyCalculator;

import java.util.ArrayList;

import javax.swing.JTable;

import org.joverseer.JOApplication;
import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.Messages;

/**
 * Table model for the Economy Totals table for the Economy Calculator
 *
 * Provides various methods for calculating fields of the Economy Totals table
 *
 * @author Marios Skounakis
 */
public class EconomyTotalsTableModel extends BaseEconomyTableModel {
    /**
	 *
	 */
	private static final int iHeaderCol0 =0;
	public static final int iValueCol0=1;
	private static final int iHeaderCol1=2;
	public static final int iValueCol1=3;
	private static final int iHeaderCol2=4;
	public static final int iValueCol2=5;
	private static final int iHeaderCol3=6;
	public static final int iValueCol3=7;

	public static final int iStartingGoldRow = 0;
	private static final int iTaxRevenueRow = 1;
	private static final int iCharMaintRow = 1;
	private static final int iTaxRateRow = 1;
	private static final int iGoldProductionRow = 2;
	private static final int iPCMaintRow = 2;
	public static final int iOrdersCostRow = 3;
	private static final int iPCLossesRow =3;
	private static final int iArmyMaintRow =3;
	public static final int iMarketSalesRow =3;
	private static final int iMarketSpendRow =2;
	public static final int iSurplusRow =3;
	private static final int iMarketProfitRow =4;
	public static final int iTotalRevenueRow =4;
	private static final int iTotalMaintRow =4;
	public static final int iTotalLossesRow =4;
	public static final int iFinalGoldRow = 5;

	private static final long serialVersionUID = -7961559423992117184L;
	String[] columnHeaders = new String[] {"", "", "", "", "", "","",""};
    // "row headers", they go into column 0 of the table
    String[] rowTags0 = new String[] {"starting","taxRevenue","gold","pclosses","totalRevenue","" };
    String[] rowTags1 = new String[] {"","char","pc","army","total",""};
    String[] rowTags2 = new String[] {"","","marketSpend","marketSales","profits",""};
    String[] rowTags3 = new String[] {"","taxRate","","ordersCost","","final"};
    String[] rowHeaders0;
    String[] rowHeaders1;
    String[] rowHeaders2;
    String[] rowHeaders3;

    int[] columnWidths = new int[] {100, 60, 95, 60, 85, 60,95,60};

    JTable table;
    public EconomyTotalsTableModel(GameHolder gameHolder)
    {
    	super(gameHolder);
		this.rowHeaders0 = new String[this.rowTags0.length];
		this.rowHeaders1 = new String[this.rowTags1.length];
		this.rowHeaders2 = new String[this.rowTags2.length];
		this.rowHeaders3 = new String[this.rowTags3.length];
		for (int i=0;i<this.rowTags0.length;i++) {
			if (!this.rowTags0[i].isEmpty()) {
				this.rowHeaders0[i] = (this.rowTags0[i] == null) ? "" : Messages.getString("EconomyCalculator.Totals." +this.rowTags0[i]);
			}
		}
		for (int i=0;i<this.rowTags1.length;i++) {
			if (!this.rowTags1[i].isEmpty()) {
				this.rowHeaders1[i] = Messages.getString("EconomyCalculator.Totals." +this.rowTags1[i]);
			}
		}
		for (int i=0;i<this.rowTags2.length;i++) {
			if (!this.rowTags2[i].isEmpty()) {
				this.rowHeaders2[i] = Messages.getString("EconomyCalculator.Totals." +this.rowTags2[i]);
			}
		}
		for (int i=0;i<this.rowTags3.length;i++) {
			if (!this.rowTags3[i].isEmpty()) {
				this.rowHeaders3[i] = (this.rowTags3[i] == null) ? "" : Messages.getString("EconomyCalculator.Totals." +this.rowTags3[i]);
			}
		}
    }
    public void setTable(JTable table) {
    	this.table = table;
    }

    @Override
	public int getColumnCount() {
        return iValueCol3+1;
    }

    @Override
	public int getRowCount() {
        return 6;
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
    	switch (column) {
    	case iHeaderCol0:
    	case iHeaderCol1:
    	case iHeaderCol2:
    	case iHeaderCol3:
    		return String.class;
    	default:
            return Integer.class;
    	}
    }

    @Override
	public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == iHeaderCol0) {
            return this.rowHeaders0[rowIndex];
        }
        if (columnIndex == iHeaderCol1) {
            return this.rowHeaders1[rowIndex];
        }
        if (columnIndex == iHeaderCol2) {
            return this.rowHeaders2[rowIndex];
        }
        if (columnIndex == iHeaderCol3) {
            return this.rowHeaders3[rowIndex];
        }
        if (getGame() == null) return "";
        if (getGame().getTurn() == null) return "";
        NationEconomy ne = getNationEconomy();
        if (ne == null) return "";
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return "";
        if (!ecd.isInitialized()) return "";

        if (columnIndex == iValueCol0) {
            switch (rowIndex) {
                case iStartingGoldRow:
            		return ne.getReserve();
                case iTaxRevenueRow:
                    return getTaxRevenue();
            	case iGoldProductionRow:
            		return getGoldProduction();
                case iPCLossesRow:
                    return -(computeLostGoldRevenue() + computeLostTaxRevenue());
            	case iTotalRevenueRow:
            		return getTotalRevenue();
            }
            return "";
        }
        if (columnIndex == iValueCol1) {
            switch (rowIndex) {
            	case iCharMaintRow:
            		return -ne.getCharMaintenance();
                case iPCMaintRow:
                    return -ne.getPopMaintenance();
            	case iArmyMaintRow:
                    return -ne.getArmyMaintenance();
            	case iTotalMaintRow:
                    return -ne.getTotalMaintenance();
            }
            return "";
        }
        if (columnIndex == iValueCol2) {
            switch (rowIndex) {
               	case iMarketSalesRow:
            		return getMarketSales();
                case iMarketSpendRow:
                    return getMarketSpend();
            	case iMarketProfitRow:
            		return getMarketProfits();
//            	case iTotalLossesRow:
//            		return getTotalLosses();
            }
            return "";
        }
        if (columnIndex == iValueCol3) {
            switch (rowIndex) {
            	case iTaxRateRow:
            		return getTaxRate();
            	case iOrdersCostRow:
                	return -ecd.getOrdersCost();
//              case iSurplusRow:
//            	return getSurplus();
                case iFinalGoldRow:
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

    public int getTotalRevenue() {
        return getTaxRevenue() + getGoldProduction()  - computeLostGoldRevenue() - computeLostTaxRevenue();
    }
    public int getTotalLosses() {
        NationEconomy ne = getNationEconomy();
        return -ne.getTotalMaintenance() - getOrdersCost() - computeLostGoldRevenue() - computeLostTaxRevenue() -getMarketSpend();
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
    	EconomyCalculatorData ecd = getEconomyCalculatorData();
    	if(ecd == null) {
	        NationEconomy ne = getNationEconomy();
	        if (ne == null) {
	        	return 0;
	        }
    	
	        return ne.getTaxBase() * 2500 * getTaxRate() / 100;
    	}
    	
    	return ecd.getTaxRevenue();
    }

    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == iValueCol2 && rowIndex == iOrdersCostRow) ||
                (columnIndex == iValueCol0 && rowIndex == iGoldProductionRow) ||
                (columnIndex == iValueCol3 && rowIndex == iTaxRateRow);
    }

    @Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == iValueCol2 && rowIndex == iOrdersCostRow) {
            setOrdersCost((Integer)aValue);
            fireTableDataChanged();
            JOApplication.publishEvent(LifecycleEventsEnum.EconomyCalculatorUpdate, this, this);
            select(rowIndex, columnIndex);
        }
        if (columnIndex == iValueCol0 && rowIndex == iGoldProductionRow) {
            setGoldProduction((Integer)aValue);
            fireTableDataChanged();
            JOApplication.publishEvent(LifecycleEventsEnum.EconomyCalculatorUpdate, this, this);
            select(rowIndex, columnIndex);
        }
        if (columnIndex == iValueCol3 && rowIndex == iTaxRateRow) {
            setTaxRate((Integer)aValue);
            fireTableDataChanged();
            JOApplication.publishEvent(LifecycleEventsEnum.EconomyCalculatorUpdate, this, this);
            select(rowIndex, columnIndex);
        }
    }

    /**
     * Returns the surplus for the nation
     */
    public int getSurplus() {
        NationEconomy ne = getNationEconomy();
        return getTaxRevenue() + getGoldProduction() - ne.getTotalMaintenance();
    }


    public Integer getMarketSales() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getMarketSales();
    }
    public Integer getMarketSpend() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getMarketSpend();
    }
    public Integer getMarketProfits() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return null;
        return ecd.getMarketProfits();
    }

    /**
     * Computes the gold production for the nation, unless it has been specifically set
     * by the user
     * @return
     */
    public Integer getGoldProduction() {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null || ecd.getGoldProduction() == null) {
            NationEconomy ne = getNationEconomy();
            // hack to compute gold production if needed
            // gold production = total revenue - calculated tax revenue
            // as of december 2020 the game engine reports a tax base excluding seiged pop centres.
            if (ne.getGoldProduction()==0 &&
                    ne.getRevenue() - ne.getTaxBase() * 2500 * ne.getTaxRate() / 100 != 0) {
                ne.setGoldProduction(ne.getRevenue() - ne.getTaxBase() * 2500 * ne.getTaxRate() / 100);
            }
            return ne.getGoldProduction();
        }
        if(ecd.getGoldProduction() != null) return ecd.getGoldProduction();
        return ecd.getProduction(ProductEnum.Gold);
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
    public int computeLostTaxRevenue(int nation) {
        Turn t = this.getTurn();
        if (t == null) return 0;
        int lostTaxRevenue = 0;

        for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nation) && pc.getLostThisTurn()) {
                int sz = pc.lookupSize(new int[] {0,0,1,2,3,4});
                NationEconomy ne = (NationEconomy)t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", nation);
                EconomyCalculatorData ecd = (EconomyCalculatorData)t.getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", nation);
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
    public int computeNewTaxBase(int nation) {
        Turn t = this.getTurn();
        if (t == null) return 0;
        int newTaxBase = 0;

        for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nation) && !pc.getLostThisTurn()) {
                int sz = pc.lookupSize(new int[] {0,0,1,2,3,4});
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
    public int computeLostGoldRevenue(int nation) {
        Turn t = this.getTurn();
        if (t == null) return 0;
        int lostGoldRevenue = 0;

        for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            if (pc.getNationNo().equals(nation) && pc.getLostThisTurn()) {
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
