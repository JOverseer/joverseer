package org.joverseer.support.readers.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ArmyEstimateElement;
import org.joverseer.domain.Character;
import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.NationMap;
import org.joverseer.support.StringUtils;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.DerivedFromWoundsInfoSource;



/**
 * Stores information about a combat. More specifically it stores:
 * - the involved armies (as CombatArmy objects)
 * - the hex number
 * - the narration
 * - the character wounds (hashmap keyed by char name, valued by arraylist of string wound descriptions)
 * - the army losses (hashmap keyed by commander name, valued by arraylist of string loss descriptions)
 * 
 * @author Marios Skounakis
 */
public class CombatWrapper {
    String narration;
    int hexNo;
    Container armies = new Container(); 
    HashMap<String, ArrayList<String>> characterWounds = new HashMap<String, ArrayList<String>>();
    HashMap<String, ArrayList<String>> armyLosses = new HashMap<String, ArrayList<String>>();
    String popCenterOutcome;
    String popName;
    String popSize;
    String popFort;
    String popNation;
    String popOutcomeNation;
    boolean naval = false;
    
    
    
    public boolean isNaval() {
		return naval;
	}

	public void setNaval(boolean naval) {
		this.naval = naval;
	}

	public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getNarration() {
        return narration;
    }
    
    public void setNarration(String narration) {
        this.narration = narration;
    }
    
    
    
    
    public Container getArmies() {
        return armies;
    }

    
    public void setArmies(Container armies) {
        this.armies = armies;
    }
    
    
    
    public String getPopCenterOutcome() {
		return popCenterOutcome;
	}

	public void setPopCenterOutcome(String popCenterOutcome) {
		this.popCenterOutcome = popCenterOutcome;
	}
	
	

	public String getPopName() {
		return popName;
	}

	public void setPopName(String popName) {
		this.popName = popName;
	}

	public String getPopSize() {
		return popSize;
	}

	public void setPopSize(String popSize) {
		this.popSize = popSize;
	}

	public String getPopFort() {
		return popFort;
	}

	public void setPopFort(String popFort) {
		this.popFort = popFort;
	}

	public String getPopNation() {
		return popNation;
	}

	public void setPopNation(String popNation) {
		this.popNation = popNation;
	}
	
	

	public String getPopOutcomeNation() {
		return popOutcomeNation;
	}

	public void setPopOutcomeNation(String popOutcomeNation) {
		this.popOutcomeNation = popOutcomeNation;
	}

	private void addToList(String key, String value, HashMap<String, ArrayList<String>> map) {
    	ArrayList<String> list = map.get(key);
    	if (list == null) {
    		list = new ArrayList<String>();
    		map.put(key, list);
    	}
    	list.add(value);
    }

    public void parse() {
    	parse(getNarration());
    }
    
