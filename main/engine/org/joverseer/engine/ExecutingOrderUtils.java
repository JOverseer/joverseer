package org.joverseer.engine;

import java.util.ArrayList;
import java.util.Collections;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;

public class ExecutingOrderUtils {
	public static boolean isCapitalHex(Turn turn, int nationNo, int hexNo) {
		PopulationCenter pc = getPopCenter(turn, hexNo);
		if (pc == null)
			return false;
		if (!checkNation(pc, nationNo))
			return false;
		return pc.getCapital();
	}

	public static boolean checkNation(IBelongsToNation o, int nationNo) {
		return o.getNationNo() == nationNo;
	}

	public static boolean checkNation(IBelongsToNation a, IBelongsToNation b) {
		return a.getNationNo().equals(b.getNationNo());
	}

	public static PopulationCenter getPopCenter(Turn turn, int hexNo) {
		PopulationCenter popCenter = (PopulationCenter) turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", hexNo);
		return popCenter;
	}

	public static Army getArmy(Turn turn, int hexNo, String commanderName) {
		Army army = (Army) turn.getContainer(TurnElementsEnum.Army).findFirstByProperties(new String[] { "hexNo", "commanderName" }, new String[] { String.valueOf(hexNo), commanderName });
		return army;
	}

	public static ArrayList<Army> getArmies(Turn turn, int hexNo) {
		return turn.getContainer(TurnElementsEnum.Army).findAllByProperty("hexNo", String.valueOf(hexNo));
	}

