package org.joverseer.ui.listviews;

import org.joverseer.ui.listviews.filters.SpellListFilter;
import org.springframework.richclient.table.ColumnToSort;

/**
 * List view for SpellInfo objects
 * 
 * @author Marios Skounakis
 */
public class SpellInfoListView extends ItemListView {
    public SpellInfoListView() {
        super("spells", SpellInfoTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[]{96, 32, 120, 32, 32, 96, 96, 150};
    }

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[]{new ColumnToSort(0, 0), new ColumnToSort(1, 1)};
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][]{SpellListFilter.createNationFilters()};
	}
    
    
}
