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
import org.joverseer.support.Container;
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
    
    private void addToList(String key, String value, HashMap<String, ArrayList<String>> map) {
    	ArrayList<String> list = map.get(key);
    	if (list == null) {
    		list = new ArrayList<String>();
    		map.put(key, list);
    	}
    	list.add(value);
    }

    public void parse() {
    	System.out.println("Parsing battle at " + getHexNo());
    	// parse char results
    	String txt = getNarration().replace("\n", " ").replace("\r", " ");
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
    	
    	for (CombatArmy ca : (ArrayList<CombatArmy>)armies.getItems()) {
    		try {
    			String commander = ca.getCommanderName().trim();
    			String commanderTitle = null;
    			String commanderName = null;
    			String[] commanderTitles = "Veteran,Hero,Commander,Captain,Lord,Regent,Warlord,General,Marshal,Lord Marshal".split(",");
    			for (String ct : commanderTitles) {
    				if (commander.startsWith(ct + " ")) {
    					commanderTitle = ct;
    					commanderName = commander.substring(ct.length() + 1).trim();
    				}
    			}
	    		ArmyEstimate ae = (ArmyEstimate)game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", commanderName); 
	    		if (ae != null) {
	    			game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).removeItem(ae);
	    		}
    			ae = new ArmyEstimate();
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
	    		ae.setCommanderTitle(commanderTitle);
	    		ae.setHexNo(getHexNo());
	    		if (armyLosses.get(ae.getCommanderName()) != null) {
		    		for (String l : armyLosses.get(ae.getCommanderName())) {
		    			if (l == null) continue;
		    			ae.getLossesDescriptions().add(l);
		    			String lossesRange = InfoUtils.getArmyLossesRange(l);
		    			ae.getLossesRanges().add(lossesRange);
		    			ae.getLosses().add(getRangeAverage(lossesRange));
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
	    		
	    		game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).addItem(ae);
	    		
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
    			System.out.println("Error in combat " + getHexNo() + " turn " + turnNo + " nation " + nationNo);
    			exc.printStackTrace();
    		}
    	}
    		
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
}
