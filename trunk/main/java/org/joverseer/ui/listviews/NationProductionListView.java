package org.joverseer.ui.listviews;

import java.util.ArrayList;

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


public class NationProductionListView extends BaseItemListView {

    public NationProductionListView() {
        super(NationProductionTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {60, 60, 48, 48, 48, 48, 48, 48, 48};
    }

    protected void setItems() {
        ArrayList items = new ArrayList();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        Container nes = g.getTurn().getContainer(TurnElementsEnum.NationEconomy);
        for (NationEconomy ne : (ArrayList<NationEconomy>) nes.getItems()) {
            ProductLineWrapper prod = new ProductLineWrapper(ne.getProduction());
            prod.setNationNo(ne.getNationNo());
            prod.setDescr("Production");
            items.add(prod);
            ProductLineWrapper stores = new ProductLineWrapper(ne.getStores());
            stores.setNationNo(ne.getNationNo());
            stores.setDescr("Stores");
            items.add(stores);

            ProductContainer nationTotals = new ProductContainer();
            nationTotals.add(ne.getProduction());
            nationTotals.add(ne.getStores());
            ProductLineWrapper total = new ProductLineWrapper(nationTotals);
            total.setNationNo(ne.getNationNo());
            total.setDescr("Total");
            items.add(total);
        }
        Container prices = g.getTurn().getContainer(TurnElementsEnum.ProductPrice);
        ProductLineWrapper sellPrices = new ProductLineWrapper();
        ProductLineWrapper buyPrices = new ProductLineWrapper();
        sellPrices.setDescr("Sell Price");
        buyPrices.setDescr("Buy Price");
        
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
        
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
    }
}
