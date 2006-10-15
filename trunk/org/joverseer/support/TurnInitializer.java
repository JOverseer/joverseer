package org.joverseer.support;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.PopulationCenter;

import java.util.ArrayList;

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
    }
}
