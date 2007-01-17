package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;


public class MarketTableModel extends AbstractTableModel {

    Game game = null;
    String[] columnHeaders = new String[] {"", "le", "br", "st", "mi", "fo", "ti", "mo"};
    String[] rowHeaders = new String[] {"stores", "production", "available to sell", "profit if all were sold",
            "sell price", "units you wish to sell", "percent you wish to sell", "available on market",
            "purchase price", "units you wish to buy", "cost/profit"};

    int[] columnWidths = new int[] {170, 64, 64, 64, 64, 64, 64, 64};

    ProductContainer sellUnits = new ProductContainer();
    ProductContainer sellPct = new ProductContainer();
    ProductContainer buyUnits = new ProductContainer();

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String productCode = columnHeaders[columnIndex];
        ProductEnum product = ProductEnum.getFromCode(productCode);
        if (rowIndex == 5) {
            sellUnits.setProduct(product, (Integer) aValue);
            fireTableDataChanged();
        }
        if (rowIndex == 6) {
            sellPct.setProduct(product, (Integer) aValue);
            fireTableDataChanged();
        }
        if (rowIndex == 9) {
            buyUnits.setProduct(product, (Integer) aValue);
            fireTableDataChanged();
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
        String productCode = columnHeaders[columnIndex];
        ProductEnum product = ProductEnum.getFromCode(productCode);
        NationEconomy ne = getNationEconomy();
        if (rowIndex == 0) {
            return ne.getStores(product);
        }
        if (rowIndex == 1) {
            return ne.getProduction(product);
        }
        if (rowIndex == 2) {
            return ne.getStores(product) + ne.getProduction(product);
        }
        if (rowIndex == 3) {
            // TODO include sell bonus
            return (ne.getStores(product) + ne.getProduction(product)) * getProductPrice(product).getSellPrice();
        }
        if (rowIndex == 4) {
            return getProductPrice(product).getSellPrice();
        }
        if (rowIndex == 5) {
            return sellUnits.getProduct(product);
        }
        if (rowIndex == 6) {
            return sellPct.getProduct(product);
        }
        if (rowIndex == 8) {
            return getProductPrice(product).getBuyPrice();
        }
        if (rowIndex == 9) {
            return buyUnits.getProduct(product);
        }
        if (rowIndex == 10) {
            ProductPrice pp = getProductPrice(product);
            int gain1 = getProductAmount(sellUnits, product) * pp.getSellPrice();
            int gain2 = getProductAmount(sellPct, product) * pp.getSellPrice()
                    * (ne.getStores(product) + ne.getProduction(product)) / 100;
            int loss = getProductAmount(buyUnits, product) * pp.getBuyPrice();
            return gain1 + gain2 - loss;
        }
        return "";
    }

    private int getProductAmount(ProductContainer pc, ProductEnum p) {
        return (pc.getProduct(p) == null ? 0 : pc.getProduct(p));
    }

    private ProductPrice getProductPrice(ProductEnum p) {
        Turn t = game.getTurn();
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        return (ProductPrice) pps.findFirstByProperty("product", p);
    }

    private Integer getSelectedNationNo() {
        return 7;
    }

    private NationEconomy getNationEconomy() {
        Turn t = game.getTurn();
        Container nes = t.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy ne = (NationEconomy) nes.findFirstByProperty("nationNo", getSelectedNationNo());
        return ne;
    }

    private Game getGame() {
        if (game == null) {
            game = GameHolder.instance().getGame();
        }
        return game;
    }


    

}
