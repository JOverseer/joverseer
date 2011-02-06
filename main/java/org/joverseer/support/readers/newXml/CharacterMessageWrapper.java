package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Army;
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
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public String getOrdersAsString() {
		String ret = "";
		for (String line : lines) {
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
		for (String line : lines) {
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
		for (String line : lines) {
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
		for (String line : lines) {
			if (line.indexOf(c.getName() + " was assassinated.") > -1)
				return true;
		}
		return false;
	}

	protected boolean getMissing(Character c) {
		for (String line : lines) {
			if (line.indexOf(c.getName() + " has gone missing.") > -1)
				return true;
		}
		return false;
	}

	protected boolean getCursed(Character c) {
		for (String line : lines) {
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
		for (String line : lines) {
			if (line.indexOf(c.getName() + " was executed.") > -1)
				return true;
		}
		return false;
	}

	public ArrayList<OrderResult> getOrderResults(InfoSource infoSource) {
		ArrayList<OrderResult> ret = new ArrayList<OrderResult>();
		for (String line : lines) {
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
				or = getReconResult(line, infoSource);
			if (or == null)
				or = getScryResult(line, infoSource);
			if (or == null)
				or = getRAResult(line, infoSource);
			if (or == null)
				or = getScoutHexResult(line, infoSource);
			if (or == null)
				or = getScoCharResult(line, infoSource);
			if (or == null)
				or = getScoutPopCenterResult(line, infoSource);
			if (or != null) {
				ret.add(or);
			}
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

	protected OrderResult getScoutHexResult(String line, InfoSource infoSource) {
		try {
			if (line.contains("A scout of the hex was attempted.")) {
				line = StringUtils.replaceNationNames(line);
				ScoutHexResult result = new ScoutHexResult();
				String climate = StringUtils.getUniquePart(line, "Climate is ", "Cool|Cold|Mild|Warm|Polar|Hot", false, true);
				String foreignArmies = StringUtils.getUniquePart(line, "Foreign armies present:", "\\.", false, false);
				if (foreignArmies != null) {
					String[] fas = foreignArmies.split("\\-");
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
						a.setCommanderName(commanderName);
						a.setCommanderTitle("");
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

	protected OrderResult getRAResult(String line, InfoSource infoSource) {
		if (line.contains(" was ordered to cast a lore spell. Research Artifact -")) {
			String artiName = StringUtils.getUniquePart(line, "Research Artifact - ", "#\\d{1,3}", false, false);
			String artiNo = StringUtils.getUniquePart(line, artiName + " #", " is a ", false, false);
			RAResultWrapper rrw = new RAResultWrapper();
			rrw.setArtiName(artiName);
			rrw.setArtiNo(artiNo);
			return rrw;
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
				}
			}
			return result;
		} catch (Exception exc) {
			return null;
		}
	}

	protected OrderResult getScryResult(String line, InfoSource infoSource) {
		return getReconResult(line, infoSource, "Scry Area - Foreign armies identified:", "None", " See report below");
	}

	protected OrderResult getReconResult(String line, InfoSource infoSource) {
		return getReconResult(line, infoSource, "was ordered to recon the area. ", "No armies were found", " See Map below");
	}

	protected OrderResult getReconResult(String line, InfoSource infoSource, String orderMessage, String noneMessage, String seeBelowMessage) {
		try {
			if (line.contains(orderMessage)) {
				if (line.contains(noneMessage))
					return null;
				int i = line.indexOf(orderMessage);
				line = line.substring(i + orderMessage.length()).trim();
				i = line.toLowerCase().indexOf(seeBelowMessage.toLowerCase());
				line = line.substring(0, i);

				ReconResultWrapper rrw = new ReconResultWrapper();

				ArrayList<String> parts = StringUtils.getParts(line, "(^)|(at \\d{4})", "at \\d{4}", false, true);
				for (String part : parts) {
					for (int j = 0; j < 26; j++) {
						Nation n = NationMap.getNationFromNo(j);
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
		if (matches != null) {
			RevealCharacterTrueResultWrapper rw = new RevealCharacterTrueResultWrapper();
			rw.setCharacterName(matches[0]);
			rw.setHexNo(Integer.parseInt(matches[1]));
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
		if (matches != null) {
			LocateArtifactTrueResultWrapper or = new LocateArtifactTrueResultWrapper();
			or.setArtifactName(matches[0]);
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
			or.setArtifactNo(Integer.parseInt(matches[1]));
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
