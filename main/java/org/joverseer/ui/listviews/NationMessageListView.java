package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.springframework.richclient.application.Application;

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
        return new int[]{64, 480};
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
