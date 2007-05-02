package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.joverseer.domain.Artifact;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.ColumnToSort;


public class LocateArtifactResultListView extends BaseItemListView {

    public LocateArtifactResultListView() {
        super(LocateArtifactResultTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {32, 32, 120, 32, 120, 96, 160};
    }


    @Override
    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{
                new ColumnToSort(0, 0),
                new ColumnToSort(1, 4)
        };
    }

    protected void setItems() {
        HashMap<Integer, LocateArtifactResult> results = new HashMap<Integer, LocateArtifactResult>();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;

        for (int ti = 0; ti <= g.getMaxTurn(); ti++) {
            if (g.getTurn(ti) == null) continue;
            Container artis = g.getTurn(ti).getContainer(TurnElementsEnum.Artifact);
            for (Artifact arti : (ArrayList<Artifact>)artis.getItems()) {
                if (results.containsKey(arti.getNumber())) {
                    results.remove(arti.getNumber());
                }
                LocateArtifactResult lar =
                    ((LocateArtifactResultTableModel)tableModel).getResult(arti);
                lar.setTurnNo(ti);
                results.put(arti.getNumber(), lar);
            }
        }
        ArrayList items = new ArrayList();
        AbstractListViewFilter filter = getActiveFilter();
        for (LocateArtifactResult lar : results.values()) {
            if (filter == null || filter.accept(lar)) {
                items.add(lar);
            }
        }
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
    }

    protected AbstractListViewFilter[][] getFilters() {
    	return new AbstractListViewFilter[][]{
    			TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(),
    			};
    }
    
    

}
