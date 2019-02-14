package org.joverseer.support.info;

import java.util.ArrayList;

import org.joverseer.JOApplication;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.AsciiUtils;
import org.joverseer.tools.combatCalc.TacticEnum;

/**
 * Various information utils. Basically wraps the Info objects via functions so
 * that game information is made accessible in a more friendly way to the code.
 * 
 * Contains static functions only.
 * 
 * TODO: This is a bad design. It should be refactored somehow but it is not
 * very easy.
 * 
 * @author Marios Skounakis
 * 
 */
public class InfoUtils {

	public static Boolean isDragon(String charName) {
		Info info = JOApplication.getInfoRegistry().getInfo("dragons");
		if (info == null)
			return null;
		if (info.getRowIdx(charName.toUpperCase()) > -1)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

	public static ArrayList<String> getAllCharacterTitles() {
		ArrayList<String> ret = new ArrayList<String>();
		Info info = JOApplication.getInfoRegistry().getInfo("characterTitles");
		if (info == null)
			return null;
		for (int i = 1; i < info.getColumnHeaders().size(); i++) {
			for (int j = 1; j < info.getRowHeaders().size(); j++) {
				ret.add(info.getValue(j, i));
			}
		}
		return ret;
	}

	public static String getCharacterStatsFromTitle(String title) {
		Info info = JOApplication.getInfoRegistry().getInfo("characterTitles");
		if (info == null)
			return null;
		for (int i = 1; i < info.getColumnHeaders().size(); i++) {
			for (int j = 1; j < info.getRowHeaders().size(); j++) {
				if (info.getValue(j, i).equals(title)) {
					return info.getValue(j, 0);
				}
			}
		}
		return null;
	}

	public static String getCharacterStatsTypeFromTitle(String title) {
		Info info = JOApplication.getInfoRegistry().getInfo("characterTitles");
		if (info == null)
			return null;
		for (int i = 1; i < info.getColumnHeaders().size(); i++) {
			for (int j = 1; j < info.getRowHeaders().size(); j++) {
				if (info.getValue(j, i).equals(title)) {
					return info.getValue(0, i);
				}
			}
		}
		return null;
	}

	public static String getHealthRangeFromWounds(String woundsDescription) {
		Info info = JOApplication.getInfoRegistry().getInfo("characterWounds");
		if (info == null)
			return null;
		int i = info.getRowIdx(woundsDescription);
		return info.getValue(i, 1);
	}

	public static ArmyElementType getElementTypeFromDescription(String description) {
		Info info = JOApplication.getInfoRegistry().getInfo("troopTypeDescriptions");
		if (info == null)
			return null;
		description = AsciiUtils.convertNonAscii(description);
		for (int j = 1; j < info.getRowHeaders().size(); j++) {
			if (info.getValue(j, 3).equals(description)) {
				String t = info.getValue(j, 2);
				if (t.equals("1")) {
					return ArmyElementType.HeavyCavalry;
				} else if (t.equals("2")) {
					return ArmyElementType.LightCavalry;
				} else if (t.equals("3")) {
					return ArmyElementType.HeavyInfantry;
				} else if (t.equals("4")) {
					return ArmyElementType.LightInfantry;
				} else if (t.equals("5")) {
					return ArmyElementType.Archers;
				} else if (t.equals("6")) {
					return ArmyElementType.MenAtArms;
				}
				
				return null;
			}
		}
		return null;
	}

	public static String getArmyWareTypeRange(String description) {
		Info info = JOApplication.getInfoRegistry().getInfo("armyWareTypes");
		if (info == null)
			return null;
		for (int j = 1; j < info.getRowHeaders().size(); j++) {
			if (info.getValue(j, 0).equals(description)) {
				String t = info.getValue(j, 1);
				return t;
			}
		}
		return null;
	}

	public static String getArmyTrainingRange(String description) {
		Info info = JOApplication.getInfoRegistry().getInfo("armyTrainingDescriptions");
		if (info == null)
			return null;
		for (int j = 1; j < info.getRowHeaders().size(); j++) {
			if (info.getValue(j, 0).toString().toLowerCase().equals(description.toLowerCase())) {
				String t = info.getValue(j, 1);
				return t;
			}
		}
		return null;
	}

	public static String getArmyLossesRange(String description) {
		Info info = JOApplication.getInfoRegistry().getInfo("armyLossesDescriptions");
		if (info == null)
			return null;
		for (int j = 1; j < info.getRowHeaders().size(); j++) {
			if (info.getValue(j, 0).toString().toLowerCase().equals(description.toLowerCase())) {
				String t = info.getValue(j, 1);
				return t;
			}
		}
		return null;
	}

	public static String getArmyMoraleRange(String description) {
		if (description == null)
			return null;
		Info info = JOApplication.getInfoRegistry().getInfo("armyMoraleDescriptions");
		if (info == null)
			return null;
		for (int j = 1; j < info.getRowHeaders().size(); j++) {
			if (description.toLowerCase().indexOf(info.getValue(j, 0).toString().toLowerCase()) > -1) {
				String t = info.getValue(j, 1);
				return t;
			}
		}
		return null;
	}

	public static String getValueFromGrid(String columnHeader, String rowHeader, String key) {
		Info info = JOApplication.getInfoRegistry().getInfo(key);
		if (info == null)
			return null;
		for (int j = 0; j < info.getRowHeaders().size(); j++) {
			if (info.getValue(j, 0).toLowerCase().equals(columnHeader.toLowerCase())) {
				for (int i = 0; i < info.getColumnHeaders().size(); i++) {
					if (info.getValue(0, i).toLowerCase().equals(rowHeader.toLowerCase())) {
						return info.getValue(j, i);
					}
				}
			}
		}
		return null;
	}

	public static Integer getTroopTerrainModifier(ArmyElementType type, HexTerrainEnum terrain) {
		Object obj = getValueFromGrid(type.getType(), terrain.toString(), "combat.troopTerrainModifiers");
		if (obj == null)
			return null;
		return Integer.parseInt(obj.toString());
	}

	public static Integer getTroopTacticModifier(ArmyElementType type, TacticEnum tactic) {
		Object obj = getValueFromGrid(type.getType(), tactic.toString(), "combat.troopTacticModifiers");
		if (obj == null)
			return null;
		return Integer.parseInt(obj.toString());
	}

	public static Integer getTacticVsTacticModifier(TacticEnum tactic1, TacticEnum tactic2) {
		Object obj = getValueFromGrid(tactic1.toString(), tactic2.toString(), "combat.tacticVsTacticModifiers");
		if (obj == null)
			return null;
		return Integer.parseInt(obj.toString());
	}

	public static Integer getTroopStrength(ArmyElementType type, String strengthType) {
		Object obj = getValueFromGrid(type.getType(), strengthType, "combat.troopsStrengths");
		if (obj == null)
			return null;
		return Integer.parseInt(obj.toString());
	}

	// not used?
	public static String getCharacterTitle(String type, int rank) {
		if (rank == 0)
			return null;
		if (rank < 10)
			return "Unranked";
		Info info = JOApplication.getInfoRegistry().getInfo("characterTitles");
		if (info == null)
			return null;
		int ci = info.getColumnIdx(type);
		String rankTitle = "100+";
		if (rank >= 100) {
			rankTitle = "100+";
		} else {
			int rankBase = (rank / 10) * 10;
			rankTitle = String.valueOf(rankBase) + "-" + String.valueOf(rankBase + 9);
		}
		int ri = info.getRowIdx(rankTitle);
		return info.getValue(ri, ci);
	}

	public static int getClimateModifier(ProductEnum pr, ClimateEnum cl) {
		Info info = JOApplication.getInfoRegistry().getInfo("climateProduction");
		int ri = info.getRowIdx(String.valueOf(cl));
		int ci = info.getColumnIdx(String.valueOf(pr));
		if (ri == -1 || ci == -1)
			return 100;
		return Integer.parseInt(info.getValue(ri, ci));
	}

	public static int getCharactersAllowed(GameTypeEnum gt,int turn) {
		Info info = JOApplication.getInfoRegistry().getInfo("charactersAllowed");
		if (info == null)
			return Integer.MAX_VALUE;
		String range;
		int upper,lower,separator;
		for (int j = 1; j < info.getRowCount(); j++) {
			if (info.getValue(j, 0).equalsIgnoreCase(gt.toString())) {
				range = info.getValue(j, 1);
				if (range.contains("+")) {
					return Integer.MAX_VALUE;
				}
				separator = range.indexOf("-");
				if (separator==0) {
					upper = Integer.valueOf(range.substring(separator+1));
					lower = 0;
				} else if (separator >0) {
					lower = Integer.valueOf(range.substring(0, separator));
					upper = Integer.valueOf(range.substring(separator+1));
				} else {
					lower = Integer.valueOf(range); 
					upper = lower;
				}
				if (lower == 1) {
					lower =0;
				}
				if ((turn >= lower) && (turn <=upper)) {
					return Integer.valueOf(info.getValue(j, 2));
				}
			}
		}
		return Integer.MAX_VALUE;
	}
}
