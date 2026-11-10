package com.middleearthgames.orderchecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.swing.JCheckBox;

import org.junit.Test;

import com.middleearthgames.orderchecker.io.Data;
import com.middleearthgames.orderchecker.io.ImportRulesCsv;

/**
 * Tests for the stop-gap patch to the legacy order checker: ruleset.csv corrections, information-only remarks,
 * the partial-fill recruit warning, the NATION (180/185) relations rule, and the 31 Oct 2026 troop rules switch.
 */
public class LegacyCheckerPatchTest {

	private static final String[] RULESET_PATHS = { "resources/metadata/orderchecker/ruleset.csv",
			"orderchecker/resources/metadata/orderchecker/ruleset.csv" };

	private InputStreamReader reader(String s) {
		InputStream is = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));
		return new InputStreamReader(is);
	}

	/** Runs the checker like Main.processOrders, answering every extra-info question "yes". */
	private void run(Main main) {
		Nation nation = main.getNation();
		String error = nation.implementPhase(1, main);
		assertNull(error);
		Vector requests = nation.getArmyRequests(main);
		nation.processArmyRequests(requests);
		int safety = 0;
		boolean done = false;
		while (!done && safety < 20) {
			safety++;
			error = nation.implementPhase(2, main);
			assertNull(error);
			Vector pending = nation.getInfoRequests();
			for (Object box : pending) {
				((JCheckBox) box).setSelected(true);
			}
			done = nation.isProcessingDone();
		}
		assertNotEquals("order checking stuck in loop", 20, safety);
	}

	/** A Main with the given rules text (no BEGIN/END tags, no trailing newline). */
	private Main mainWithRules(String rules) {
		Main main = new Main(true, new Data());
		Main.main = main;
		Ruleset ruleSet = new Ruleset();
		main.setRuleSet(ruleSet);
		ImportRulesCsv csv = new ImportRulesCsv("", ruleSet);
		assertTrue(csv.openStream(reader(csv.makeRuleString(rules))));
		assertNull(csv.parseRules());
		csv.closeFile();
		return main;
	}

	/** A Main loaded with the real ruleset.csv. */
	private Main mainWithRealRules() throws Exception {
		Main main = new Main(true, new Data());
		Main.main = main;
		Ruleset ruleSet = new Ruleset();
		main.setRuleSet(ruleSet);
		File file = null;
		for (String p : RULESET_PATHS) {
			if (new File(p).exists()) {
				file = new File(p);
				break;
			}
		}
		assertTrue("ruleset.csv not found", file != null);
		ImportRulesCsv csv = new ImportRulesCsv("", ruleSet);
		assertTrue(csv.openStream(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))));
		assertNull(csv.parseRules());
		csv.closeFile();
		return main;
	}

	private Nation nationFor(Main main, int nationNo) {
		Nation nation = new Nation();
		nation.SetNation(nationNo);
		main.setNation(nation);
		main.getData().setGameType("1650");
		nation.setGameType("1650");
		return nation;
	}

	private Character character(Nation nation, String id, String name) {
		Character c = new Character(id);
		c.setName(name);
		c.setNation(nation.getNation());
		nation.addCharacter(c);
		return c;
	}

	private PopCenter popCenter(Nation nation, int location, int size, boolean capital) {
		PopCenter pc = new PopCenter(location);
		pc.setNation(nation.getNation());
		pc.setName("Testburg");
		pc.setNewPC();
		pc.setSize(size);
		pc.setFortification(0);
		if (capital) {
			pc.setCapital(1);
			nation.setCapital(location);
		}
		nation.addPopulationCenter(pc);
		return pc;
	}

	private Order order(Character c, int number, String... params) {
		Order o = new Order(c, number);
		for (String p : params) {
			o.addParameter(p);
		}
		c.addOrder(o);
		return o;
	}

	private boolean anyContains(Vector v, String text) {
		for (Object o : v) {
			if (String.valueOf(o).contains(text)) {
				return true;
			}
		}
		return false;
	}

	// ---------------------------------------------------------------- recruits

	private static final String RECRUIT_RULES = "408,,TROOPRECRUIT,1,0,2,0\n412,,TROOPRECRUIT,1,0,3,0\n420,,TROOPRECRUIT,1,0,5,0";

	private Character recruiter(Main main, boolean newRules, int size) {
		main.setNewTroopRules(newRules);
		Nation nation = nationFor(main, 1);
		popCenter(nation, 1234, size, false);
		Character c = character(nation, "recru", "Recruiter");
		c.setLocation(1234);
		return c;
	}

	@Test
	public void classicPoolIsSizeTimes100AndOverCapIsPartialFillWarning() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, false, 1);
		Order a = order(c, 408, "60");
		Order b = order(c, 408, "60");
		run(main);
		assertEquals(0, a.getErrorResults().size());
		assertEquals(0, a.getWarnResults().size());
		assertTrue(anyContains(a.getInfoResults(), "still has 40 troops available"));
		assertEquals("over the cap is not an error", 0, b.getErrorResults().size());
		assertEquals(1, b.getWarnResults().size());
		assertTrue(String.valueOf(b.getWarnResults().get(0)), anyContains(b.getWarnResults(), "can supply only 40"));
		assertTrue(anyContains(b.getWarnResults(), "will recruit 40"));
	}

	@Test
	public void classicLightInfantryGetsNoExtraAllowance() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, false, 1);
		Order a = order(c, 412, "150");
		run(main);
		assertEquals(0, a.getErrorResults().size());
		assertTrue(anyContains(a.getWarnResults(), "can supply only 100"));
	}

	@Test
	public void newRulesCampSupplies150LightInfantry() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order a = order(c, 412, "150");
		run(main);
		assertEquals(0, a.getErrorResults().size());
		assertEquals(0, a.getWarnResults().size());
	}

	@Test
	public void newRules151stLightInfantryIsPartialFillWarningNotError() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order a = order(c, 412, "151");
		run(main);
		assertEquals(0, a.getErrorResults().size());
		assertEquals(1, a.getWarnResults().size());
		assertTrue(String.valueOf(a.getWarnResults().get(0)), anyContains(a.getWarnResults(), "can supply only 150"));
		assertTrue(anyContains(a.getWarnResults(), "will recruit 150"));
		assertTrue(anyContains(a.getWarnResults(), "light troops"));
	}

	@Test
	public void newRulesSharedPool75LightPlus50HeavyFillsCamp() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order li = order(c, 412, "75");
		Order hi = order(c, 408, "50");
		run(main);
		assertEquals(0, li.getErrorResults().size() + hi.getErrorResults().size());
		assertEquals("75 LI + 50 HI exactly fills a camp", 0, li.getWarnResults().size() + hi.getWarnResults().size());
	}

	@Test
	public void newRules100HeavyPlusOneLightGoesOverTheCap() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order hi = order(c, 408, "100");
		Order li = order(c, 412, "1");
		run(main);
		assertEquals(0, hi.getWarnResults().size());
		assertEquals(0, li.getErrorResults().size());
		assertTrue(String.valueOf(li.getWarnResults()), anyContains(li.getWarnResults(), "can supply only 0"));
	}

	@Test
	public void newRulesArchersAndMenAtArmsDoubleAllowance() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order ma = order(c, 420, "200");
		run(main);
		assertEquals(0, ma.getErrorResults().size());
		assertEquals(0, ma.getWarnResults().size());
	}

	@Test
	public void hireArmyLightCavalryAtOnePointFiveTimes() {
		// 770: amount is P1, troop type is P2 (the real ruleset row).
		Main main = mainWithRules("770,,TROOPRECRUIT,1,2,0,5");
		Character c = recruiter(main, true, 1);
		Order ok = order(c, 770, "150", "LC");
		run(main);
		assertEquals(0, ok.getErrorResults().size());
		assertEquals(0, ok.getWarnResults().size());

		Main main2 = mainWithRules("770,,TROOPRECRUIT,1,2,0,5");
		Character c2 = recruiter(main2, true, 1);
		Order over = order(c2, 770, "151", "LC");
		run(main2);
		assertEquals(0, over.getErrorResults().size());
		assertTrue(String.valueOf(over.getWarnResults()), anyContains(over.getWarnResults(), "can supply only 150"));
	}

	// ----------------------------------------------------------------- food

	@Test
	public void foodRequirementClassicAndNewRules() {
		int[] hc = { 100, 0, 0, 0, 0, 0 };
		int[] lc = { 0, 100, 0, 0, 0, 0 };
		int[] hi = { 0, 0, 100, 0, 0, 0 };
		int[] li = { 0, 0, 0, 100, 0, 0 };
		int[] ar = { 0, 0, 0, 0, 100, 0 };
		int[] ma = { 0, 0, 0, 0, 0, 100 };
		assertEquals(200, Army.foodRequirement(hc, false));
		assertEquals(200, Army.foodRequirement(lc, false));
		assertEquals(100, Army.foodRequirement(hi, false));
		assertEquals(100, Army.foodRequirement(li, false));
		assertEquals(100, Army.foodRequirement(ar, false));
		assertEquals(100, Army.foodRequirement(ma, false));
		assertEquals(200, Army.foodRequirement(hc, true));
		assertEquals(0, Army.foodRequirement(lc, true));
		assertEquals(100, Army.foodRequirement(hi, true));
		assertEquals(0, Army.foodRequirement(li, true));
		assertEquals(100, Army.foodRequirement(ar, true));
		assertEquals(100, Army.foodRequirement(ma, true));
		int[] mixed = { 10, 20, 30, 40, 50, 60 };
		assertEquals(10 * 2 + 20 * 2 + 30 + 40 + 50 + 60, Army.foodRequirement(mixed, false));
		assertEquals(10 * 2 + 30 + 50 + 60, Army.foodRequirement(mixed, true));
	}

	private Army lightArmy(Nation nation, Character commander) {
		Army army = new Army(1234);
		army.setCommander(commander.getName());
		army.setNation(nation.getNation());
		army.setTroopAmount(100);
		army.setExtraInfo("100LI");
		nation.addArmy(army);
		return army;
	}

	@Test
	public void lightOnlyArmyNeedsNoFoodUnderNewRulesAndStillAsksForOneFood() {
		for (boolean newRules : new boolean[] { false, true }) {
			Main main = mainWithRules("850,MovArmy,ARMYMOVE,0,,,");
			main.setMap(new Map());
			main.setNewTroopRules(newRules);
			Nation nation = nationFor(main, 1);
			Character c = character(nation, "cmdr", "Commander");
			c.setLocation(1234);
			Army army = lightArmy(nation, c);
			order(c, 850, "no", "e");

			String error = nation.implementPhase(1, main);
			assertNull(error);
			assertEquals(newRules ? 0 : 100, army.getFoodRequirement());
			Vector requests = nation.getArmyRequests(main);
			nation.processArmyRequests(requests);
			nation.implementPhase(2, main);
			boolean asksOneFood = anyContainsBoxes(nation.getInfoRequests(), "have 1 food");
			assertEquals("light-only army must still be asked whether it has 1 food (new rules=" + newRules + ")", newRules,
					asksOneFood);
		}
	}

	private boolean anyContainsBoxes(Vector boxes, String text) {
		for (Object o : boxes) {
			if (((JCheckBox) o).getText().contains(text)) {
				return true;
			}
		}
		return false;
	}

	// -------------------------------------------------------- ruleset rows

	@Test
	public void natSellOnlyAtTheCapital() throws Exception {
		Main main = mainWithRealRules();
		Nation nation = nationFor(main, 1);
		popCenter(nation, 1234, 3, true);
		popCenter(nation, 2222, 2, false);
		Character c = character(nation, "sellr", "Seller");
		c.setLocation(2222);
		Order away = order(c, 325, "fo", "50");
		run(main);
		// 325 is after the first combat order, so a battle could have moved the capital: warning, not error
		assertFalse(String.valueOf(away.getErrorResults()), anyContains(away.getErrorResults(), "capital"));
		assertTrue(String.valueOf(away.getWarnResults()), anyContains(away.getWarnResults(), "is not at the capital (1234); the order needs the nation's capital, so it will fail unless the capital moves here this turn (e.g. after a battle)."));

		Main main2 = mainWithRealRules();
		Nation nation2 = nationFor(main2, 1);
		popCenter(nation2, 1234, 3, true);
		Character c2 = character(nation2, "sellr", "Seller");
		c2.setLocation(1234);
		Order at = order(c2, 325, "fo", "50");
		run(main2);
		assertEquals(String.valueOf(at.getErrorResults()), 0, at.getErrorResults().size());
	}

	@Test
	public void natTranIsJudgedAtTheLocationAfterAnEarlierMoveToTheCapital() {
		final String rules = "810,MovChar,CHARMOVE,1,12,0\n947,,RANK,0,5,0,0,0,1";
		// 810 moves the character to the capital before 947 resolves: no capital error.
		Main main = mainWithRules(rules);
		main.setMap(new Map());
		Nation nation = nationFor(main, 1);
		popCenter(nation, 1234, 3, true);
		Character c = character(nation, "tranr", "Transporter");
		c.setLocation(1236);
		order(c, 810, "1234");
		Order tran = order(c, 947, "2222", "fo", "50");
		run(main);
		assertFalse(String.valueOf(tran.getWarnResults()), anyContains(tran.getWarnResults(), "is not at the capital"));

		// control: no move, so the character is away from the capital when 947 resolves
		Main main2 = mainWithRules(rules);
		main2.setMap(new Map());
		Nation nation2 = nationFor(main2, 1);
		popCenter(nation2, 1234, 3, true);
		Character c2 = character(nation2, "tranr", "Transporter");
		c2.setLocation(1236);
		Order tran2 = order(c2, 947, "2222", "fo", "50");
		run(main2);
		assertTrue(anyContains(tran2.getWarnResults(), "is not at the capital"));
	}

	@Test
	public void caravanPriceOrdersNeedEmissaryAndCommandAtCapital() throws Exception {
		Main main = mainWithRealRules();
		Nation nation = nationFor(main, 1);
		popCenter(nation, 1234, 3, true);
		popCenter(nation, 2222, 2, false);
		Character c = character(nation, "price", "Pricer");
		c.setCommandRank(10);
		c.setLocation(2222);
		Order o = order(c, 960, "10");
		run(main);
		assertTrue(anyContains(o.getErrorResults(), "has no emissary rank"));
		int capitalWarnings = 0;
		for (Object e : o.getWarnResults()) {
			if (String.valueOf(e).contains("is not at the capital")) {
				capitalWarnings++;
			}
		}
		assertEquals("reported once even though 960 has two RANK rows", 1, capitalWarnings);
		assertFalse(anyContains(o.getErrorResults(), "Must be at capital"));
	}

	@Test
	public void atCapitalIsAnErrorBelowTheFirstCombatOrderAndAWarningFromIt() throws Exception {
		// 180 (below 230): certain failure, error. 325 (above): a battle may move the capital, warning.
		Main main = mainWithRealRules();
		Nation nation = nationFor(main, 1);
		popCenter(nation, 1234, 3, true);
		popCenter(nation, 2222, 2, false);
		Character c = character(nation, "upgrd", "Upgrader");
		c.setCommandRank(10);
		c.setLocation(2222);
		Order up = order(c, 180, "5");
		run(main);
		assertTrue(String.valueOf(up.getErrorResults()), anyContains(up.getErrorResults(), "Must be at capital"));
		assertFalse(anyContains(up.getWarnResults(), "is not at the capital"));
		assertEquals(230, Rule.FIRST_COMBAT_ORDER);
	}

	private Order conjure(String spellRow, String amount, int mageRank, boolean withArmy, Army[] armyOut) {
		Main main = mainWithRules("330,CstCjSp,SPELL,1,,,,\n" + spellRow);
		Nation nation = nationFor(main, 1);
		Character c = character(nation, "mageg", "Mage");
		c.setMageRank(mageRank);
		c.setLocation(1234);
		if (withArmy) {
			armyOut[0] = lightArmy(nation, c);
		}
		Order o = order(c, 330, spellRow.split(",")[0], amount);
		run(main);
		return o;
	}

	@Test
	public void conjureBlankZeroAndOverMaxAllConjureTheMaximum() {
		// spell row, natural mage rank 30, maximum = rank x factor
		String[][] cases = { { "508,Spell,MAGEAMT,2,5,0,", "150" }, { "510,Spell,MAGEAMT,2,25,1,", "750" },
				{ "512,Spell,MAGEAMT,2,5,2,", "150" } };
		for (String[] cs : cases) {
			String max = cs[1];
			for (String amount : new String[] { "-", "", "0", "9999", String.valueOf(Integer.parseInt(max) + 1) }) {
				Order o = conjure(cs[0], amount, 30, false, null);
				String label = cs[0] + " amount '" + amount + "'";
				assertFalse(label + " " + o.getErrorResults(), anyContains(o.getErrorResults(), "quantity"));
				assertFalse(label + " " + o.getErrorResults(), anyContains(o.getErrorResults(), "Maximum of"));
				assertTrue(label + " " + o.getInfoResults(), anyContains(o.getInfoResults(), max));
			}
			// below the maximum: no remark
			Order below = conjure(cs[0], "10", 30, false, null);
			assertFalse(anyContains(below.getInfoResults(), "maximum"));
		}
		Order junk = conjure("508,Spell,MAGEAMT,2,5,0,", "abc", 30, false, null);
		assertTrue(anyContains(junk.getErrorResults(), "Could not determine the quantity"));
	}

	@Test
	public void conjureHordesArmyContentIsTheMaximumForBlankZeroAndOverMax() {
		for (String amount : new String[] { "-", "0", "9999" }) {
			Army[] army = new Army[1];
			conjure("512,Spell,MAGEAMT,2,5,2,", amount, 30, true, army);
			assertEquals("men-at-arms for amount '" + amount + "'", 150, army[0].getTroopContent(9999)[5]);
		}
		Army[] army = new Army[1];
		conjure("512,Spell,MAGEAMT,2,5,2,", "40", 30, true, army);
		assertEquals(40, army[0].getTroopContent(9999)[5]);
	}

	@Test
	public void nameCharacterAcceptsUpTo30PointsInTens() {
		String rule = "725,NamChar,NEWCHAR,3,4,,";
		Main main = mainWithRules(rule);
		Nation nation = nationFor(main, 1);
		Character c = character(nation, "namer", "Namer");
		c.setCommandRank(30);
		Order ten = order(c, 725, "", "m", "10", "0", "0", "0");
		Order thirty = order(c, 725, "", "m", "10", "10", "10", "0");
		Order fifteen = order(c, 725, "", "m", "15", "15", "0", "0");
		Order over = order(c, 725, "", "m", "20", "20", "0", "0");
		run(main);
		assertEquals(String.valueOf(ten.getErrorResults()), 0, ten.getErrorResults().size());
		assertTrue(anyContains(ten.getInfoResults(), "20 unallocated points"));
		assertEquals(0, thirty.getErrorResults().size());
		assertTrue(anyContains(fifteen.getErrorResults(), "must be 0, 10, 20 or 30"));
		assertTrue(anyContains(over.getErrorResults(), "at most 30"));
	}

	// ------------------------------------------------- information, not alarm

	@Test
	public void lostListPurchaseAmountAndMovJoinRemarksAreNotErrorsOrWarnings() {
		Main main = mainWithRules("315,PrchCar,PRODUCTINFO,2,0,1,2,0");
		Nation nation = nationFor(main, 1);
		Character c = character(nation, "buyer", "Buyer");
		Order buy = order(c, 315, "fo", "10");
		run(main);
		assertEquals(0, buy.getWarnResults().size());
		assertEquals(0, buy.getErrorResults().size());
		assertTrue(anyContains(buy.getInfoResults(), "# of units is expected"));
	}

	@Test
	public void movJoinTargetNotAtStartHexIsOnlyAWarning() {
		Main main = mainWithRules("870,MovJoin,COMMANDER,2,1,0,2\n870,,COMMANDERNOT,0,0,0,5");
		Nation nation = nationFor(main, 1);
		Character mover = character(nation, "mover", "Mover");
		mover.setLocation(2000);
		Character target = character(nation, "targt", "Target");
		target.setLocation(2100);
		Order o = order(mover, 870, "2200", "targt");
		run(main);
		assertEquals(String.valueOf(o.getErrorResults()), 0, o.getErrorResults().size());
		assertTrue(anyContains(o.getWarnResults(), "unless the target moves or is hired (770)"));
	}

	// ------------------------------------------------------ 180 / 185 NATION

	/**
	 * True if order 180 or 185 given by nation 3 (alignment issuerAl) to nation 5 (alignment targetAl) fails because
	 * relations are already at the limit. Nation 9 is a Free People witness so the game is a team game unless
	 * allVsAll is true.
	 */
	private boolean limitError(int orderNo, int issuerAl, int targetAl, int relation, boolean allVsAll, String gameType) {
		Main main = mainWithRules("185,DnStNat,NATION,1,1,,,,,,,\n180,UpStNat,NATION,0,1,,,,,,,");
		Nation nation = nationFor(main, 3);
		main.getData().setGameType(gameType);
		nation.setGameType(gameType);
		for (int n = 1; n <= 25; n++) {
			nation.addNation("N" + n);
		}
		Data data = main.getData();
		data.findGame(nation);
		data.setNationAlignment(3 - 1, issuerAl, nation);
		data.setNationAlignment(5 - 1, targetAl, nation);
		if (allVsAll) {
			for (int n = 1; n <= 25; n++) {
				data.setNationAlignment(n - 1, NEU, nation);
			}
		} else {
			data.setNationAlignment(9 - 1, 1, nation);
		}
		nation.getRelations().setRelationsFor(5, relation);
		Character c = character(nation, "emiss", "Emissary");
		Order o = order(c, orderNo, "5");
		run(main);
		return anyContains(o.getErrorResults(), "already ");
	}

	private static final int FP = 1, DS = 2, NEU = 0;
	private static final int HATED = 0, DISLIKED = 1, NEUTRAL = 2, TOLERATED = 3, FRIENDLY = 4;

	@Test
	public void standardGameSameAllegianceBand() {
		// Neutral/Tolerated/Friendly: never Disliked/Hated
		assertTrue(limitError(180, FP, FP, FRIENDLY, false, "1650"));
		assertFalse(limitError(180, FP, FP, TOLERATED, false, "1650"));
		assertTrue(limitError(185, FP, FP, NEUTRAL, false, "1650"));
		assertFalse(limitError(185, FP, FP, TOLERATED, false, "1650"));
		assertTrue(limitError(185, DS, DS, NEUTRAL, false, "2950"));
	}

	@Test
	public void standardGameOppositeAllegianceBand() {
		// Hated/Disliked/Neutral: never Tolerated/Friendly
		assertTrue(limitError(180, FP, DS, NEUTRAL, false, "1650"));
		assertFalse(limitError(180, FP, DS, DISLIKED, false, "1650"));
		assertTrue(limitError(185, FP, DS, HATED, false, "1650"));
		assertFalse(limitError(185, FP, DS, DISLIKED, false, "1650"));
	}

	@Test
	public void attackNationNeedsADislikedOrHatedNation() {
		// 235 shares the NATION rule with 185 but is judged on the turn-start relation, not a floor
		Main m = mainWithRules("185,DnStNat,NATION,1,1,,,,,,,\n235,AttNat,NATION,1,1,,");
		Nation nation = nationFor(m, 3);
		for (int n = 1; n <= 25; n++) {
			nation.addNation("N" + n);
		}
		m.getData().findGame(nation);
		int[] rel = { TOLERATED, HATED, DISLIKED, NEUTRAL, FRIENDLY };
		Order[] orders = new Order[rel.length];
		Character c = character(nation, "attkr", "Attacker");
		for (int k = 0; k < rel.length; k++) {
			nation.getRelations().setRelationsFor(5 + k, rel[k]);
			orders[k] = order(c, 235, String.valueOf(5 + k));
		}
		order(c, 235, "20"); // relation unknown: no remark
		run(m);
		assertTrue(anyContains(orders[0].getErrorResults(), "Tolerated, not Disliked or Hated, so it cannot be attacked"));
		assertEquals(0, orders[1].getErrorResults().size());
		assertFalse(anyContains(orders[1].getErrorResults(), "worst allowed"));
		assertEquals(0, orders[2].getErrorResults().size());
		assertTrue(anyContains(orders[3].getErrorResults(), "cannot be attacked"));
		assertTrue(anyContains(orders[4].getErrorResults(), "cannot be attacked"));
		assertEquals(0, nation.getOrder(5).getErrorResults().size());
	}

	@Test
	public void attackNationAllowsForSameTurnRelationsOrders() {
		Main m = mainWithRules("180,UpStNat,NATION,0,1,,,,,,,\n185,DnStNat,NATION,1,1,,,,,,,\n235,AttNat,NATION,1,1,,");
		Nation nation = nationFor(m, 3);
		for (int n = 1; n <= 25; n++) {
			nation.addNation("N" + n);
		}
		m.getData().findGame(nation);
		nation.getRelations().setRelationsFor(5, TOLERATED); // two 185s take it to Disliked: warning
		nation.getRelations().setRelationsFor(6, TOLERATED); // none: error
		nation.getRelations().setRelationsFor(7, HATED); // one 180 lifts it only to Disliked: fine
		nation.getRelations().setRelationsFor(8, DISLIKED); // one 180 lifts it to Neutral: warning
		Character a = character(nation, "attka", "AttackerA");
		Character b = character(nation, "attkb", "AttackerB");
		Character d = character(nation, "attkd", "AttackerD");
		order(b, 185, "5");
		order(d, 185, "5");
		Order t5 = order(a, 235, "5");
		Order t6 = order(a, 235, "6");
		order(b, 180, "7");
		order(b, 180, "8");
		Order t7 = order(d, 235, "7");
		Order t8 = order(d, 235, "8");
		run(m);
		assertEquals(0, t5.getErrorResults().size());
		assertTrue(anyContains(t5.getWarnResults(), "can only be attacked if this turn's Downgrade Relations succeed"));
		assertTrue(anyContains(t6.getErrorResults(), "cannot be attacked"));
		assertEquals(0, t7.getErrorResults().size() + t7.getWarnResults().size());
		assertEquals(0, t8.getErrorResults().size());
		assertTrue(anyContains(t8.getWarnResults(), "may no longer be Disliked or Hated after this turn's Upgrade Relations"));
	}

	@Test
	public void bandsInvolvingNeutralNationsAreNotApplied() {
		// Neutral target or issuer: only the Friendly/Hated limits (1650 has no real neutrals; the data is unreliable)
		assertFalse(limitError(180, FP, NEU, TOLERATED, false, "1650"));
		assertTrue(limitError(180, FP, NEU, FRIENDLY, false, "1650"));
		assertFalse(limitError(185, DS, NEU, DISLIKED, false, "1650"));
		assertTrue(limitError(185, DS, NEU, HATED, false, "1650"));
		assertFalse(limitError(180, NEU, FP, TOLERATED, false, "1650"));
		assertFalse(limitError(185, NEU, DS, DISLIKED, false, "1650"));
		assertTrue(limitError(185, NEU, DS, HATED, false, "1650"));
	}

	@Test
	public void bandsAreSkippedWhenTheIssuersRelationsContradictThem() {
		// nation 3 (FP) is Disliked towards a same-allegiance nation (7): the alignments do not describe this game,
		// so a downgrade at Neutral towards FP nation 5 is not flagged (the floor would be Neutral).
		for (boolean outOfBand : new boolean[] { false, true }) {
			Main main = mainWithRules("185,DnStNat,NATION,1,1,,,,,,,");
			Nation nation = nationFor(main, 3);
			for (int n = 1; n <= 25; n++) {
				nation.addNation("N" + n);
			}
			main.getData().findGame(nation);
			main.getData().setNationAlignment(2, FP, nation);
			main.getData().setNationAlignment(4, FP, nation);
			main.getData().setNationAlignment(6, FP, nation);
			main.getData().setNationAlignment(8, DS, nation);
			nation.getRelations().setRelationsFor(5, NEUTRAL);
			nation.getRelations().setRelationsFor(7, outOfBand ? DISLIKED : TOLERATED);
			Character c = character(nation, "emiss", "Emissary");
			Order o = order(c, 185, "5");
			run(main);
			assertEquals("out of band relation present: " + outOfBand, !outOfBand, anyContains(o.getErrorResults(), "already "));
		}
	}

	@Test
	public void allVsAllOnlyHasTheFriendlyAndHatedLimits() {
		assertFalse(limitError(180, NEU, NEU, TOLERATED, true, "1650"));
		assertTrue(limitError(180, NEU, NEU, FRIENDLY, true, "1650"));
		assertFalse(limitError(185, NEU, NEU, DISLIKED, true, "1650"));
		assertFalse(limitError(185, NEU, NEU, NEUTRAL, true, "1650"));
		assertTrue(limitError(185, NEU, NEU, HATED, true, "1650"));
	}

	@Test
	public void fourthAgeFriendlyOnlyWithinAllegianceAndHatedOnlyAcross() {
		String fa = "Fourth Age";
		assertTrue(limitError(180, FP, FP, FRIENDLY, false, fa));
		assertFalse(limitError(180, FP, FP, TOLERATED, false, fa));
		assertTrue(limitError(185, FP, FP, DISLIKED, false, fa));
		assertFalse(limitError(185, FP, FP, NEUTRAL, false, fa));
		// across (a Neutral nation is its own allegiance): Friendly never, Hated allowed
		assertTrue(limitError(180, FP, DS, TOLERATED, false, fa));
		assertFalse(limitError(180, FP, DS, DISLIKED, false, fa));
		assertTrue(limitError(185, FP, DS, HATED, false, fa));
		assertFalse(limitError(180, FP, NEU, TOLERATED, false, fa));
	}

	@Test
	public void otherGameTypesOnlyHaveTheFriendlyAndHatedLimits() {
		assertFalse(limitError(180, FP, DS, TOLERATED, false, "Kin Strife"));
		assertTrue(limitError(180, FP, DS, FRIENDLY, false, "Kin Strife"));
		assertFalse(limitError(185, FP, FP, DISLIKED, false, "BOFA"));
		assertTrue(limitError(185, FP, FP, HATED, false, "BOFA"));
	}

	@Test
	public void limitMessageNamesTheLevelAndAllegiances() {
		Main main = mainWithRules("180,UpStNat,NATION,0,1,,,,,,,");
		Nation nation = nationFor(main, 3);
		for (int n = 1; n <= 25; n++) {
			nation.addNation("N" + n);
		}
		main.getData().findGame(nation);
		main.getData().setNationAlignment(2, FP, nation);
		main.getData().setNationAlignment(4, DS, nation);
		nation.getRelations().setRelationsFor(5, NEUTRAL);
		Character c = character(nation, "emiss", "Emissary");
		Order o = order(c, 180, "5");
		run(main);
		assertTrue(String.valueOf(o.getErrorResults()), anyContains(o.getErrorResults(),
				"are already Neutral, the best allowed between Free People and Dark Servant nations."));
	}

	// ---------------------------------------------------------- RANK fixes

	@Test
	public void duplicateMovementOrdersAreDetectedWithTheCorrectIndex() {
		// 850 and 870 are movement orders. Before the fix, otherOrder was compared against movementOrders[i]
		// (the outer index), so only order pairs that happened to line up were found.
		Main main = mainWithRules("850,MovArmy,RANK,0,5,0,0,1,0\n870,MovJoin,RANK,0,5,0,0,1,0\n830,MovNavy,RANK,0,5,0,0,1,0");
		Nation nation = nationFor(main, 1);
		Character c = character(nation, "movr", "Mover");
		Order a = order(c, 870, "2200", "x");
		Order b = order(c, 830);
		run(main);
		assertTrue(String.valueOf(a.getErrorResults()), anyContains(a.getErrorResults(), "Two skill, spell or movement orders"));
		assertTrue(anyContains(b.getErrorResults(), "Two skill, spell or movement orders"));
	}

	@Test
	public void realRulesetHasTheCorrectedRows() throws Exception {
		Main main = mainWithRealRules();
		Ruleset rs = main.getRuleSet();
		// 265 was a typo for 260
		assertEquals(0, rs.getRulesForOrder(265, false).size());
		assertTrue(countRules(rs, 260, "RANK") == 1);
		// 605 had a duplicate RANK row
		assertEquals(1, countRules(rs, 605, "RANK"));
		// 610 gained its agent skill row
		assertEquals(1, countRules(rs, 610, "RANK"));
		// 960 / 965 need emissary and command rank
		assertEquals(2, countRules(rs, 960, "RANK"));
		assertEquals(2, countRules(rs, 965, "RANK"));
		for (int no : new int[] { 280, 325, 660, 947 }) {
			assertEquals("order " + no, 1, countRules(rs, no, "RANK"));
		}
		for (int no : new int[] { 625, 640, 645, 650, 655 }) {
			assertEquals("order " + no, 1, countRules(rs, no, "RANK"));
		}
	}

	private int countRules(Ruleset rs, int order, String name) {
		Ruleset rules = rs.getRulesForOrder(order, false);
		int n = 0;
		for (int i = 0; i < rules.size(); i++) {
			if (((Rule) rules.get(i)).getName().equalsIgnoreCase(name)) {
				n++;
			}
		}
		return n;
	}

	@Test
	public void flagDefaultsOffAndIsSettable() {
		Main main = new Main(true, new Data());
		assertFalse(main.isNewTroopRules());
		main.setNewTroopRules(true);
		assertTrue(main.isNewTroopRules());
	}
}
