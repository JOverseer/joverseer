package org.joverseer.ui.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterInfoCollector;

/**
 * Wraps information about Enemy Character Rumors. Also contains a static method
 * for generating the character rumors for the game.
 * 
 * A character rumor contains: - the character name - the hex number, if
 * applicable - the turn number of the latest rumor - a string of a list of all
 * rumors from all turns - whether the character is a starting char or not - the
 * character type (agent or emmisary)
 * 
 * @author Marios Skounakis
 */
public class EnemyCharacterRumorWrapper implements IHasMapLocation, IBelongsToNation {

	String name;
	int hexNo;
	int turnNo;
	String reportedTurns = "";
	boolean startChar;
	String charType;
	int lastTurnNo;
	int nationNo;
	int actionCount;
	String inactiveReason;

	public String getInactiveReason() {
		return this.inactiveReason;
	}

	public void setInactiveReason(String inactiveReason) {
		this.inactiveReason = inactiveReason;
	}

	public int getActionCount() {
		return this.actionCount;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public int getLastTurnNo() {
		return this.lastTurnNo;
	}

	public void setLastTurnNo(int lastTurnNo) {
		this.lastTurnNo = lastTurnNo;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReportedTurns() {
		return this.reportedTurns;
	}

	public void setReportedTurns(String reportedTurns) {
		this.reportedTurns = reportedTurns;
	}

	public int getTurnNo() {
		return this.turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	@Override
	public int getX() {
		return this.hexNo / 100;
	}

	@Override
	public int getY() {
		return this.hexNo % 100;
	}

	public void addReport(int turnNo1, String rep) {
		this.reportedTurns += (this.reportedTurns.equals("") ? "" : ", ") + rep;
		if (this.lastTurnNo < turnNo1)
			this.lastTurnNo = turnNo1;
		this.actionCount++;
	}

	public boolean getStartChar() {
		return this.startChar;
	}

	public void setStartChar(boolean startChar) {
		this.startChar = startChar;
	}

	public String getCharType() {
		return this.charType;
	}

	public void setCharType(String charType) {
		this.charType = charType;
	}

	public static Container<EnemyCharacterRumorWrapper> getAgentWrappers() {
		return getAgentWrappers(false);
	}

	public static Container<EnemyCharacterRumorWrapper> getAgentWrappers(boolean useCharacterInfoCollector) {
		Container<EnemyCharacterRumorWrapper> thieves = new Container<EnemyCharacterRumorWrapper>(new String[] { "name" });
		Game g = GameHolder.instance().getGame();
		if (Game.isInitialized(g)) {
			// rumor type - short description
			String[] types = new String[] { "theft", "theft", "assas.", "kidnap", "emiss.", "sabot." };
			// char type, according to rumor
			String[] charTypes = new String[] { "agent", "agent", "agent", "agent", "emmisary", "agent", };
			// the patterns for identifying the rumors
			String[] prefixes = new String[] { "^There are rumors of a theft attempt involving (.+) at .+", "^There are rumors of a theft attempt involving (.+)\\.$", "^There are rumors of an assassination attempt involving (.+) and .+", "^There are rumors of a kidnap attempt involving (.+) and .+", "^The loyalty was influenced from the efforts or presence of (.+) at .+", "^There are rumors of a sabotage attempt involving (.+) at .+" };
			for (int i = 0; i <= g.getMaxTurn(); i++) {
				Turn t = g.getTurn(i);

				if (t == null)
					continue;

				NationRelations gameNr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", g.getMetadata().getNationNo());

				for (NationMessage nm : t.getNationMessages().getItems()) {
					String charName = null;
					String repType = null;
					String charType = null;
					// for all patterns
					for (int j = 0; j < prefixes.length; j++) {
						String prefix = prefixes[j];
						Matcher m = Pattern.compile(prefix).matcher(nm.getMessage());
						if (m.matches()) {
							// if pattern matches nation message
							// get the info for the match (name, report type,
							// char type)
							charName = m.group(1).trim();
							repType = types[j];
							charType = charTypes[j];
							// stop matching patterns
							break;
						}
					}
					if (charName != null) {
						if (InfoUtils.isDragon(charName))
							continue;
						// if a match was found create new rumor
						EnemyCharacterRumorWrapper thief = thieves.findFirstByProperty("name", charName);
						Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", charName);
						boolean startChar = false;
						if (c == null) {
							c = g.getMetadata().getCharacters().findFirstByProperty("name", charName);
							if (c != null)
								startChar = true;
						}
						int nationNo = 0;
						if (c != null) {
							if (c.getNationNo() > 0) {
								NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c.getNationNo());
								if (nr.getAllegiance() == gameNr.getAllegiance()) {
									// if you can deduce that the char is of
									// friendly allegiance,
									// do not show him
									continue;
								}
								nationNo = c.getNationNo();
							}
						}
						if (useCharacterInfoCollector && nationNo == 0) {
							AdvancedCharacterWrapper acw = CharacterInfoCollector.instance().getCharacterForTurn(charName, t.getTurnNo());
							if (acw != null) {
								if (acw.getNationNo() != null)
									nationNo = acw.getNationNo();
							}
						}
						if (thief == null) {
							// if new character, create rumor wrapper
							thief = new EnemyCharacterRumorWrapper();
							startChar = g.getMetadata().getCharacters().findFirstByProperty("id", Character.getIdFromName(charName)) != null;
							thief.setName(charName);
							thief.setTurnNo(t.getTurnNo());
							thief.addReport(t.getTurnNo(), repType + " " + t.getTurnNo());
							thief.setStartChar(startChar);
							thief.setCharType(charType);
							thief.setNationNo(nationNo);
							thieves.addItem(thief);
						} else {
							// if rumor wrapper already exists, add the report
							// for this turn
							thief.addReport(t.getTurnNo(), repType + " " + t.getTurnNo());
							// also update nation if possible
							if (thief.getNationNo() == 0) {
								thief.setNationNo(nationNo);
							}
						}
					}
				}
			}
			if (useCharacterInfoCollector) {
				int turnNo = g.getMaxTurn();
				for (EnemyCharacterRumorWrapper w : thieves.getItems()) {
					AdvancedCharacterWrapper acw = CharacterInfoCollector.instance().getCharacterForTurn(w.getName(), turnNo);
					if (acw != null) {
						if (acw.isHostage()) {
							w.setInactiveReason("Hostage of " + acw.getHostageHolderName());
							continue;
						}
					}
					acw = CharacterInfoCollector.instance().getLatestCharacter(w.getName(), turnNo);
					if (acw != null) {
						if (acw.getDeathReason() != null && !acw.getDeathReason().equals(CharacterDeathReasonEnum.NotDead)) {
							w.setInactiveReason("Died turn " + acw.getTurnNo() + " (" + acw.getDeathReason() + ")");
						}
					}
				}
			}
		}
		return thieves;
	}

}
