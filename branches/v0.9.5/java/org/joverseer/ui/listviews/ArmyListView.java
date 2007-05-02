package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;

public class ArmyListView extends ItemListView {

	public ArmyListView() {
		super(TurnElementsEnum.Army, ArmyTableModel.class);
	}

	protected int[] columnWidths() {
		return new int[]{48, 64, 120, 64, 120, 48, 48, 48, 150};
	}
	
    protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][]{NationFilter.createNationFilters()};
    }



}
