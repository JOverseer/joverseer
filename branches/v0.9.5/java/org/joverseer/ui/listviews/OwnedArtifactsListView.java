package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.OwnedArtifact;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.ColumnToSort;


public class OwnedArtifactsListView extends ItemListView {
    public OwnedArtifactsListView() {
        super(TurnElementsEnum.Character, OwnedArtifactsTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96, 48, 132, 48, 120, 120};
    }
    
    @Override
    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{
                new ColumnToSort(0, 2),
                new ColumnToSort(1, 3),
                new ColumnToSort(2, 0)
        };
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;
        Container items = g.getTurn().getContainer(turnElementType);
        ArrayList artis = new ArrayList();
        for (Character c : (ArrayList<Character>)items.getItems()) {
            for (Integer id : c.getArtifacts()) {
                ArtifactInfo ai = (ArtifactInfo)g.getMetadata().getArtifacts().findFirstByProperty("no", id);
                if (ai == null) continue;
                OwnedArtifact a = new OwnedArtifact();
                a.setNationNo(c.getNationNo());
                a.setName(ai.getName());
                a.setNumber(ai.getNo());
                a.setOwner(c.getName());
                a.setHexNo(c.getHexNo());
                a.setPower1(ai.getPower1());
                a.setPower2(ai.getPower2());
                artis.add(a);
            }
        }
        tableModel.setRows(artis);
    }

}