	public static Character getCharacter(Turn turn, String name) {
		Character ret = (Character) turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name);
		return ret;
	}

	public static Character getCharacterById(Turn turn, String id) {
		Character ret = (Character) turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("id", id);
		return ret;
	}

	public static boolean checkSameHex(IHasMapLocation a, IHasMapLocation b) {
		return a.getX() == b.getX() && a.getY() == b.getY();
	}

	public static Character getCommander(Turn turn, Army army, boolean checkSameHex) {
		Character c = getCharacter(turn, army.getCommanderName());
		if (c == null)
			return null;
		if (!checkSameHex || checkSameHex(c, army))
			return c;
		return null;
	}

	public static ArrayList<Character> getCharsWithArmy(Turn turn, Army army, boolean includeCommander) {
		ArrayList<Character> ret = new ArrayList<Character>();
		for (String name : army.getCharacters()) {
			Character c = getCharacter(turn, name.trim());
			if (c != null)
				ret.add(c);
		}
		Character commander = getCommander(turn, army, false);
		if (commander != null) {
			if (includeCommander && !ret.contains(commander)) {
				ret.add(commander);
			} else if (!includeCommander && ret.contains(commander)) {
				ret.remove(commander);
			}
		}
		return ret;
	}

	public static void refreshCharacter(Turn turn, Character item) {
		refreshItem(turn, TurnElementsEnum.Character, item);
	}

	public static void refreshArmy(Turn turn, Army item) {
		refreshItem(turn, TurnElementsEnum.Army, item);
	}

	@SuppressWarnings("unchecked")
	public static void refreshItem(Turn turn, TurnElementsEnum el, Object item) {
		Container c = turn.getContainer(el);
		c.refreshItem(item);
	}

	public static void setArmyLocation(Turn turn, Army a, int hex) {
		a.setHexNo(String.valueOf(hex));
		refreshArmy(turn, a);
		for (Character c : getCharsWithArmy(turn, a, true)) {
			c.setHexNo(hex);
			refreshCharacter(turn, c);
		}
	}

	public static Company getCompany(Turn turn, String commander) {
		Company comp = (Company) turn.getContainer(TurnElementsEnum.Company).findFirstByProperty("commander", commander);
		return comp;
	}

	public static ArrayList<Character> getCharsWithCompany(Turn turn, Company company, boolean includeCommander) {
		ArrayList<Character> ret = new ArrayList<Character>();
		for (String name : company.getMembers()) {
			Character c = getCharacter(turn, name);
			if (c == null)
				continue;
			if (!includeCommander && c.getName().equals(company.getCommander()))
				continue;
			ret.add(c);
		}
		if (includeCommander) {
			Character c = getCharacter(turn, company.getCommander());
			if (!ret.contains(c))
				ret.add(c);
		}
		return ret;
	}

	public static boolean hasAvailableProduct(PopulationCenter pc, ProductEnum product, int amount) {
		return getStores(pc, product) >= amount;
	}

	public static int getStores(PopulationCenter pc, ProductEnum product) {
		Integer s = pc.getStores(product);
		if (s == null)
			s = 0;
		return s;
	}

	public static int getProduction(PopulationCenter pc, ProductEnum product) {
		Integer s = pc.getProduction(product);
		if (s == null)
			s = 0;
		return s;
	}

	public static void consumeProduct(PopulationCenter pc, ProductEnum product, int amount) {
		int stores = getStores(pc, product);
		int consume = Math.min(stores, amount);
		stores -= consume;
		pc.setStores(product, stores);
	}

	public static NationEconomy getNationEconomy(Turn turn, int nationNo) {
		return (NationEconomy) turn.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", nationNo);
	}

	public static Company findCompany(Turn turn, Character c) {
		for (Company comp : (ArrayList<Company>) turn.getContainer(TurnElementsEnum.Company).getItems()) {
			for (String member : comp.getMembers()) {
				if (member.equals(c.getName()))
					return comp;
			}
			if (comp.getCommander().equals(c.getName()))
				return comp;
		}
		return null;
	}

	public static Army findArmy(Turn turn, Character c) {
		for (Army army : (ArrayList<Army>) turn.getContainer(TurnElementsEnum.Army).getItems()) {
			for (String member : army.getCharacters()) {
				if (member.trim().equals(c.getName()))
					return army;
			}
			if (army.getCommanderName().equals(c.getName()))
				return army;
		}
		return null;
	}

	public static boolean notWithArmyOrCompany(Turn turn, Character c) {
		return findCompany(turn, c) == null && findArmy(turn, c) == null;
	}

	/**
	 * Verifies that a has friendly relations to b (also returns true if a and b
	 * are of same nation)
	 */
	public static boolean checkFriendly(Turn t, IBelongsToNation a, IBelongsToNation b) {
		try {
			if (a.getNationNo() == b.getNationNo())
				return true;
			NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", a.getNationNo());
			if (nr == null)
				return false;
			return NationRelationsEnum.Friendly.equals(nr.getRelationsFor(b.getNationNo()));
		} catch (RuntimeException exc) {
			throw exc;
		}
	}

	/**
	 * Verifies that a has non hostile (friendly or tolerated) relations to b
	 * (also returns true if a and b are of same nation)
	 */
	public static boolean checkNonHostile(Turn t, IBelongsToNation a, IBelongsToNation b) {
		try {
			if (a.getNationNo() == b.getNationNo() || a.getNationNo().equals(b.getNationNo()))
				return true;
			NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", a.getNationNo());
			return NationRelationsEnum.Friendly.equals(nr.getRelationsFor(b.getNationNo())) || NationRelationsEnum.Tolerated.equals(nr.getRelationsFor(b.getNationNo()));
		} catch (RuntimeException exc) {
			throw exc;
		}
	}

	/**
	 * Verifies that a can attack b
	 */
	public static boolean canAttack(Turn t, IBelongsToNation a, IBelongsToNation b) {
		try {
			if (a.getNationNo() == b.getNationNo())
				return false;
			NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", a.getNationNo());
			return NationRelationsEnum.Neutral.equals(nr.getRelationsFor(b.getNationNo())) || NationRelationsEnum.Disliked.equals(nr.getRelationsFor(b.getNationNo())) || NationRelationsEnum.Hated.equals(nr.getRelationsFor(b.getNationNo()));
		} catch (RuntimeException exc) {
			throw exc;
		}
	}

	public static Army createArmy(String commanderName, int nationNo, NationAllegianceEnum allegiance, int hexNo, InfoSource infoSource) {
		Army army = new Army();
		army.setSize(ArmySizeEnum.unknown);
		army.setInformationSource(InformationSourceEnum.exhaustive);
		army.setInfoSource(infoSource);
		army.setCommanderName(commanderName);
		army.setNationNo(nationNo);
		army.setNationAllegiance(allegiance);
		army.setCommanderTitle("");
		army.setHexNo(String.valueOf(hexNo));
		army.setMorale(10);
		return army;
	}

	public static Army createArmy(Character commander, InfoSource infoSource) {
		return createArmy(commander.getName(), commander.getNationNo(), commander.getNation().getAllegiance(), commander.getHexNo(), infoSource);
	}

	public static ProductPrice getProductPrice(Turn t, ProductEnum product) {
		ProductPrice pp = (ProductPrice) t.getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", product);
		return pp;
	}

	public static int getTotalStoresForNation(Turn turn, ProductEnum product, int nationNo) {
		int totalAmount = 0;
		for (PopulationCenter pop : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo)) {
			totalAmount += ExecutingOrderUtils.getStores(pop, product);
		}
		return totalAmount;
	}

	public static int getTotalProductionForNation(Turn turn, ProductEnum product, int nationNo) {
		int totalAmount = 0;
		for (PopulationCenter pop : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo)) {
			totalAmount += ExecutingOrderUtils.getProduction(pop, product);
		}
		return totalAmount;
	}

	public static void consumeProductPercentageForNation(Turn turn, ProductEnum product, int pct, int nationNo) {
		for (PopulationCenter pop : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo)) {
			int a = ExecutingOrderUtils.getStores(pop, product);
			a = a * pct / 100;
			ExecutingOrderUtils.consumeProduct(pop, product, a);
		}
	}

	public static boolean atCapital(Turn turn, Character c) {
		PopulationCenter pc = getPopCenter(turn, c.getHexNo());
		return (pc != null && pc.getCapital() && checkNation(c, pc));
	}

	public static void removeCharacterFromArmy(Turn turn, Army a, Character c) {
		String toRemove = null;
		for (String name : a.getCharacters()) {
			if (name.trim().equals(c.getName())) {
				toRemove = name;
				break;
			}
		}
		if (toRemove != null) {
			a.getCharacters().remove(toRemove);
		}
	}

	public static void removeCharacterFromCompany(Turn turn, Company comp, Character c) {
		if (comp.getMembers().contains(c.getName()))
			comp.getMembers().remove(c.getName());
	}

	public static void setCharacterTitle(Turn turn, Character c) {
		String title = getCharacterTitle(turn, c);
		if (title == null)
			title = "";
		c.setTitle(title);
	}

	public static String getCharacterTitle(Turn turn, Character c) {
		String type = "Commander";
		if (c == null)
			return "Unknown character";
		int max = c.getCommand();
		if (getArmy(turn, c.getHexNo(), c.getName()) == null) {
			if (c.getCommand() < c.getAgent()) {
				max = c.getAgent();
				type = "Agent";
			}
			if (c.getMage() > max) {
				max = c.getMage();
				type = "Mage";
			}
			if (c.getEmmisary() > max) {
				max = c.getEmmisary();
				type = "Emissary";
			}
		}
		String title = InfoUtils.getCharacterTitle(type, max);
		return title;
	}

	public static void removeArmy(Turn turn, Army a) {
		turn.getContainer(TurnElementsEnum.Army).removeItem(a);
	}

	public static void setCharacterStatTotals(Game game, Turn turn, Character c) {
		int commandBonus = 0;
		int mageBonus = 0;
		int agentBonus = 0;
		int emissaryBonus = 0;
		int challengeBonus = 0;
		int stealthBonus = 0;

		for (int artiNo : c.getArtifacts()) {
			ArtifactInfo ai = game.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
			if (ai == null) {
				throw new RuntimeException("ArtifactInfo " + artiNo + " not found.");
			}
			String bonusType = ai.getBonusType();
			int rank = ai.getBonusRank();
			if (bonusType == null)
				continue;
			if ("Command".equals(bonusType)) {
				commandBonus += rank;
			} else if ("Emissary".equals(bonusType)) {
				emissaryBonus += rank;
			} else if ("Mage".equals(bonusType)) {
				mageBonus += rank;
			} else if ("Agent".equals(bonusType)) {
				agentBonus += rank;
			} else if ("Stealth".equals(bonusType)) {
				stealthBonus += rank;
			} else if ("Combat".equals(bonusType)) {
				challengeBonus += rank / 50;
			}
		}
		if (c.getCommand() > 0) {
			c.setCommandTotal(c.getCommand() + commandBonus);
		} else {
			c.setCommandTotal(0);
		}
		if (c.getEmmisary() > 0) {
			c.setEmmisaryTotal(c.getEmmisary() + emissaryBonus);
		} else {
			c.setEmmisaryTotal(0);
		}
		if (c.getMage() > 0) {
			c.setMageTotal(c.getMage() + mageBonus);
		} else {
			c.setMageTotal(0);
		}
		if (c.getAgent() > 0) {
			c.setAgentTotal(c.getAgent() + agentBonus);
		} else {
			c.setAgentTotal(0);
		}

		c.setStealthTotal(c.getStealth() + stealthBonus);

		// compute challenge rank
		int commandChallenge = c.getCommandTotal();
		int mageChallenge = c.getMageTotal();
		int agentChallenge = c.getAgentTotal() * 75 / 100;
		int emissaryChallenge = c.getEmmisaryTotal() / 2;

		int max = commandChallenge;
		if (mageChallenge > max) {
			max = mageChallenge;
		}
		if (agentChallenge > max) {
			max = agentChallenge;
		}
		if (emissaryChallenge > max) {
			max = emissaryChallenge;
		}
		int challenge = commandChallenge + mageChallenge + agentChallenge + emissaryChallenge;
		challenge = max + (challenge - max) / 4;
		c.setChallenge(challenge + challengeBonus);

	}

	public static void healCharacter(Turn turn, Character c) {
		// TODO heals fast
		Integer health = c.getHealth();
		if (health == null)
			return;
		if (health == 0)
			return;
		if (health == 100)
			return;
		int recover = Math.min(100 - health, 14);
		// TODO add message
		c.setHealth(health + recover);
	}

	public static int getSpellProficiency(Character c, int spellId) {
		for (SpellProficiency sp : c.getSpells()) {
			if (sp.getSpellId() == spellId)
				return sp.getProficiency();
		}
		return 0;
	}

	/**
	 * winner plunders dead char's artifacts
	 */
	public static void plunderArtifacts(Turn turn, Character winner, Character dead) {
		ArrayList<Integer> artis = new ArrayList<Integer>();
		artis.addAll(dead.getArtifacts());
		Collections.shuffle(artis);
		int count = 0;
		for (Integer ai : artis) {
			if (winner.getArtifacts().size() < 6) {
				winner.getArtifacts().add(ai);
				count++;
			}
		}
		// TODO rest of artifacts must fall on hex

		appendMessage(winner, "{char} recovered some artifacts from " + dead.getName() + "'s body.");
	}

	public static void characterDied(Turn turn, Character c, CharacterDeathReasonEnum reason) {
		if (reason == null)
			reason = CharacterDeathReasonEnum.Dead;
		c.setDeathReason(reason);
		Army a;
		if ((a = getArmy(turn, c.getHexNo(), c.getName())) != null) {
			// army commander
			Character newCommander = null;
			for (Character member : getCharsWithArmy(turn, a, false)) {
				if (member.getCommand() > 0) {
					if (newCommander == null) {
						newCommander = member;
					} else if (newCommander.getCommandTotal() < member.getCommandTotal()) {
						newCommander = member;
					}
				}
			}
			if (newCommander != null) {
				// set new commander
				removeCharacterFromArmy(turn, a, newCommander);
				a.setCommanderName(newCommander.getName());
				a.setMorale(Math.max(a.getMorale() - 10, 1));
			} else {
				// disband the army
				disbandArmy(turn, a);
				appendMessage(c, "{char}'s army was disbanded.");
			}
		} else if ((a = findArmy(turn, c)) != null) {
			// not army commander
			// remove from army
			removeCharacterFromArmy(turn, a, c);
		}
		Company comp;
		if ((comp = getCompany(turn, c.getName())) != null) {
			// company commander
			// disband the company
			disbandCompany(turn, comp);
			appendMessage(c, "{char}'s company was disbanded.");
		} else if ((comp = findCompany(turn, c)) != null) {
			// remove the character from the company
			removeCharacterFromCompany(turn, comp, c);
		}
	}

	public static void disbandArmy(Turn turn, Army a) {
		turn.getContainer(TurnElementsEnum.Army).removeItem(a);
	}

	public static void disbandCompany(Turn turn, Company comp) {
		turn.getContainer(TurnElementsEnum.Company).removeItem(comp);
	}

	public static void appendMessage(Character c, String message) {
		String orderResults = c.getOrderResults();
		if (orderResults == null)
			orderResults = "";
		orderResults += (orderResults.equals("") ? "" : " ") + renderVariable(message, c);
		c.setOrderResults(orderResults);
	}

	public static String renderVariable(String message, Character c) {
		message = message.replace("{char}", c.getName());
		message = message.replace("{hex}", String.valueOf(c.getHexNo()));
		return message;
	}

	public static int getRevenue(Turn turn, PopulationCenter pc) {
		int r = (pc.getSize().getCode() - 1) * 2500;
		NationEconomy ne = getNationEconomy(turn, pc.getNationNo());
		return r * ne.getTaxRate() / 100;
	}

	public static int getAvailableGold(Turn turn, int nationNo) {
		NationEconomy ne = getNationEconomy(turn, nationNo);
		return ne.getAvailableGold();
	}

	public static void consumeGold(Turn turn, int nationNo, int gold) {
		NationEconomy ne = getNationEconomy(turn, nationNo);
		ne.setAvailableGold(ne.getAvailableGold() - gold);
	}

	public static int getMageBonus(Character c) {
		return c.getMageTotal() - c.getMage();
	}

	public static int spellCastRoll(Character c, SpellProficiency sp) {
		int mb = getMageBonus(c);
		int p = sp.getProficiency();
		return Randomizer.roll(mb + p);
	}

	public static boolean anchorShips(Turn turn, Army a) {
		ArmyElement warships = a.getElement(ArmyElementType.Warships);
		ArmyElement transports = a.getElement(ArmyElementType.Transports);
		if (warships != null && warships.getNumber() > 0 || transports != null && transports.getNumber() > 0) {
			Army anchoredShips = (Army) turn.getContainer(TurnElementsEnum.Army).findFirstByProperties(new String[] { "hexNo", "nationNo", "commanderName" }, new Object[] { a.getHexNo(), a.getNationNo(), "[Anchored Ships]" });
			if (anchoredShips == null) {
				anchoredShips = new Army();
				anchoredShips.setCommanderName("[Anchored Ships]");
				anchoredShips.setCommanderTitle("");
				anchoredShips.setHexNo(a.getHexNo());
				anchoredShips.setNationAllegiance(a.getNationAllegiance());
				anchoredShips.setInfoSource(new XmlTurnInfoSource(turn.getTurnNo(), a.getNationNo()));
				anchoredShips.setInformationSource(InformationSourceEnum.exhaustive);
				anchoredShips.setNationNo(a.getNationNo());
				anchoredShips.setSize(ArmySizeEnum.unknown);
				anchoredShips.setElement(new ArmyElement(ArmyElementType.Warships, 0));
				anchoredShips.setElement(new ArmyElement(ArmyElementType.Transports, 0));
				anchoredShips.setNavy(true);
				turn.getContainer(TurnElementsEnum.Army).addItem(anchoredShips);
			}
			if (warships != null) {
				ArmyElement existingWarships = anchoredShips.getElement(ArmyElementType.Warships);
				existingWarships.setNumber(existingWarships.getNumber() + warships.getNumber());
				a.getElements().remove(warships);
			}
			if (transports != null) {
				ArmyElement existingTransports = anchoredShips.getElement(ArmyElementType.Transports);
				existingTransports.setNumber(existingTransports.getNumber() + transports.getNumber());
				a.getElements().remove(transports);
			}
			a.setNavy(false);
			return true;
		}
		return false;
	}

	public static void addElement(Army army, ArmyElement ae) {
		if (army.getElement(ae.getArmyElementType()) == null) {
			army.setElement(ae);
		} else {
			ArmyElement eae = army.getElement(ae.getArmyElementType());
			int newNumber = eae.getNumber() + ae.getNumber();
			int newTraining = (eae.getTraining() * eae.getNumber() + ae.getTraining() * ae.getNumber()) / newNumber;
			int newArmor = (eae.getArmor() * eae.getNumber() + ae.getArmor() * ae.getNumber()) / newNumber;
			int newWeapons = (eae.getWeapons() * eae.getNumber() + ae.getWeapons() * ae.getNumber()) / newNumber;
			eae.setNumber(newNumber);
			eae.setTraining(newTraining);
			eae.setWeapons(newWeapons);
			eae.setArmor(newArmor);
		}
	}

	public static void removeElementTroops(Army army, ArmyElementType type, int number) {
		ArmyElement ae = army.getElement(type);
		if (ae == null)
			return;
		ae.setNumber(ae.getNumber() - number);
		if (ae.getNumber() < 0) {
			ae.setNumber(0);
		}
		if (ae.getNumber() == 0) {
			army.removeElement(type);
		}
	}

	public static boolean hasSNA(Game game, int nationNo, SNAEnum sna) {
		Nation n = game.getMetadata().getNationByNum(nationNo);
		if (n == null)
			return false;
		return n.getSnas() != null && n.getSnas().contains(sna);
	}

	public static void cleanupArmy(Turn turn, Army a) {
		int totalTroops = 0;
		for (ArmyElementType aet : ArmyElementType.values()) {
			if (a.getNumber(aet) == 0) {
				a.removeElement(aet);
			}
			totalTroops += aet.isTroop() ? a.getNumber(aet) : 0;

		}
		if (totalTroops < 100) {
			// disband army
			int warships = a.getNumber(ArmyElementType.Warships);
			int transports = a.getNumber(ArmyElementType.Transports);
			if (warships + transports > 0) {
				Army na = new Army();
				na.setNationNo(a.getNationNo());
				na.setHexNo(a.getHexNo());
				na.setCommanderName("[Anchored Ships]");
				na.setCommanderTitle("");
				na.setNationAllegiance(a.getNationAllegiance());
				na.setInformationSource(InformationSourceEnum.exhaustive);
				na.setSize(ArmySizeEnum.unknown);
				na.setNavy(true);
				na.setInfoSource(new XmlTurnInfoSource(turn.getTurnNo(), a.getNationNo()));
				if (warships > 0) {
					na.setElement(ArmyElementType.Warships, warships);
					a.removeElement(ArmyElementType.Warships);
				}
				if (transports > 0) {
					na.setElement(ArmyElementType.Transports, transports);
					a.removeElement(ArmyElementType.Transports);
				}
				turn.getContainer(TurnElementsEnum.Army).addItem(na);
			}
			turn.getContainer(TurnElementsEnum.Army).removeItem(a);
		}
	}
}
