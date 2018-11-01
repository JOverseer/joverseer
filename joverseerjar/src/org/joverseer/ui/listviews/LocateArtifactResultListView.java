package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.game.Turn;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.springframework.richclient.table.ColumnToSort;

/**
 * List view for LA/LAT results
 * 
 * @author Marios Skounakis
 */
public class LocateArtifactResultListView extends BaseItemListView {

	public LocateArtifactResultListView() {
		super(LocateArtifactResultTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 32, 32, 120, 32, 120, 96, 160 };
	}

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[] { new ColumnToSort(0, 0), new ColumnToSort(1, 4) };
	}

	@Override
	protected void setItems() {
		ArrayList<LocateArtifactResult> results = new ArrayList<LocateArtifactResult>();
		Turn t = this.getTurn();
		if (t == null)
			return;

		for (int ti = 0; ti <= this.game.getMaxTurn(); ti++) {
			if (this.game.getTurn(ti) == null)
				continue;
			for (Artifact arti : this.game.getTurn(ti).getArtifacts()) {
				if (DerivedFromSpellInfoSource.class.isInstance(arti.getInfoSource())) {
					for (LocateArtifactResult lar : ((LocateArtifactResultTableModel) this.tableModel).getResults(arti)) {
						lar.setTurnNo(ti);
						results.add(lar);
					}

				}
			}
		}
		ArrayList<LocateArtifactResult> items = new ArrayList<LocateArtifactResult>();
		AbstractListViewFilter filter = getActiveFilter();
		for (LocateArtifactResult lar : results) {
			if (filter == null || filter.accept(lar)) {
				items.add(lar);
			}
		}
		this.tableModel.setRows(items);
		this.tableModel.fireTableDataChanged();
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][] { TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(), };
	}

}
