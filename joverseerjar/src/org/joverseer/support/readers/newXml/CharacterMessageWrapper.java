package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;
import org.joverseer.support.StringUtils;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.readers.pdf.AssassinationResultWrapper;
import org.joverseer.support.readers.pdf.ExecutionResultWrapper;
import org.joverseer.support.readers.pdf.InfluenceOtherResultWrapper;
import org.joverseer.support.readers.pdf.LocateArtifactResultWrapper;
import org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper;
import org.joverseer.support.readers.pdf.OrderResult;
import org.joverseer.support.readers.pdf.RevealCharacterResultWrapper;
import org.joverseer.support.readers.pdf.RevealCharacterTrueResultWrapper;

public class CharacterMessageWrapper {
	String charId;
	ArrayList<String> lines = new ArrayList<String>();

	public String getCharId() {
		return this.charId;
	}

	public void setCharId(String charId) {
		this.charId =  charId.trim(); //for those pesky ids less than 5 characters
	}

	public ArrayList<String> getLines() {
		return this.lines;
	}

	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public void addLine(String line) {
		this.lines.add(line);
	}

	public String getOrdersAsString() {
		String ret = "";
		for (String line : this.lines) {
			ret += (ret.equals("") ? "" : " ") + line;
		}
		return ret;
	}

