package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JTable;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;
import org.joverseer.ui.domain.ProductLineWrapper;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.ColumnToSort;


public class NationProductionListView extends BaseItemListView {

    public NationProductionListView() {
        super(NationProductionTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {0, 60, 60, 48, 48, 48, 48, 48, 48, 48};
    }

    protected void setItems() {
        ArrayList items = new ArrayList();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        Container nes = g.getTurn().getContainer(TurnElementsEnum.NationEconomy);
        ProductContainer totalProdContainer = new ProductContainer();
        int counter = 1;
        for (NationEconomy ne : (ArrayList<NationEconomy>) nes.getItems()) {
            ProductLineWrapper prod = new ProductLineWrapper(ne.getProduction());
            prod.setNationNo(ne.getNationNo());
            prod.setDescr("Production");
            prod.setIdx(counter);
            counter++;
            items.add(prod);
            ProductLineWrapper stores = new ProductLineWrapper(ne.getStores());
            stores.setNationNo(ne.getNationNo());
            stores.setDescr("Stores");
            stores.setIdx(counter);
            counter++;
            items.add(stores);

            ProductContainer nationTotals = new ProductContainer();
            nationTotals.add(ne.getProduction());
            nationTotals.add(ne.getStores());
            ProductLineWrapper total = new ProductLineWrapper(nationTotals);
            total.setNationNo(ne.getNationNo());
            total.setIdx(counter);
            counter++;
            total.setDescr("Total");
            items.add(total);
            
            totalProdContainer.add(ne.getProduction());
        }
        ProductLineWrapper totalProduction = new ProductLineWrapper(totalProdContainer);
        totalProduction.setDescr("Total Production");
        Container prices = g.getTurn().getContainer(TurnElementsEnum.ProductPrice);
        ProductLineWrapper sellPrices = new ProductLineWrapper();
        ProductLineWrapper buyPrices = new ProductLineWrapper();
        
        sellPrices.setDescr("Sell Price");
        sellPrices.setIdx(counter);
        counter++;

        buyPrices.setDescr("Buy Price");
        buyPrices.setIdx(counter);
        counter++;
        
        totalProduction.setIdx(counter);

        ProductPrice pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Food);
        if (pp != null) {
            sellPrices.setFood(pp.getSellPrice());
            buyPrices.setFood(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Food);
        if (pp != null) {
            sellPrices.setFood(pp.getSellPrice());
            buyPrices.setFood(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Leather);
        if (pp != null) {
            sellPrices.setLeather(pp.getSellPrice());
            buyPrices.setLeather(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Bronze);
        if (pp != null) {
            sellPrices.setBronze(pp.getSellPrice());
            buyPrices.setBronze(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Steel);
        if (pp != null) {
            sellPrices.setSteel(pp.getSellPrice());
            buyPrices.setSteel(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Mithril);
        if (pp != null) {
            sellPrices.setMithril(pp.getSellPrice());
            buyPrices.setMithril(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Mounts);
        if (pp != null) {
            sellPrices.setMounts(pp.getSellPrice());
            buyPrices.setMounts(pp.getBuyPrice());
        }

        pp = (ProductPrice) prices.findFirstByProperty("product", ProductEnum.Timber);
        if (pp != null) {
            sellPrices.setTimber(pp.getSellPrice());
            buyPrices.setTimber(pp.getBuyPrice());
        }

        items.add(sellPrices);
        items.add(buyPrices);
        items.add(totalProduction);
        
        ArrayList filteredItems = new ArrayList();
        for (ProductLineWrapper plw : (ArrayList<ProductLineWrapper>)items) {
            if (getActiveFilter() == null || getActiveFilter().accept(plw)) {
                filteredItems.add(plw);
            }
        }
        tableModel.setRows(filteredItems);
        tableModel.fireTableDataChanged();
    }
    
    
    
    protected JTable createTable() {
        JTable tbl = super.createTable();
        tbl.getColumnModel().getColumn(0).setWidth(0);
        return tbl;
    }

    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{new ColumnToSort(0, 0)};
    }

    protected AbstractListViewFilter[][] getFilters() {
    	return new AbstractListViewFilter[][]{
    			new AbstractListViewFilter[]{
    					new ProductionFilter("All", null),
    					new ProductionFilter("Production", "Production"),
    					new ProductionFilter("Stores", "Stores"),
    					new ProductionFilter("Total", "Total"),
    			}
        };
    }



    class ProductionFilter extends AbstractListViewFilter {
        String type;

        public ProductionFilter(String description, String type) {
            super(description);
            this.type = type;
        }
        
        public boolean accept(Object obj) {
            ProductLineWrapper plw = (ProductLineWrapper)obj;
            return type == null || 
                plw.getDescr().equals("Total Production") ||
                plw.getDescr().equals("Sell Price") ||
                plw.getDescr().equals("Buy Price") ||
                plw.getDescr().equals(type);
        }
        
    }
}
