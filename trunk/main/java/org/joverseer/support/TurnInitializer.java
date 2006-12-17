package org.joverseer.support;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.springframework.richclient.application.Application;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 10:13:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TurnInitializer {
    public void initializeTurnWith(Turn newTurn, Turn previousTurn) {
        newTurn.getContainers().put(TurnElementsEnum.PopulationCenter, new Container());
        Container newPcs = newTurn.getContainer(TurnElementsEnum.PopulationCenter);

        newTurn.getContainers().put(TurnElementsEnum.NationRelation, new Container());
        Container newRelations = newTurn.getContainer(TurnElementsEnum.NationRelation);

        if (previousTurn != null) {
            // copy pcs
            Container oldPcs = previousTurn.getContainer(TurnElementsEnum.PopulationCenter);
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)oldPcs.items) {
                PopulationCenter newPc = pc.clone();
                newPcs.addItem(newPc);
            }
            Container oldRelations = previousTurn.getContainer(TurnElementsEnum.NationRelation);
            for (NationRelations nr : (ArrayList<NationRelations>)oldRelations.items) {
                NationRelations newNr = nr.clone();
                newRelations.addItem(newNr);
            }
        } else {
            // get pcs from metadata
            GameMetadata gm = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
            Container gmPCs = gm.getPopulationCenters();
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)gmPCs.items) {
                PopulationCenter newPc = pc.clone();
                newPcs.addItem(newPc);
            }

            // init relations as default
            for (int i=1; i<26; i++) {
                NationRelations nr = new NationRelations();
                nr.setNationNo(i);
                if (i < 11) {
                    nr.setAllegiance(NationAllegianceEnum.FreePeople);
                } else if (i < 21) {
                    nr.setAllegiance(NationAllegianceEnum.DarkServants);
                } else {
                    nr.setAllegiance(NationAllegianceEnum.Neutral);
                }
                for (int j=1; j<26; j++) {
                    if (i < 10 && j < 10) {
                        nr.setRelationsFor(j, NationRelationsEnum.Tolerated);
                    } else if (i > 10 && i < 21 && j > 10 && j < 21) {
                        nr.setRelationsFor(j, NationRelationsEnum.Tolerated);
                    } else if (i > 20 || j > 20) {
                        nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                    } else {
                        nr.setRelationsFor(j, NationRelationsEnum.Disliked);
                    }
                }
                newRelations.addItem(nr);
            }
        }
        newTurn.getContainers().put(TurnElementsEnum.Character, new Container(new String[]{"id", "name"}));
        newTurn.getContainers().put(TurnElementsEnum.Army, new Container());
        newTurn.getContainers().put(TurnElementsEnum.NationEconomy, new Container());
        Container hexInfo = new Container(new String[]{"hexNo"});
        GameMetadata gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
        for (Hex h : (Collection <Hex>)gm.getHexes()) {
            HexInfo hi = new HexInfo();
            hi.setVisible(false);
            hi.setX(h.getColumn());
            hi.setY(h.getRow());
            hexInfo.addItem(hi);
        }
        newTurn.getContainers().put(TurnElementsEnum.HexInfo, hexInfo);
        newTurn.getContainers().put(TurnElementsEnum.NationMessage, new Container());
    }
}