	public void updateCharacter(Character c, Game game) {
		String orders = getOrdersAsString();
		c.setOrderResults(orders);
		if (getAssassinated(c)) {
			c.setDeathReason(CharacterDeathReasonEnum.Assassinated);
		} else if (getCursed(c)) {
			c.setDeathReason(CharacterDeathReasonEnum.Cursed);
		} else if (getExecuted(c)) {
			c.setDeathReason(CharacterDeathReasonEnum.Executed);
		} else if (getMissing(c)) { // 2010-02-09 this is KS specific
			c.setDeathReason(CharacterDeathReasonEnum.Missing);
		}
		if (!c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead)) {
			Integer hexNo;
			if (c.getDeathReason().equals(CharacterDeathReasonEnum.Missing)) {
				hexNo = getLastSeenLocation();
			} else {
				hexNo = getOriginalLocation();
			}
			if (hexNo != null) {
				game.getTurn().getContainer(TurnElementsEnum.Character).removeItem(c);
				c.setHexNo(hexNo);
				game.getTurn().getContainer(TurnElementsEnum.Character).addItem(c);
			}
		}
	}

	protected Integer getLastSeenLocation() {
		// 2010 02 09 - KS Specific, parse
		// "{Dervorin} has gone missing. She was last seen in the Mixed Forest at 1908. No one seems to know what has happened to her."
		for (String line : this.lines) {
			if (line.indexOf("was last seen ") > -1 && line.indexOf("was located in an unknown location") == -1) {
				String[] sentences = line.split("\\.");
				if (sentences.length > 1 && sentences[1].indexOf("was last seen") > -1) {
					String hexNo = sentences[1].substring(sentences[1].length() - 4, sentences[1].length());
					try {
						return Integer.parseInt(hexNo);
					} catch (Exception exc) {
						// do nothing
						return null;
					}
				}
			}
		}
		return null;
	}

	protected Integer getOriginalLocation() {
		for (String line : this.lines) {
			if (line.indexOf("was located") > -1 && line.indexOf("was located in an unknown location") == -1) {
				String hexNo = line.substring(line.length() - 5, line.length() - 1);
				try {
					return Integer.parseInt(hexNo);
				} catch (Exception exc) {
					// do nothing
					return null;
				}
			}
		}
		return null;
	}

	protected boolean getAssassinated(Character c) {
		for (String line : this.lines) {
			if (line.indexOf(c.getName() + " was assassinated.") > -1)
				return true;
		}
		return false;
	}

	protected boolean getMissing(Character c) {
		for (String line : this.lines) {
			if (line.indexOf(c.getName() + " has gone missing.") > -1)
				return true;
		}
		return false;
	}

	protected boolean getCursed(Character c) {
		for (String line : this.lines) {
			if (line.indexOf("was killed due to a mysterious and deadly curse.") > -1)
				return true;
			if (line.indexOf("was killed due to a mysterious and severe sickness.") > -1)
				return true;
			if (StringUtils.getUniqueRegexMatch(line, "was killed due to a mysterious and \\w+ (weakness).") != null)
				return true;
		}
		return false;
	}

	protected boolean getExecuted(Character c) {
		for (String line : this.lines) {
			if (line.indexOf(c.getName() + " was executed.") > -1)
				return true;
		}
		return false;
	}

	public ArrayList<OrderResult> getOrderResults(InfoSource infoSource,GameMetadata gm) {
		ArrayList<OrderResult> ret = new ArrayList<OrderResult>();
		for (String line : this.lines) {
			line = line.replace("\n", " ").replace("\n", " ");
			if (line.isEmpty()) {
				continue; // slight optimisation
			}
			OrderResult or = null;
			or = getAssassinationOrderResult(line);
			if (or == null)
				or = getExecutionOrderResult(line);
			if (or == null)
				or = getInfOtherOrderResult(line);
			if (or == null)
				or = getLAOrderResult(line);
			if (or == null)
				or = getOwnedLAOrderResult(line);
			if (or == null)
				or = getLATOrderResult(line);
			if (or == null)
				or = getOwnedLATOrderResult(line);
			if (or == null)
				or = getRCTOrderResult(line);
			if (or == null)
				or = getRCOrderResult(line);
			if (or == null)
				or = getReconResult(line, infoSource,gm);
			if (or == null)
				or = getScryResult(line, infoSource,gm);
			if (or == null)
				or = getPalantirResult(line, infoSource,gm);
			if (or == null)
				or = getRAResult(line, infoSource);
			if (or == null)
				or = getScoutHexResult(line, infoSource);
			if (or == null)
				or = getScoCharResult(line, infoSource);
			if (or == null)
				or = getScoutPopCenterResult(line, infoSource);
			if (or == null)
				or = getScoArmyResult(line, infoSource);
			if (or == null)
				or = getScoutAreaResult(line, infoSource,gm);
			if (or != null)
				ret.add(or);
		}
		return ret;

	}

	protected OrderResult getScoutPopCenterResult(String line, InfoSource infoSource) {
		try {
			if (line.contains("A scout of the population center was attempted. ")) {
				String size = StringUtils.getUniquePart(line, "A scout of the population center was attempted. ", " named ", false, false);
				String name = StringUtils.getUniquePart(line, size + " named ", " - ", false, false);
				boolean capital = line.contains(" - capital - ");
				String nation = StringUtils.getUniquePart(line, "owned by ", " - ", false, false);
				if (nation.startsWith("the "))
					nation = StringUtils.stripFirstWord(nation);
				String fort = StringUtils.getUniquePart(line, "fortified with ", " - ", false, false);
				String loyalty = StringUtils.getUniquePart(line, "loyalty = ", "\\.", false, false);
				if (name == null || size == null)
					return null;
				PopulationCenterSizeEnum pcSize = PopulationCenterSizeEnum.getFromLabel(size);
				PopulationCenter pc = new PopulationCenter();
				pc.setName(name);
				pc.setSize(pcSize);
				if (capital)
					pc.setCapital(capital);
				pc.setNationNo(NationMap.getNationFromName(nation).getNumber());
				if (fort != null)
					pc.setFortification(FortificationSizeEnum.getFromText(fort));
				if (loyalty != null)
					pc.setLoyalty(Integer.parseInt(loyalty));
				pc.setInfoSource(infoSource);
				pc.setInformationSource(InformationSourceEnum.detailed);
				ScoutPopResult result = new ScoutPopResult();
				result.setPopulationCenter(pc);

				String production = StringUtils.getUniquePart(line, "Production - ", "\\.", false, false);
				if (production != null) {
					String[] ps = production.split("\\-");
					for (String p : ps) {
						String productStr = StringUtils.getUniquePart(p, "^", " : ", true, false);
						String amtStr = StringUtils.getUniquePart(p, " : ", "$", false, true);
						ProductEnum pe = ProductEnum.getFromText(productStr);
						int amt = Integer.parseInt(amtStr);
						pc.setProduction(pe, amt);
					}
				}

				String stores = StringUtils.getUniquePart(line, "Stores - ", "\\.", false, false);
				if (stores != null) {
					String[] ps = stores.split("\\-");
					for (String p : ps) {
						String productStr = StringUtils.getUniquePart(p, "^", " : ", true, false);
						String amtStr = StringUtils.getUniquePart(p, " : ", "$", false, true);
						ProductEnum pe = ProductEnum.getFromText(productStr);
						int amt = Integer.parseInt(amtStr);
						pc.setStores(pe, amt);
					}
				}

				String foreignArmies = StringUtils.getUniquePart(line, "Foreign armies present:", "\\.", false, false);
				String[] armies = foreignArmies.split("\\-");
				for (String army : armies) {
					Nation n = NationMap.getNationFromName(army.trim());
					if (n != null) {
						result.addArmy(n.getName());
					}
				}
				return result;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/*He was ordered to scout the area.  Jilad of the Dunadan Rangers with about 1400 troops at 1409
	. See Map below.
	*/
	protected OrderResult getScoutAreaResult(String line,InfoSource infoSource,GameMetadata gm) {
		return getReconResult(line, infoSource, "was ordered to scout the area. ", "No armies were found", " See Map below",gm.getNations());
	}
	protected OrderResult getScoutHexResult(String line, InfoSource infoSource) {
		try {
			if (line.contains("A scout of the hex was attempted.")) {
				line = StringUtils.replaceNationNames(line);
				ScoutHexResult result = new ScoutHexResult();
				//String climate = StringUtils.getUniquePart(line, "Climate is ", "Cool|Cold|Mild|Warm|Polar|Hot", false, true);
				String foreignArmies = StringUtils.getUniquePart(line, "Foreign armies present:", "\\.", false, false);
				if (foreignArmies != null) {
					String[] fas = foreignArmies.split("\\- ");
					for (String fa : fas) {
						if (fa.length() < 5)
							continue;
						String commanderName = StringUtils.getUniquePart(fa, "^", " of ", false, false);
						String nation = StringUtils.getUniquePart(fa, " of ", "$", false, false);
						nation = StringUtils.stripFirstWordCond(nation, "the");
						Nation n = StringUtils.getFromNationCode(nation);
						Army a = new Army();
						a.setInformationSource(InformationSourceEnum.some);
						a.setInfoSource(infoSource);
						if (commanderName.equals("")) {
							a.defaultName();
						} else {
							a.setCommanderName(commanderName);
							a.setCommanderTitle("");
						}
						a.setSize(ArmySizeEnum.unknown);

						a.setNationNo(n.getNumber());
						a.setNationAllegiance(n.getAllegiance());
						result.addArmy(a);
					}
				}
				return result;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	static protected String extract2ndPower(String powers2) {
		int pos; 
		if (powers2.length() >0) {
			final String seek = "Possession of the artifact can allow casting of the spell ";
			pos = powers2.indexOf(seek);
			if (pos >-1) {
				return powers2.substring(pos+seek.length()); 
			} else {
				final String fail = "He was not able to cast the spell";
				pos = powers2.indexOf(fail);
				if (pos < 0) {
					return powers2;
				}
			}
		}
		//TODO: I18M
		return "Unknown";		
	}
	
	// increase combat damage by nnnn pts
	// increase (Agent|Mage|Command|Emissary|Stealth) Rank by nnnn
	// Possession of the artifact can allow casting of the spell xxxxx
	// This artifact will aid in the research of spells
	// does "Scout Area" on any hex
	// hides one Population Center at any time
	// allows open seas movement like coastal movement.

	static protected void extractPowers(String report,ArtifactWrapper aw) {
		int pos; 
		String[] powers = report.split("\\.");
		if (powers[0].length() > 0) {
			pos = powers[0].indexOf(" combat ");
			if (pos >-1) {
				String rest = StringUtils.getFirstWordAfter(powers[0], " by ");
				if (rest.length() > 0 ) {
					try {
						aw.combat = Integer.parseInt(rest)/50;
					} catch (Exception e) {
						//ignore it.
					}
				}
			} else {
				pos = powers[0].indexOf(" Rank by ");
				if (pos > -1) {
					int rank = 0;
					String rest = StringUtils.getFirstWordAfter(powers[0], " by ");
					if (rest.length() > 0 ) {
						try {
							rank = Integer.parseInt(rest);
						} catch (Exception e) {
							//ignore it.
						}
					}
					aw.setRank(powers[0], rank);
				}
			}
		}
		if (powers.length > 1) {
			aw.latent = extract2ndPower(powers[1]);
		}
	}
	protected OrderResult getRAResult(String line, InfoSource infoSource) {
		if (line.contains(" was ordered to cast a lore spell.")) {
			RAResultWrapper rrw = new RAResultWrapper();
			final String start= "Research Artifact - ";
			String text = line;
			String[] singleResult = text.split(start);
			// note we skip the first index.
			for(int index=1;index < singleResult.length;index++) {
				String[] parts = singleResult[index].split(" - ");
				if (parts.length < 1)
					continue;
				String[] names = parts[0].split("#");
				if (names.length < 2)
					continue;
				String name = names[0].trim();
				String[] words = names[1].split(" ");
				String artiNo = words[0].trim();
				
				int art;
				try {
					art = Integer.parseInt(artiNo);
				} catch (NumberFormatException e) {
					// don't add if not recognised.
					continue;
				}
				ArtifactWrapper aw = rrw.getArtifactMatching(art);
				if (aw != null)
					continue; // already present - ignore.
				rrw.Add(name, art);
				aw = rrw.getArtifactMatching(art);
				if (parts.length > 1) {
					String allegiance = parts[1].trim(); // still includes tag
					if (parts.length> 2) {
						extractPowers(parts[2].trim(),aw);
					}
				}
			}
			return rrw;
		}
		return null;
	}

	protected OrderResult getScoArmyResult(String line, InfoSource infoSource) {
		try {
			String start = "A scout of the army was attempted. ";
			int i = line.indexOf(start);
			if (i < 0)
				return null;
			if (!line.contains("is located"))
				return null;
			String[] nat = StringUtils.extractNation(line);
			line = nat[0];
			String[] title = StringUtils.extractCharacterTitle(line);
			line = title[0];
			String commander = StringUtils.getUniquePart(line, "#title#", "of #nation#", false, false);
			String hex = StringUtils.getUniquePart(line, " at ", ", travel", false, false);
			String morale = StringUtils.getUniquePart(line, "Morale is ", "\\. ", false, false);
			ReconResultWrapper result = new ReconResultWrapper();
			Army a = new Army();
			a.setInformationSource(InformationSourceEnum.some);
			a.setInfoSource(infoSource);
			a.setCommanderName(commander);
			a.setCommanderTitle(title[1]);
			a.setSize(ArmySizeEnum.unknown);
			a.setHexNo(hex);
			if (morale != null)
				a.setMorale(Integer.parseInt(morale));
			Nation n = NationMap.getNationFromName(nat[1]);
			a.setNationNo(n.getNumber());
			a.setNationAllegiance(n.getAllegiance());
			int troopi = line.indexOf("Troops: ");
			if (troopi > -1) {
				String troops = line.substring(troopi + 7);
				String hc = StringUtils.getUniquePart(troops, "Heavy Cavalry: ", " ", false, false);
				String lc = StringUtils.getUniquePart(troops, "Light Cavalry: ", " ", false, false);
				String hi = StringUtils.getUniquePart(troops, "Heavy Infantry: ", " ", false, false);
				String li = StringUtils.getUniquePart(troops, "Light Infantry: ", " ", false, false);
				String ar = StringUtils.getUniquePart(troops, "Archers: ", " ", false, false);
				String ma = StringUtils.getUniquePart(troops.toLowerCase(), "men at arms: ", " ", false, false);
				if (hc != null)
					a.getElements().add(new ArmyElement(ArmyElementType.HeavyCavalry, Integer.parseInt(hc)));
				if (lc != null)
					a.getElements().add(new ArmyElement(ArmyElementType.LightCavalry, Integer.parseInt(hc)));
				if (hi != null)
					a.getElements().add(new ArmyElement(ArmyElementType.HeavyInfantry, Integer.parseInt(hc)));
				if (li != null)
					a.getElements().add(new ArmyElement(ArmyElementType.LightInfantry, Integer.parseInt(hc)));
				if (ar != null)
					a.getElements().add(new ArmyElement(ArmyElementType.Archers, Integer.parseInt(hc)));
				if (ma != null)
					a.getElements().add(new ArmyElement(ArmyElementType.MenAtArms, Integer.parseInt(hc)));
			}
			result.addArmy(a);

			return result;
		} catch (Exception e) {

		}
		return null;
	}

	protected OrderResult getScoCharResult(String line, InfoSource infoSource) {
		try {
			ScoutCharsResult result = new ScoutCharsResult();
			String start = "A scout for characters was attempted. Found:";
			int i = line.indexOf(start);
			if (i < 0)
				return null;
			int j = line.indexOf(" One or more reports may be incorrect.");
			if (j == -1)
				j = line.indexOf(" Nothing else was reported at this time.", i);
			if (j < 0)
				return null;
			String chars = line.substring(i + start.length(), j).trim();
			String[] chs = chars.split("\\.");
			for (String ch : chs) {
				ch = ch.trim();
//				String allegiance = null;
//				String gender = null;
				if (ch.contains(" - Free People")) {
//					allegiance = "FP";
					ch = ch.replace(" - Free People", " #allegiance#");
				} else if (ch.contains(" - Dark Servant")) {
//					allegiance = "DS";
					ch = ch.replace(" - Dark Servant", " #allegiance#");
				}
				if (ch.endsWith(" Male")) {
//					gender = "Male";
					ch = ch.replace(" Male", "");
				} else if (ch.endsWith(" Female")) {
//					gender = "Female";
					ch = ch.replace(" Female", "");
				}
				if (ch.startsWith("An unknown")) {
					if (ch.contains("Free People")) {
//						allegiance = "FP";
					} else if (ch.contains("Dark Servant")) {
//						allegiance = "DS";
					}
					// TODO handle these cases
					// Character newChar = new Character();
					// newChar.setName("[Unknown " + allegiance + "/" + gender +
					// "]");
					// newChar.setId(Character.getIdFromName(newChar.getName()));
					// newChar.setTitle("");
					// newChar.setNationNo(0);
					// newChar.setInfoSource(infoSource);
					// newChar.setInformationSource(InformationSourceEnum.limited);
					// result.addCharacter(newChar);
				} else {
					String[] chPlusNation = StringUtils.extractNation(ch);
					if (chPlusNation != null) {
						String[] chPlusTitle = StringUtils.extractCharacterTitle(chPlusNation[0]);
						if (chPlusTitle != null) {
							String charName = StringUtils.getUniquePart(chPlusTitle[0], "#title#", " - #nation#", false, false);
							if (charName != null) {
								Character newChar = new Character();
								newChar.setName(charName);
								newChar.setId(Character.getIdFromName(charName));
								newChar.setTitle(chPlusTitle[1]);
								newChar.setNationNo(NationMap.getNationFromName(chPlusNation[1]).getNumber());
								newChar.setInfoSource(infoSource);
								newChar.setInformationSource(InformationSourceEnum.limited);
								result.addCharacter(newChar);
							}
						}

					} else if (ch.contains("#allegiance#")) {
						// todo parse simple name or generic description such as
						// "dark servant male"
						String charName = StringUtils.getUniquePart(ch, "^", "#allegiance#", false, false);
						if (charName != null) {
							Character newChar = new Character();
							newChar.setName(charName);
							newChar.setId(Character.getIdFromName(charName));
							newChar.setTitle("");
							// todo set allegiance
							newChar.setNationNo(0);
							newChar.setInfoSource(infoSource);
							newChar.setInformationSource(InformationSourceEnum.limited);
							result.addCharacter(newChar);
						}
					} else {
						String charName = ch;
						if (charName != null) {
							Character newChar = new Character();
							newChar.setName(charName);
							newChar.setId(Character.getIdFromName(charName));
							newChar.setTitle("");
							newChar.setNationNo(0);
							newChar.setInfoSource(infoSource);
							newChar.setInformationSource(InformationSourceEnum.limited);
							result.addCharacter(newChar);
						}
					}
				}
			}
			return result;
		} catch (Exception exc) {
			return null;
		}
	}

	protected OrderResult getPalantirResult(String line, InfoSource infoSource,GameMetadata gm) {
		if (line.contains("e was ordered to use a scrying artifact.")) {
			if (!line.contains("None"))
				return getReconResult(line, infoSource, "Foreign armies identified:", "None", " See report below",gm.getNations());
		}
		return null;
	}

	protected OrderResult getScryResult(String line, InfoSource infoSource,GameMetadata gm) {
		return getReconResult(line, infoSource, "Scry Area - Foreign armies identified:", "None", " See report below",gm.getNations());
	}

	protected OrderResult getReconResult(String line, InfoSource infoSource,GameMetadata gm) {
		return getReconResult(line, infoSource, "was ordered to recon the area. ", "No armies were found", " See Map below",gm.getNations());
	}

	protected OrderResult getReconResult(String line, InfoSource infoSource, String orderMessage, String noneMessage, String seeBelowMessage,ArrayList<Nation> nationList) {
		try {
			if (line.contains(orderMessage)) {
				if (line.contains(noneMessage))
					return null;
				int i = line.indexOf(orderMessage);
				line = line.substring(i + orderMessage.length()).trim();
				i = line.toLowerCase().indexOf(seeBelowMessage.toLowerCase());
				line = line.substring(0, i);
				line = line.replace("  ", " "); // replace 2 spaces with 1

				ReconResultWrapper rrw = new ReconResultWrapper();

				ArrayList<String> parts = StringUtils.getParts(line, "(^)|(at \\d{4})", "at \\d{4}", false, true);
				for (String part : parts) {
					for (Nation n : nationList) {
						String nn = StringUtils.getUniquePart(part, " " + n.getName(), " with about", true, false);
						if (nn != null && nn.equals(n.getName())) {
							part = part.replace(" of the " + n.getName(), "#nation#").replace(" of " + n.getName(), "#nation#");
							String character = StringUtils.getUniquePart(part, "^", "#nation#", false, false);
							String troops = StringUtils.getUniquePart(part, " with about ", " troops ", false, false);
							String hex = StringUtils.getUniquePart(part, " troops at ", "(\\.)|$", false, false);
							Army a = new Army();
							a.setInformationSource(InformationSourceEnum.some);
							a.setInfoSource(infoSource);
							a.setCommanderName(character);
							a.setCommanderTitle("");
							a.setSize(ArmySizeEnum.unknown);
							try {
								a.setTroopCount(Integer.parseInt(troops));
							} catch (Exception e) {
								a.setTroopCount(0);
							}
							a.setHexNo(hex);
							a.setNationNo(n.getNumber());
							a.setNationAllegiance(n.getAllegiance());
							rrw.addArmy(a);
							break;
						}
					}
				}
				return rrw;
			}
			return null;
		} catch (Exception exc) {
			return null;
		}
	}

	protected OrderResult getRCTOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Reveal Character True - ", " is located at ", "." };
		String matches[] = matchPattern(line, ptr);
		int hexIndex = 1;
		if (matches == null) {
			matches = matchPattern(line,new String[] {"was ordered to cast a lore spell. Reveal Character True - ", " is located in the ", " at ", "."});
			hexIndex = 2;
		}
		if (matches == null) {
			matches = matchPattern(line,new String[] {"was ordered to cast a lore spell. Reveal Character True - ", " may be located in the ", " at ", "."});
			hexIndex = 2;
		}
		if (matches != null) {
			RevealCharacterTrueResultWrapper rw = new RevealCharacterTrueResultWrapper();
			rw.setCharacterName(matches[0]);
			rw.setHexNo(Integer.parseInt(matches[hexIndex]));
			return rw;
		}
		return null;
	}

	protected OrderResult getRCOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Reveal Character - ", " is located at or near ", "." };
		String matches[] = matchPattern(line, ptr);
		if (matches != null) {
			RevealCharacterResultWrapper rw = new RevealCharacterResultWrapper();
			rw.setCharacterName(matches[0]);
			rw.setHexNo(Integer.parseInt(matches[1]));
			return rw;
		}
		return null;
	}

	protected OrderResult getOwnedLAOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Locate Artifact - ", " #", "is possessed by ", " at or near ", "." };
		String matches[] = matchPattern(line, ptr);
		if (matches == null) {
			// this pattern seems current in 2018
			matches = matchPattern(line,new String[] {"was ordered to cast a lore spell. Locate Artifact - ", " #", "may be possessed by", " at or near ", "."});
		}
		if (matches != null) {
			LocateArtifactResultWrapper or = new LocateArtifactResultWrapper();
			or.setArtifactName(matches[0]);
			or.setArtifactNo(Integer.parseInt(matches[1]));
			or.setOwner(matches[2]);
			or.setHexNo(Integer.parseInt(matches[3]));
			return or;
		}
		return null;
	}

	protected OrderResult getOwnedLATOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Locate Artifact True - ", " #", "is possessed by ", " in the ", " at ", "." };
		String matches[] = matchPattern(line, ptr);
		if (matches == null) {
			// this pattern seems current in 2018
			matches = matchPattern(line,new String[] {"was ordered to cast a lore spell. Locate Artifact True - ", " #", "may be possessed by ", " in the ", " at ", "."});
		}
		if (matches != null) {
			LocateArtifactTrueResultWrapper or = new LocateArtifactTrueResultWrapper();
			or.setArtifactName(matches[0]);
			// cope with "blah #200, a sword, "
			int pos = matches[1].indexOf(",");
			if (pos >0) {
				matches[1] = matches[1].substring(0,pos);
			}
			or.setArtifactNo(Integer.parseInt(matches[1]));
			or.setOwner(matches[2]);
			or.setHexNo(Integer.parseInt(matches[4]));
			return or;
		}
		return null;
	}

	public OrderResult getLAOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Locate Artifact - ", " #", "is located at or near ", "." };
		String matches[] = matchPattern(line, ptr);
		if (matches != null) {
			LocateArtifactResultWrapper or = new LocateArtifactResultWrapper();
			or.setArtifactName(matches[0]);
			or.setArtifactNo(Integer.parseInt(matches[1]));
			or.setHexNo(Integer.parseInt(matches[2]));
			return or;
		}
		return null;
	}

	public OrderResult getLATOrderResult(String line) {
		String ptr[] = new String[] { "was ordered to cast a lore spell. Locate Artifact True - ", " #", "is located ", " at ", "." };
		String matches[] = matchPattern(line, ptr);
		if (matches != null) {
			LocateArtifactTrueResultWrapper or = new LocateArtifactTrueResultWrapper();
			or.setArtifactName(matches[0]);
			// cope with "artifact #nn, a Ring"
			int pos = matches[1].indexOf(',');
			if (pos > -1) {
				or.setArtifactNo(Integer.parseInt(matches[1].substring(0, pos)));
			} else {
				or.setArtifactNo(Integer.parseInt(matches[1]));
			}
			or.setHexNo(Integer.parseInt(matches[3]));
			return or;
		}
		return null;
	}

	private String[] matchPattern(String line, String[] pattern) {
		try {
			int[] locations = new int[pattern.length];
			int[] widths = new int[pattern.length];
			for (int j = 0; j < pattern.length; j++) {
				String p = pattern[j];
				int startIndex = 0;
				if (j > 0) {
					startIndex = locations[j - 1];
				}
				int i = line.indexOf(p, startIndex);
				if (i == -1)
					return null;
				locations[j] = i;
				widths[j] = p.length();
			}
			String[] matches = new String[locations.length - 1];
			for (int j = 0; j < matches.length; j++) {
				matches[j] = line.substring(locations[j] + widths[j], locations[j + 1]).trim();
			}
			return matches;
		} catch (Throwable e) {
			return null;
		}
	}

	private OrderResult getExecutionOrderResult(String line) {
		String ptr = "was ordered to execute a hostage.";
		String ptr1 = " was executed.";
		int i = line.indexOf(ptr);
		int j = line.indexOf(ptr1);
		if (i > -1 && j > -1) {
			int s = i + ptr.length();
			String charName = line.substring(s, j).trim();
			ExecutionResultWrapper arw = new ExecutionResultWrapper();
			arw.setCharacter(charName);
			return arw;
		}
		return null;
	}

	private OrderResult getInfOtherOrderResult(String line) {
		String ptr = "was ordered to influence their population center loyalty. The loyalty was influenced/reduced at ";
		String ptr1 = ". Current loyalty is perceived to be ";
		String ptr2 = ".";
		int i = line.indexOf(ptr);
		int j = line.indexOf(ptr1);
		if (i > -1 && j > -1) {
			int k = line.substring(j).indexOf(ptr2, 5);
			int s = i + ptr.length();
			String popCenterName = line.substring(s, j).trim();
			s = j + ptr1.length();
			String loyalty = line.substring(s, j + k);
			InfluenceOtherResultWrapper rw = new InfluenceOtherResultWrapper();
			rw.setPopCenter(popCenterName);
			rw.setLoyalty(loyalty);
			return rw;
		}
		return null;
	}

	private OrderResult getAssassinationOrderResult(String line) {
		String ptr = "was ordered to assassinate a character.";
		String ptr1 = "was assassinated.";
		int i = line.indexOf(ptr);
		int j = line.indexOf(ptr1);
		if (i > -1 && j > -1) {
			int s = i + ptr.length();
			String charName = line.substring(s, j).trim();
			AssassinationResultWrapper arw = new AssassinationResultWrapper();
			arw.setCharacter(charName);
			return arw;
		}
		return null;
	}
}
