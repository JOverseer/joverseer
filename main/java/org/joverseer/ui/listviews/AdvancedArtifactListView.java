package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.ui.domain.ArtifactWrapper;
import org.springframework.richclient.application.Application;


public class AdvancedArtifactListView extends BaseItemListView {

    public AdvancedArtifactListView() {
        super(AdvancedArtifactTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96, 48, 132, 48, 120, 120, 48, 120};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;
        // initialize with metadata info
        MetadataSource ms = new MetadataSource();
        Container aws = new Container(new String[]{"number"});
        for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)g.getMetadata().getArtifacts().getItems()) {
            ArtifactWrapper aw = new ArtifactWrapper();
            aw.setNumber(ai.getNo());
            aw.setName(ai.getName());
            aw.setOwner(ai.getOwner());
            aw.setPower1(ai.getPower1());
            aw.setPower2(ai.getPower2());
            aw.setInfoSource(ms);
            aw.setTurnNo(0);
            aws.addItem(aw);
        }
        
        // loop over turns
        for (int i=0; i<=g.getCurrentTurn(); i++) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            for (Artifact a : (ArrayList<Artifact>)t.getContainer(TurnElementsEnum.Artifact).getItems()) {
                ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", a.getNumber());
                aw.setOwner(a.getOwner());
                aw.setHexNo(a.getHexNo());
                aw.setInfoSource(a.getInfoSource());
                aw.setTurnNo(t.getTurnNo());
                Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", aw.getOwner());
                if (c != null) {
                    aw.setNationNo(c.getNationNo());
                    if (c.getNationNo() == null || c.getNationNo() == 0) {
                        c = (Character)g.getMetadata().getCharacters().findFirstByProperty("id", c.getId());
                        if (c != null) {
                            aw.setNationNo(c.getNationNo());
                        }
                    }
                }
            }
            
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                for (Integer aid : c.getArtifacts()) {
                    ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", aid);
                    aw.setOwner(c.getName());
                    aw.setHexNo(c.getHexNo());
                    aw.setInfoSource(c.getInfoSource());
                    aw.setNationNo(c.getNationNo());
                    aw.setTurnNo(t.getTurnNo());
                }
            }
        }
        tableModel.setRows(aws.getItems());
    }

}
