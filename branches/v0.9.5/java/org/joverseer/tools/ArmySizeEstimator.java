package org.joverseer.tools;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.ArmySizeEstimate;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;


public class ArmySizeEstimator {
    public ArrayList<ArmySizeEstimate> estimateArmySizes() {
        ArrayList<ArmySizeEstimate> ret = new ArrayList<ArmySizeEstimate>();
        for (ArmySizeEnum size : ArmySizeEnum.values()) {
            if (size != ArmySizeEnum.unknown && size != ArmySizeEnum.tiny) {
                ret.add(new ArmySizeEstimate(ArmySizeEstimate.ARMY_TYPE, size));
            }
        }
        for (ArmySizeEnum size : ArmySizeEnum.values()) {
            if (size != ArmySizeEnum.unknown && size != ArmySizeEnum.tiny) {
                ret.add(new ArmySizeEstimate(ArmySizeEstimate.NAVY_TYPE, size));
            }
        }
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return ret;
        Turn t = g.getTurn();
        if (t == null) return ret;
        Container armies = t.getContainer(TurnElementsEnum.Army);
        for (Army a : (ArrayList<Army>)armies.getItems()) {
            for (ArmySizeEstimate ae : ret) {
                ae.addArmy(a);
            }
        }
        return ret;
    }
}
