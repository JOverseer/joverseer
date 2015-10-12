package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JTable;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;
import org.joverseer.ui.domain.ProductLineWrapper;
import org.joverseer.ui.support.Messages;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.ColumnToSort;

/**
 * List view for Nation Production objects
 * 
 * @author Marios Skounakis
 */
public class NationProductionListView extends BaseItemListView {

	public enum ProductionTypesEnum {
		Total(0),Production(1),Stores(2),SellPrice(3),BuyPrice(4),TotalProduction(5);
		int identity;
		private ProductionTypesEnum(int identity) {
			this.identity = identity;
		}
	}
	public class IdentifiedProductLineWrapper extends ProductLineWrapper
	{
		ProductionTypesEnum identity;
		public IdentifiedProductLineWrapper(ProductionTypesEnum identity)
		{
			this.identity = identity;
		}
		public IdentifiedProductLineWrapper(ProductionTypesEnum identity,ProductContainer pc)
		{
			super(pc);
			this.identity=identity;
		}
		public ProductionTypesEnum getIdentity() { return identity;}
	}
	public NationProductionListView() {
		super(NationProductionTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 0, 60, 60, 48, 48, 48, 48, 48, 48, 48 };
	}

	@Override
	protected void setItems() {
		ArrayList<IdentifiedProductLineWrapper> items = new ArrayList<IdentifiedProductLineWrapper>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (!Game.isInitialized(g))
			return;
		ProductContainer totalProdContainer = new ProductContainer();
		int counter = 1;
		for (NationEconomy ne : g.getTurn().getNationEconomies()) {
			IdentifiedProductLineWrapper prod = new IdentifiedProductLineWrapper(ProductionTypesEnum.Production,ne.getProduction());
			prod.setNationNo(ne.getNationNo());
			prod.setDescr(Messages.getString("NationProductionListView.Production"));
			prod.setIdx(counter);
			counter++;
			items.add(prod);
			IdentifiedProductLineWrapper stores = new IdentifiedProductLineWrapper(ProductionTypesEnum.Stores,ne.getStores());
			stores.setNationNo(ne.getNationNo());
			stores.setDescr(Messages.getString("NationProductionListView.Stores"));
			stores.setIdx(counter);
			counter++;
			items.add(stores);

			ProductContainer nationTotals = new ProductContainer();
			nationTotals.add(ne.getProduction());
			nationTotals.add(ne.getStores());
			IdentifiedProductLineWrapper total = new IdentifiedProductLineWrapper(ProductionTypesEnum.Total,nationTotals);
			total.setNationNo(ne.getNationNo());
			total.setIdx(counter);
			counter++;
			total.setDescr(Messages.getString("NationProductionListView.Total"));
			items.add(total);

			totalProdContainer.add(ne.getProduction());
		}
		IdentifiedProductLineWrapper totalProduction = new IdentifiedProductLineWrapper(ProductionTypesEnum.TotalProduction,totalProdContainer);
		totalProduction.setDescr(Messages.getString("NationProductionListView.TotalProduction"));
		Container<ProductPrice> prices = g.getTurn().getProductPrices();
		IdentifiedProductLineWrapper sellPrices = new IdentifiedProductLineWrapper(ProductionTypesEnum.SellPrice);
		IdentifiedProductLineWrapper buyPrices = new IdentifiedProductLineWrapper(ProductionTypesEnum.BuyPrice);

		sellPrices.setDescr(Messages.getString("NationProductionListView.SellPrice"));
		sellPrices.setIdx(counter);
		counter++;

		buyPrices.setDescr(Messages.getString("NationProductionListView.BuyPrice"));
		buyPrices.setIdx(counter);
		counter++;

		totalProduction.setIdx(counter);

		ProductPrice pp;
		for (ProductEnum product:ProductEnum.values())
		{
			if (product ==  ProductEnum.Gold) continue;

			pp = prices.findFirstByProperty("product", product);
			if (pp != null) {
				sellPrices.setProduct(product, pp.getSellPrice());
				buyPrices.setProduct(product, pp.getBuyPrice());
			}
		}

		items.add(sellPrices);
		items.add(buyPrices);
		items.add(totalProduction);

		ArrayList<IdentifiedProductLineWrapper> filteredItems = new ArrayList<IdentifiedProductLineWrapper>();
		for (IdentifiedProductLineWrapper plw : items) {
			if (getActiveFilter() == null || getActiveFilter().accept(plw)) {
				filteredItems.add(plw);
			}
		}
		this.tableModel.setRows(filteredItems);
		this.tableModel.fireTableDataChanged();
	}

	@Override
	protected JTable createTable() {
		JTable tbl = super.createTable();
		tbl.getColumnModel().getColumn(0).setWidth(0);
		return tbl;
	}

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[] { new ColumnToSort(0, 0) };
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][] {
				new AbstractListViewFilter[] { new ProductionFilter(Messages.getString("NationProductionListView.All"), null), 
				new ProductionFilter(Messages.getString("NationProductionListView.Production"), ProductionTypesEnum.Production), 
				new ProductionFilter(Messages.getString("NationProductionListView.Stores"), ProductionTypesEnum.Stores),
				new ProductionFilter(Messages.getString("NationProductionListView.Total"), ProductionTypesEnum.Total), } };
	}

	class ProductionFilter extends AbstractListViewFilter {
		ProductionTypesEnum type;

		public ProductionFilter(String description, ProductionTypesEnum type) {
			super(description);
			this.type = type;
		}

		@Override
		public boolean accept(Object obj) {
			IdentifiedProductLineWrapper plw = (IdentifiedProductLineWrapper) obj;
			return this.type == null
					|| plw.getIdentity() == ProductionTypesEnum.TotalProduction
					|| plw.getIdentity() == ProductionTypesEnum.SellPrice
					|| plw.getIdentity() == ProductionTypesEnum.BuyPrice
					|| plw.getIdentity() == this.type;
		}

	}
}