    public void parse(String narration) {
    	System.out.println("Parsing battle at " + getHexNo());
    	// parse char results
    	String txt = narration.replace("\n", " ").replace("\r", " ");
    	while (txt.indexOf("  ") > -1) {
    		txt = txt.replace("  ", " ");
    	};
    	
    	String injured = " appeared to have survived but suffers from ";
    	int i = 0;
    	do {
    		i = txt.indexOf(injured, i);
    		if (i > -1) {
    			// found
    			int j = txt.lastIndexOf(".", i);
    			int k = txt.indexOf(" ", i + injured.length());
    			String charName = txt.substring(j+1, i).trim();
    			String wounds = txt.substring(i + injured.length(), k);
    			System.out.println(charName + " suffered " + wounds + " wounds.");
    			wounds = wounds + " wounds";
    			addToList(charName, wounds, characterWounds);
    			i = i + injured.length();
    		}
    	} while (i > -1);
    	
    	// parse army losses
    	String losses = "'s forces were victorious in the battle, but suffered ";
    	i = 0;
    	do {
    		i = txt.indexOf(losses, i);
    		if (i > -1) {
    			// found
    			int j = txt.lastIndexOf(".", i);
    			int k = txt.indexOf(" ", i + losses.length());
    			String commanderName = txt.substring(j+1, i).trim();
    			String aLosses = txt.substring(i + losses.length(), k);
    			System.out.println(commanderName + "'s had " + aLosses + " losses.");
    			addToList(commanderName, aLosses, armyLosses);

    			i = i + losses.length();
    		} 
    	} while (i > -1);
    	
    	//  parse army losses against pc
    	losses = "'s army survived the attack on the";
    	String losses1 = ", but suffered ";
    	i = 0;
    	int i1 = 0;
    	do {
    		i = txt.indexOf(losses, i);
    		i1 = txt.indexOf(losses1, i + losses.length());
    		if (i > -1 && i1 > -1) {
    			// found
    			int j = txt.lastIndexOf(".", i);
    			int k = txt.indexOf(" ", i1 + losses1.length());
    			String commanderName = txt.substring(j+1, i).trim();
    			String aLosses = txt.substring(i1 + losses1.length(), k);
    			System.out.println(commanderName + "'s had " + aLosses + " losses against the pop center.");
    			addToList(commanderName, aLosses, armyLosses);

    			i = i1 + losses1.length();
    		} else {
    			i = -1;
    			i1 = -1;
    		}
    	} while (i > -1);
    	
    	// parse destroyed armies
    	String destroyed = "'s forces were destroyed/routed in the battle.";
    	i = 0;
    	do {
    		i = txt.indexOf(destroyed, i);
    		if (i > -1) {
    			// found
    			int j = txt.lastIndexOf(".", i);
    			String commanderName = txt.substring(j+1, i).trim();
    			System.out.println(commanderName + "'s were destroyed.");
    			addToList(commanderName, "destroyed", armyLosses);
    			i = i + losses.length();
    		} 
    	} while (i > -1);
    	
    	// parse found no enemies to fight
    	String noFight = "'s forces found no enemy armies to fight.";
    	i = 0;
    	do {
    		i = txt.indexOf(noFight, i);
    		if (i > -1) {
    			// found
    			int j = txt.lastIndexOf(".", i);
    			String commanderName = txt.substring(j+1, i).trim();
    			System.out.println(commanderName + "'s found no enemies to fight.");
    			addToList(commanderName, null, armyLosses);
    			i = i + losses.length();
    		} 
    	} while (i > -1);
    }
    
    private static String getStringSegment(String string, String startString, String endString, boolean includeStart, boolean includeEnd) {
        int idx1 = string.indexOf(startString);
        if (idx1 == -1) return null;
        int idx2 = (endString != null ? string.indexOf(endString, idx1) + endString.length() : string.length());
        if (idx2 == 0) return null;
        if (!includeStart) {
            idx1 = idx1 + startString.length() + 1;
        }
        if (!includeEnd && endString != null) {
            idx2 = idx2 - endString.length();
        }
        return string.substring(idx1, idx2).trim();
    }

