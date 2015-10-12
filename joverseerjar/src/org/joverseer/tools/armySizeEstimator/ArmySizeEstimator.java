package org.joverseer.tools.armySizeEstimator;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;

/**
 * Class the estimates the number of men (and ships) per army (and navy) size It
 * uses the ArmySizeEstimate
 * 
 * @author Marios Skounakis
 * 
 */
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
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g == null || !Game.isInitialized(g))
			return ret;
		Turn t = g.getTurn();
		if (t == null)
			return ret;
		for (Army a : t.getArmies()) {
			for (ArmySizeEstimate ae : ret) {
				ae.addArmy(a);
			}
		}
		return ret;
	}

	public ArmySizeEstimate getSizeEstimateForArmySize(ArmySizeEnum size, String armyType) {
		ArrayList<ArmySizeEstimate> se = estimateArmySizes();
		for (ArmySizeEstimate ae : se) {
			if (ae.getType().equals(armyType) && ae.getSize() == size) {
				return ae;
			}
		}
		return null;
	}
}
