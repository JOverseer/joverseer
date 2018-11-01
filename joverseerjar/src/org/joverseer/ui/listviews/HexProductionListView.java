package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JComponent;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.joverseer.ui.support.controls.TableUtils;

public class HexProductionListView extends ItemListView {
	ArrayList<HexProductionWrapper> averages = new ArrayList<HexProductionWrapper>();

	public HexProductionListView() {
		super(TurnElementsEnum.PopulationCenter, HexProductionTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64 };
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.add(new AbstractListViewFilter("Terrain average") {
			@Override
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper) obj;
				return hpw.getHexNo() == null && hpw.getClimate() == null;
			}

		});
		filters1.add(new AbstractListViewFilter("Terrain/climate average") {
			@Override
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper) obj;
				return hpw.getHexNo() == null && hpw.getClimate() != null;
			}

		});
		filters1.add(new AbstractListViewFilter("Individual hexes") {
			@Override
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper) obj;
				return hpw.getHexNo() != null && hpw.getHexNo() > 0;
			}

		});
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}) };
	}

	@Override
	protected void setItems() {
		this.averages.clear();
		ArrayList<HexProductionWrapper> items = new ArrayList<HexProductionWrapper>();
		Turn t = this.getTurn();
		if (t != null) {
			for (PopulationCenter pc : t.getPopulationCenters()) {
				boolean hasProduction = false;
				for (ProductEnum p : ProductEnum.values()) {
					if (pc.getProduction(p) != null && pc.getProduction(p) > 0) {
						hasProduction = true;
					}
				}
				if (hasProduction) {
					HexProductionWrapper pw = new HexProductionWrapper(pc, this.game, t);
					if (getActiveFilter() != null && getActiveFilter().accept(pw)) {
						items.add(pw);
					}
					addToAverage(pw);
				}
			}
		}
		for (HexProductionWrapper av : this.averages) {
			av.divideByCount();
			if (getActiveFilter() != null && getActiveFilter().accept(av)) {
				items.add(av);
			}
		}
		this.tableModel.setRows(items);

	}

	protected void addToAverage(HexProductionWrapper pw) {
		boolean foundTerrain = false;
		boolean foundClimate = false;
		for (HexProductionWrapper av : this.averages) {
			if (av.getTerrain() != null && av.getTerrain().equals(pw.getTerrain())) {
				if (av.getClimate() == null) {
					foundTerrain = true;
					av.add(pw);
				}
				if (av.getClimate() != null && av.getClimate().equals(pw.getClimate())) {
					foundClimate = true;
					av.add(pw);
				}
			}
		}
		if (!foundTerrain) {
			HexProductionWrapper av = new HexProductionWrapper();
			av.setTerrain(pw.getTerrain());
			av.add(pw);
			this.averages.add(av);
		}
		if (!foundClimate && pw.getClimate() != null) {
			HexProductionWrapper av = new HexProductionWrapper();
			av.setTerrain(pw.getTerrain());
			av.setClimate(pw.getClimate());
			av.add(pw);
			this.averages.add(av);
		}
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		TableUtils.setTableColumnRenderer(this.table, HexProductionTableModel.iHexNo, new HexNumberCellRenderer(this.tableModel));
		return c;
	}
}