    public void updateGame(Game game, int turnNo, int nationNo) {
    	for (String charName : characterWounds.keySet()) {
    		Character c = (Character)game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", charName);
    		if (c == null) {
    			// do nothing
    		} else {
    			if (c.getInformationSource() == InformationSourceEnum.exhaustive || 
    					c.getInformationSource() == InformationSourceEnum.detailed) continue;
    			// update health
    			ArrayList<String> woundsDescrList = characterWounds.get(charName);
    			for (String woundsDescr : woundsDescrList) {
    				woundsDescr = woundsDescr.substring(0, 1).toUpperCase() + woundsDescr.substring(1);
    				String healthRange = InfoUtils.getHealthRangeFromWounds(woundsDescr);
	    			if (healthRange != null) {
	    				if (c.getHealth() == null || c.getHealth() == 0 && 
	    						c.getInformationSource() != InformationSourceEnum.exhaustive &&
	    						c.getInformationSource() != InformationSourceEnum.detailed) {
	    						DerivedFromWoundsInfoSource dwis = new DerivedFromWoundsInfoSource(turnNo, nationNo);
	    						dwis.setWoundsDescription(woundsDescr);
	    						c.setHealthEstimate(new InfoSourceValue(woundsDescr, dwis));
	    				}
	    				System.out.println(charName + " " + healthRange);
	    			}
    			}
    		}
    	}
    	
    	for (ArmyEstimate ae : getArmyEstimates()) {
    		ArmyEstimate eae = (ArmyEstimate)game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", ae.getCommanderName()); 
    		if (eae != null) {
    			game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).removeItem(eae);
    		}
    		game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).addItem(ae);
    	}
    	
    }
    
    public ArrayList<ArmyEstimate> getArmyEstimates() {
    	ArrayList<ArmyEstimate> ret = new ArrayList<ArmyEstimate>();
    	Game game = GameHolder.instance().getGame();
    	for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
    		try {
    			String commander = ca.getCommanderName().trim();
    			String commanderTitle = ca.getCommanderTitle();
    			String commanderName = commander;
    			String[] commanderTitles = "Veteran,Hero,Commander,Captain,Lord,Regent,Warlord,General,Marshal,Lord Marshal".split(",");
    			for (String ct : commanderTitles) {
    				if (commander.startsWith(ct + " ")) {
    					commanderTitle = ct;
    					commanderName = commander.substring(ct.length() + 1).trim();
    				}
    			}
	    		ArmyEstimate ae = new ArmyEstimate();
    			Nation n = game.getMetadata().getNationByName(ca.getNation());
    			if (n == null) {
    				Character c = (Character)game.getMetadata().getCharacters().findFirstByProperty("name", commanderName);
    				if (c != null) {
    					ae.setNationNo(c.getNationNo());
    				} else {
    					c = (Character)game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", commanderName);
    					if (c != null) ae.setNationNo(c.getNationNo());
    				}
    			} else {
    				ae.setNationNo(n == null ? null : n.getNumber());
    			}
	    		ae.setCommanderName(commanderName);
	    		ae.setCommanderTitle(commanderTitle == null ? "" : commanderTitle);
	    		ae.setHexNo(getHexNo());
	    		if (armyLosses.get(ae.getCommanderName()) != null) {
		    		for (String l : armyLosses.get(ae.getCommanderName())) {
		    			if (l == null) continue;
		    			ae.getLossesDescriptions().add(l);
		    			String lossesRange = InfoUtils.getArmyLossesRange(l);
		    			ae.getLossesRanges().add(lossesRange);
		    		}
	    		}
	    		
	    		// morale
	    		String moraleRange = InfoUtils.getArmyMoraleRange(ca.getMorale());
	    		if (moraleRange != null) {
	    			ae.setMoraleRange(moraleRange);
	    			ae.setMorale(getRangeAverage(moraleRange));
	    		} else {
	    			ae.setMoraleRange("?");
	    			ae.setMorale(30);
	    		}
	    		
	    		ret.add(ae);
	    		
	    		for (CombatArmyElement cae : (ArrayList<CombatArmyElement>)ca.regiments.getItems()) {
	    			String descr = cae.getDescription();
	    			String[] parts = descr.split("\\s{2,50}");
	    			if (parts.length == 4) {
	    				// parts[0] split into number and descr
	    				int i = parts[0].indexOf(" ");
	    				int no = Integer.parseInt(parts[0].substring(0, i).trim());
	    				String rd = parts[0].substring(i + 1).trim();
	    				ArmyElementType aet = InfoUtils.getElementTypeFromDescription(rd);
	    				if (aet == null) {
	    					System.out.println("Failed to find element type from description " + rd);
	    					continue;
	    				}
	    				String weapons = parts[1];
	    				String weaponRange = InfoUtils.getArmyWareTypeRange(weapons);
	    				String armor = parts[2];
	    				String armorRange = InfoUtils.getArmyWareTypeRange(armor);
	    				String training = parts[3];
	    				parts = training.split(" ");
	    				String trainingRange = null;
	    				for (String p : parts) {
	    					 trainingRange = InfoUtils.getArmyTrainingRange(p);
	    					 if (trainingRange != null) break;
	    				}
	    				System.out.println(no + " " + aet + " " + weapons + " " + weaponRange + " " + armor + " " + armorRange + " " + training + " " + trainingRange);
	    				
	    				ArmyEstimateElement aee = new ArmyEstimateElement();
	    				aee.setNumber(no);
	    				aee.setDescription(rd);
	    				aee.setType(aet);
	    				aee.setWeaponsDescription(weapons);
	    				aee.setWeaponsRange(weaponRange);
	    				aee.setWeapons(getRangeAverage(weaponRange));
	    				aee.setArmorDescription(armor);
	    				aee.setArmorRange(armorRange);
	    				aee.setArmor(getRangeAverage(armorRange));
	    				aee.setTrainingDescription(training);
	    				aee.setTrainingRange(trainingRange);
	    				aee.setTraining(getRangeAverage(trainingRange));
	    				
	    				ae.getRegiments().add(aee);
	    			} else {
	    				System.out.println("Error parsing regiment " + descr);
	    			}
	    		}
    		}
    		catch (Exception exc) {
    			System.out.println("Error in combat " + getHexNo());
    			exc.printStackTrace();
    		}
    	}
    	return ret;
    }
    
    protected int getRangeAverage(String rangeString, int max) {
    	if (rangeString.indexOf("-") > -1) {
	    	String[] parts = rangeString.split("-");
	    	try {
	    		return (int)Math.round(((double)Integer.parseInt(parts[0]) + (double)Integer.parseInt(parts[1])) / 2d);
	    	}
	    	catch (Exception exc) {
	    		exc.printStackTrace();
	    	}
	    	return 0;
    	}
    	if (rangeString.endsWith("+")) {
    		String[] parts = rangeString.split("+");
	    	try {
	    		return (int)Math.round(((double)Integer.parseInt(parts[0]) + (double)max) / 2d);
	    	}
	    	catch (Exception exc) {
	    		exc.printStackTrace();
	    	}
	    	return 0;
    	}
    	try {
    		return Integer.parseInt(rangeString);
    	}
    	catch (Exception exc) {
    		exc.printStackTrace();
    	}
    	return 0;
    }
    protected int getRangeAverage(String rangeString) {
    	return getRangeAverage(rangeString, 100);
    }
    
    public void parseNavalConflict(String narration) {
    	setNaval(true);
    	String army_start = "At the head of a fleet of";
    	String report_start = "On that day in history";
    	String army_end = army_start + "|" + report_start;
    	String cleanNarration = StringUtils.removeExtraspaces(StringUtils.removeAllNewline(narration));
    	ArrayList<String> armyTexts = StringUtils.getParts(cleanNarration, army_start, army_end, true, false);
    	for (String armyText : armyTexts) {
    		String commander = StringUtils.getUniquePart(armyText, "was ", " of the nation of ", false, false);
    		String nation = StringUtils.getUniquePart(armyText, "of the nation of ", "\\.", false, false);
    		String commanderTitle = StringUtils.getFirstWord(commander);
    		String commanderName = StringUtils.stripFirstWord(commander);
    		if (nation.startsWith("the ")) nation = StringUtils.stripFirstWord(nation);
    		
    		CombatArmy ca = new CombatArmy();
    		ca.setCommanderName(commanderName);
    		ca.setNation(nation);
    		
    		String warships = StringUtils.getUniqueRegexMatch(armyText, "(\\d+) warships");
    		String transports = StringUtils.getUniqueRegexMatch(armyText, "(\\d+) transports");
    		
    		if (warships != null) {
    			CombatArmyElement cae = new CombatArmyElement();
    			cae.setDescription(warships + " Warships");
    			ca.getRegiments().addItem(cae);
    		}
    		if (transports != null) {
    			CombatArmyElement cae = new CombatArmyElement();
    			cae.setDescription(transports + " Transports");
    			ca.getRegiments().addItem(cae);
    		}
    		
    		String survived = commanderName + "'s forces were victorious in the battle";
    		String destroyed = commanderName + "'s forces were destroyed/routed";
    		String commanderSurvived = commanderName + " appeared to have survived";
    		String commanderKilled = commanderName + " was killed";
    		String commanderCaptured = commanderName + " was captured";
    		if (cleanNarration.contains(survived)) {
    			String lossesSentence = StringUtils.getUniquePart(cleanNarration, survived, "\\.", true, true);
    			String losses = StringUtils.getUniquePart(lossesSentence, "but suffered ", " losses", false, false);
    			addToList(ca.getCommanderName(), losses, armyLosses);
    			ca.setSurvived(true);
    		} else if (cleanNarration.contains(destroyed)) {
    			ca.setSurvived(false);
    		}
    		if (cleanNarration.contains(commanderKilled)) {
    			ca.setCommanderOutcome("killed");
    		} else if (cleanNarration.contains(commanderCaptured)) {
    			ca.setCommanderOutcome("captured");
    		} else if (cleanNarration.contains(commanderSurvived)) {
    			ca.setCommanderOutcome("survived");
    		}
    		
    		armies.addItem(ca);
    	}
    	return;
    }
    
    public void parseAll(String narration) {
    	try {
	    	armies.clear();
	    	narration = narration.replace("…", "");
	    	if (narration.contains("naval conflict")) {
	    		parseNavalConflict(narration);
	    		return;
	    	}
	    	String army_start = "At the head of a ";
	    	String pop_start = "The Camp|The Village|The Town|The Major Town|The City";
	    	String report_start = "(Report from )|(Against the forces)|(After the battle)";
	    	String army_end = army_start + "|" + pop_start + "|" + report_start;
	    	String army_rode = " army rode ";
	    	String of_the_nation_of = " of the nation of ";
	    	String behind_him = "Behind him the forming ranks were filled with:";
	    	String battle_joined = "After the battle had joined";
	    	String after_the_battle = "After the battle\\.";
	    	ArrayList<String> armyTexts = StringUtils.getParts(narration, army_start, army_end, true, false);
	    	
	    	for (String armyText : armyTexts) {
	    		CombatArmy ca = parseArmy(armyText);
	    		if (ca != null) {
	    			armies.addItem(ca);
	    		}
	    	}
	    	
	    	String popCenter = StringUtils.getUniquePart(narration, pop_start, report_start, true, false);
	    	if (popCenter == null) {
	    		popCenter = StringUtils.getUniquePart(narration, pop_start, "After the battle\\.\\.\\.\\. ", true, false);
	    	}
	    	
	    	String outcomePart = StringUtils.getUniquePart(narration, after_the_battle, null, false, false);
	    	if (outcomePart != null) {
		    	outcomePart = StringUtils.removeAllNewline(outcomePart);
		    	outcomePart = StringUtils.removeExtraspaces(outcomePart);
		    	
		    	String popOutcome = StringUtils.getUniquePart(outcomePart, "After the attack on the population center\\.", null, true, false);
		    	String armyOutcome = outcomePart;
		    	if (popOutcome != null) {
		    		int i = outcomePart.indexOf(popOutcome);
		    		armyOutcome = outcomePart.substring(0, i);
		    	}
		    	
		    	for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
		    		String forces = ca.getCommanderName() + "'s forces";
		    		String forceOutcome = StringUtils.getUniquePart(armyOutcome, forces, "\\.", true, true);
		    		if (forceOutcome == null) {
		    			ca.setSurvived(true);
		    		} else if (forceOutcome.contains("victorious")) {
		    			String losses = StringUtils.getUniquePart(forceOutcome, "suffered", "losses", false, false);
		    			addToList(ca.getCommanderName(), losses, armyLosses);
		    			ca.setSurvived(true);
		    		} else if (forceOutcome.contains("destroyed/routed")) {
		    			ca.setSurvived(false);
		    			addToList(ca.getCommanderName(), "destroyed", armyLosses);
		    		} else if (forceOutcome.contains("found no enemy armies to fight.")) {
		    			ca.setSurvived(true);
		    			addToList(ca.getCommanderName(), null, armyLosses);
		    		}
		    		String commanderSurvived = ca.getCommanderName() + " appeared to have survived.";
		    		String commanderCaptured = ca.getCommanderName() + " was captured.";
		    		String commanderKilled = ca.getCommanderName() + " was killed.";
		    		String commanderWounded = ca.getCommanderName() + " appeared to have survived but suffers from ";
		    		if (armyOutcome == null) {
		    			ca.setCommanderName("survived");
		    		} else if (armyOutcome.contains(commanderSurvived)) {
		    			ca.setCommanderOutcome("survived");
		    		} else if (armyOutcome.contains(commanderCaptured)) {
		    			ca.setCommanderOutcome("captured");
		    		} else if (armyOutcome.contains(commanderKilled)) {
		    			ca.setCommanderOutcome("killed");
		    		} else if (armyOutcome.contains(commanderWounded)) {
		    			String wounds = StringUtils.getUniquePart(armyOutcome, commanderWounded, "wounds\\.", false, false);
		    			ca.setCommanderOutcome(wounds + " wounds");
		    		}
		    	}
		    	
		    	if (popCenter != null) 
		    	{
		    		popCenter = StringUtils.removeAllNewline(popCenter);
		    		popCenter = StringUtils.removeExtraspaces(popCenter);
		    		String popNationOriginal = StringUtils.getUniquePart(popCenter, "flying the flag of ", " is situated", false, false);
		    		popName = StringUtils.getUniquePart(popCenter, pop_start, " flying", false, false);
		    		if (popName.startsWith("of ")) popName = StringUtils.stripFirstWord(popName);
		    		popSize = StringUtils.getUniquePart(popCenter, pop_start, " of", true, false);
		    		if (popSize.startsWith("The ")) popSize = StringUtils.stripFirstWord(popSize);
		    		popFort = StringUtils.getUniquePart(popCenter, "It is fortified by a ", "\\,", false, false);
		    		if (popNationOriginal.startsWith("the ")) popNationOriginal = StringUtils.stripFirstWord(popNationOriginal);
		    		popNation = popNationOriginal;
		    		if (popOutcome == null) {
		    			setPopCenterOutcome("not affected");
		    		} else {
			        	String destroyed = "has been reduced to a Ruins";
			    		if (popOutcome.contains(destroyed)) {
			    			setPopCenterOutcome("destroyed");
			    		} else {
			    			String newNation = StringUtils.getUniquePart(popOutcome, "now flies the flag of ", "\\.", false, false);
			    			if (newNation != null) {
				    			if (newNation.startsWith("the ")) newNation = StringUtils.stripFirstWord(newNation);
				    			if (newNation.equals(popNationOriginal)) {
				    				setPopCenterOutcome("not affected");
				    			} else {
				    				setPopCenterOutcome("captured");
				    				setPopOutcomeNation(newNation);
				    			}
				    		} else {
				    			setPopCenterOutcome("not affected");
				    		}
			    		}
			    		for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
			    			String forces = ca.getCommanderName() + "'s army ";
			    			String forceOutcome = StringUtils.getUniquePart(popOutcome, forces, "\\.", true, true);
				    		if (forceOutcome == null) {
				    			ca.setSurvived(true);
				    		} else if (forceOutcome.contains("survived")) {
				    			String losses = StringUtils.getUniquePart(forceOutcome, "suffered", "losses", false, false);
				    			addToList(ca.getCommanderName(), losses, armyLosses);
				    			ca.setSurvived(true);
				    		} else if (forceOutcome.contains("destroyed")) {
				    			ca.setSurvived(false);
				    			addToList(ca.getCommanderName(), "destroyed", armyLosses);
				    		} 
			    		}
		    		}
		    	}
	    	}
    	}
    	catch (Exception e) {
    		parseAll(narration);
    	}
    }
    
    protected CombatArmy parseArmy(String text) {
    	String commanderName = StringUtils.getUniquePart(text, " army rode ", " of the nation of ", false, false).trim();
    	int i = commanderName.indexOf(" ");
    	String commanderTitle = commanderName.substring(0, i);
    	commanderName = commanderName.substring(i + 1);
    	String nation = StringUtils.getUniquePart(text, " of the nation of ", "\\.", false, false);
    	if (nation.startsWith("the ")) nation = nation.substring(4);
    	CombatArmy ca = new CombatArmy();
    	ca.setCommanderName(commanderName);
    	ca.setCommanderTitle(commanderTitle);
    	ca.setNation(nation);
    	String morale = StringUtils.getUniquePart(text, "The mount on which ", " battle lines", false, false);
    	ca.setMorale(morale);
    	for (String regiment : StringUtils.getParts(text, "  [\\d]+", "(\\n)|$", true, false)) {
    		CombatArmyElement cae = new CombatArmyElement();
    		ca.getRegiments().addItem(cae);
    		String number = StringUtils.getFirstWord(regiment);
    		regiment = StringUtils.stripFirstWord(regiment);
    		String type = StringUtils.getUniquePart(regiment, "^", " with ", false, false);
    		String weapons = StringUtils.getUniquePart(regiment, "with ", " weapons", false, false);
    		String armor = StringUtils.getUniquePart(regiment, ",", " armor", false, false);
    		String training = StringUtils.getUniquePart(regiment, " armor, ", "$", false, false);
    		cae.setDescription(number + " " + type + "     " + weapons + "     " + armor + "    " + training);
    	}
    	return ca;
    }
    
    public ArrayList<CombatArmy> getCombatArmies(NationAllegianceEnum notOfAllegiance) {
    	ArrayList<CombatArmy> ret = new ArrayList<CombatArmy>();
    	for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
    		if (notOfAllegiance == null) {
    			ret.add(ca);
    			continue;
    		}
    		Nation n = NationMap.getNationFromName(ca.getNation());
    		if (!notOfAllegiance.equals(n.getAllegiance())) {
    			ret.add(ca);
    		}
    	}
    	return ret;
    }
    
    public ArrayList<Nation> getNations(NationAllegianceEnum notOfAllegiance) {
    	ArrayList<Nation> ret = new ArrayList<Nation>();
    	for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
    		Nation n = NationMap.getNationFromName(ca.getNation());
    		if (ret.contains(n)) continue;
    		if (notOfAllegiance == null || !notOfAllegiance.equals(n.getAllegiance())) {
    			ret.add(n);
    		}
    	}
    	return ret;
    }
}
