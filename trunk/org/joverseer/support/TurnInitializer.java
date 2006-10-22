package org.joverseer.support;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.HexInfo;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
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
        if (previousTurn != null) {
            // copy pcs
            Container oldPcs = previousTurn.getContainer(TurnElementsEnum.PopulationCenter);
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)oldPcs.items) {
                PopulationCenter newPc = pc.clone();
                newPcs.addItem(newPc);
            }
        }
        newTurn.getContainers().put(TurnElementsEnum.Character, new Container());
        newTurn.getContainers().put(TurnElementsEnum.Army, new Container());
        newTurn.getContainers().put(TurnElementsEnum.NationEconomy, new Container());
        Container hexInfo = new Container();
        GameMetadata gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
        for (Hex h : (Collection <Hex>)gm.getHexes()) {
            HexInfo hi = new HexInfo();
            hi.setVisible(false);
            hi.setX(h.getColumn());
            hi.setY(h.getRow());
            hexInfo.addItem(hi);
        }
        newTurn.getContainers().put(TurnElementsEnum.HexInfo, hexInfo);
    }
}
