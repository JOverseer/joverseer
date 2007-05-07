package org.joverseer.tools.combatCalc;

import java.util.ArrayList;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.info.InfoUtils;

public class Combat {
	HexTerrainEnum terrain;
	
	ArrayList<CombatArmy> side1 = new ArrayList<CombatArmy>();
	ArrayList<CombatArmy> side2 = new ArrayList<CombatArmy>();
	
	public static int computeNativeArmyStrength(CombatArmy ca, HexTerrainEnum terrain) {
		int strength = 0;
		for (ArmyElement ae : ca.getElements()) {
			Integer s = InfoUtils.getTroopStrength(ae.getArmyElementType(), "Attack");
			if (s == null) continue;
			int tacticMod = InfoUtils.getTroopTacticModifier(ae.getArmyElementType(), ca.getTactic());
			int terrainMod = InfoUtils.getTroopTerrainModifier(ae.getArmyElementType(), terrain);
			double mod = (double)(ae.getTraining() + 
						ae.getWeapons() +
						tacticMod +
						terrainMod) / 400d;
			strength += s * ae.getNumber() * mod;
		}
		strength = strength * (ca.getCommandRank() + ca.getMorale() + 200) / 400;
		return strength;
	}
	
	public static int computNativeArmyConstitution(CombatArmy ca) {
		int constit = 0;
		for (ArmyElement ae : ca.getElements()) {
			Integer s = InfoUtils.getTroopStrength(ae.getArmyElementType(), "Defense");
			if (s == null) continue;
			constit += s * ae.getNumber() * (double)(100 + ae.getArmor()) / 100d;
		}
		return constit;
	}
	
	public static int computeModifiedArmyStrength(HexTerrainEnum terrain, CombatArmy ca1, CombatArmy ca2) {
		int s = computeNativeArmyStrength(ca1, terrain);
		int tacticVsTacticMod = InfoUtils.getTacticVsTacticModifier(ca1.getTactic(), ca2.getTactic());
		s = (int)(s * (double)tacticVsTacticMod / 100d);
		return s;
	}
	
	public static int computeLosses(CombatArmy ca, int enemyStrength) {
		int constit = computNativeArmyConstitution(ca);
		return enemyStrength/constit;
	}
	
	
}
