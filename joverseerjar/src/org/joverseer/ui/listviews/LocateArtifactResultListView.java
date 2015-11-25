package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.springframework.richclient.application.Application;
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
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (!Game.isInitialized(g))
			return;

		for (int ti = 0; ti <= g.getMaxTurn(); ti++) {
			if (g.getTurn(ti) == null)
				continue;
			for (Artifact arti : g.getTurn(ti).getArtifacts()) {
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
