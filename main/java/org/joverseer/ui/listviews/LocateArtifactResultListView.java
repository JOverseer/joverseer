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
import org.springframework.richclient.application.Application;


public class LocateArtifactResultListView extends BaseItemListView {

    public LocateArtifactResultListView() {
        super(LocateArtifactResultTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {32, 32, 32, 32, 96, 96, 160};
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
        for (LocateArtifactResult lar : results.values()) {
            items.add(lar);
        }
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
    }
    

}
