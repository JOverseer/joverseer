package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;

/**
 * List view for NationMessage objects
 * 
 * @author Marios Skounakis
 */
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

	@Override
	protected AbstractListViewFilter getTextFilter(String txt) {
		if (txt == null || txt.equals("")) return super.getTextFilter(txt);
		return new TextFilter("Message", "message", txt);
	}

	@Override
	protected boolean hasTextFilter() {
		return true;
	}


}
