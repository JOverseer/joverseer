package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;


public class MarketTableModel extends BaseEconomyTableModel {

    String[] columnHeaders = new String[] {"", "le", "br", "st", "mi", "fo", "ti", "mo"};
    String[] rowHeaders = new String[] {"stores", "production", "available to sell", "profit if all were sold",
            "sell price", "units you wish to sell", "percent you wish to sell", "available on market",
            "purchase price", "units you wish to buy", "cost/profit"};

    int[] columnWidths = new int[] {170, 64, 64, 64, 64, 64, 64, 64};

    

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        EconomyCalculatorData ecd = getEconomyCalculatorData();
        String productCode = columnHeaders[columnIndex];
        ProductEnum product = ProductEnum.getFromCode(productCode);
        if (rowIndex == 5) {
            ecd.setSellUnits(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
        }
        if (rowIndex == 6) {
            ecd.setSellPct(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
        }
        if (rowIndex == 9) {
            ecd.setBuyUnits(product, (Integer) aValue);
            fireTableDataChanged();
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));
        }
    }

    public int getColumnCount() {
        return 8;
    }

    public int getRowCount() {
        return 11;
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
        return columnIndex > 0 && (rowIndex == 5 || rowIndex == 6 || rowIndex == 9);
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
            return ecd.getMarketProfits(product);
        }
        return "";
    }

    

}
