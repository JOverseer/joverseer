package org.joverseer.ui.economyCalculator;

import javax.swing.JTable;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;

/**
 * Table model for the market actions of a nation for the Economy Calculator
 * @author Marios Skounakis
 */
public class MarketTableModel extends BaseEconomyTableModel {
    // row 0 of the table
    String[] columnHeaders = new String[] {"", "le", "br", "st", "mi", "fo", "ti", "mo"};
    // column 0 of the table
    String[] rowHeaders = new String[] {"stores", "production", "available to sell", "profit if all were sold",
            "sell price", "units you wish to sell", "percent you wish to sell", "available on market",
            "purchase price", "units you wish to buy", "bid price", "units your wish to bid for", "cost/profit"};

    // column widths
    int[] columnWidths = new int[] {170, 64, 64, 64, 64, 64, 64, 64};
    JTable table;
    EconomyTotalsTableModel ettm;

    public void setTable(JTable table) {
    	this.table = table;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        String productCode = columnHeaders[columnIndex];
        ProductEnum product = ProductEnum.getFromCode(productCode);
        if (rowIndex == 5) {
            ecd.setSellUnits(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (rowIndex == 6) {
            ecd.setSellPct(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (rowIndex == 9) {
            ecd.setBuyUnits(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (rowIndex == 10) {
            ecd.setBidPrice(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
        if (rowIndex == 11) {
            if (aValue != null && aValue.toString().startsWith("-")) {
                String tr = aValue.toString().substring(1);
                Integer newTr = Integer.parseInt(tr);
                int amt = getTotalsModel().getBuyAmountForTaxIncrease(newTr);
                if (amt > 0 && ecd.getBidPrice(product) > 0) {
                    aValue = (int)Math.round((double)amt / (double)ecd.getBidPrice(product));
                } else {
                    aValue = 0;
                }
            }
            ecd.setBidUnits(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
            select(rowIndex, columnIndex);
        }
    }
    
    protected void select(int rowIndex, int columnIndex) {
    	try {
    		table.setColumnSelectionInterval(columnIndex, columnIndex);
    		table.setRowSelectionInterval(rowIndex, rowIndex);
    	}
    	catch (Exception exc) {
    		// do nothing
    	}
    }

    public int getColumnCount() {
        return 8;
    }

    public int getRowCount() {
        return 13;
    }

    public String getColumnName(int column) {
        return columnHeaders[column];
    }

    public int getColumnWidth(int column) {
        return columnWidths[column];
    }


    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0)
            return String.class;
        return Integer.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 0 && (rowIndex == 5 || rowIndex == 6 || rowIndex == 9 || rowIndex == 10 || rowIndex == 11);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return rowHeaders[rowIndex];
        if (!Game.isInitialized(getGame()))
            return "";
        if (getGame().getTurn() == null) return "";
        NationEconomy ne = getNationEconomy();
        if (ne == null) return "";
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        if (ecd == null) return "";
        if (!ecd.isInitialized()) return "";
        String productCode = columnHeaders[columnIndex];
        ProductEnum product = ProductEnum.getFromCode(productCode);
        if (rowIndex == 0) {
            return ecd.getStores(product);
        }
        if (rowIndex == 1) {
            return ecd.getProduction(product);
        }
        if (rowIndex == 2) {
            return ecd.getTotal(product);
        }
        if (rowIndex == 3) {
            // TODO include sell bonus
            return ecd.getTotal(product) * ecd.getSellPrice(product);
        }
        if (rowIndex == 4) {
            return ecd.getSellPrice(product);
        }
        if (rowIndex == 5) {
            return ecd.getSellUnits(product);
        }
        if (rowIndex == 6) {
            return ecd.getSellPct(product);
        }
        if (rowIndex == 7) {
            return ecd.getMarketTotal(product);
        }
        if (rowIndex == 8) {
            return ecd.getBuyPrice(product);
        }
        if (rowIndex == 9) {
            return ecd.getBuyUnits(product);
        }
        if (rowIndex == 10) {
            return ecd.getBidPrice(product);
        }
        if (rowIndex == 11) {
            return ecd.getBidUnits(product);
        }
        if (rowIndex == 12) {
            return ecd.getMarketProfits(product);
        }
        return "";
    }

    
    public EconomyTotalsTableModel getTotalsModel() {
        return ettm;
    }

    
    public void setTotalsModel(EconomyTotalsTableModel ettm) {
        this.ettm = ettm;
    }

    

}
