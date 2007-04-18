package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;


public class NationMessageListView extends ItemListView {
    public NationMessageListView() {
        super(TurnElementsEnum.NationMessage, NationMessageTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 400};
    }
    
    protected AbstractListViewFilter[][] getFilters() {
    	return new AbstractListViewFilter[][]{NationFilter.createNationFilters()};
    }


}
