package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.PopCenterInfoSourceTableCellRenderer;
import org.springframework.richclient.table.ColumnToSort;


public class PopulationCenterListView extends ItemListView {
    public PopulationCenterListView() {
        super(TurnElementsEnum.PopulationCenter, PopulationCenterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 96,
                        64, 64, 64, 40, 96};
    }
    
    
    
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		table.setDefaultRenderer(InfoSource.class, new PopCenterInfoSourceTableCellRenderer(tableModel));
		return c;
	}

	protected AbstractListViewFilter[][] getFilters() {
        ArrayList filters = new ArrayList();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][]{(AbstractListViewFilter[])filters.toArray(new AbstractListViewFilter[]{})};
    }
    
    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[] {new ColumnToSort(0, 2), new ColumnToSort(0, 1)};
    }

}
