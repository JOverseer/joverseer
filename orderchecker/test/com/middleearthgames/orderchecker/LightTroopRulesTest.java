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

/** Tests for the 31 Oct 2026 troop rules switch of the legacy order checker (recruit pools and food). */
public class LightTroopRulesTest {

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
	public void classicPoolIsSizeTimes100AndOverTheCapIsAnError() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, false, 1);
		Order a = order(c, 408, "60");
		Order b = order(c, 408, "60");
		run(main);
		assertEquals(0, a.getErrorResults().size());
		assertTrue(anyContains(a.getWarnResults(), "still has 40 troops available"));
		assertTrue(String.valueOf(b.getErrorResults()), anyContains(b.getErrorResults(), "is out of troops!  Maximum 100 available."));
	}

	@Test
	public void classicLightInfantryGetsNoExtraAllowance() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, false, 1);
		Order a = order(c, 412, "150");
		run(main);
		assertTrue(anyContains(a.getErrorResults(), "Maximum 100 available."));
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
	public void newRules151stLightInfantryIsAnErrorNamingThePerTypeMaximum() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order a = order(c, 412, "151");
		run(main);
		assertTrue(String.valueOf(a.getErrorResults()), anyContains(a.getErrorResults(), "Maximum 150 Light Infantry available"));
	}

	@Test
	public void newRulesSharedPool75LightPlus50HeavyFillsCamp() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order li = order(c, 412, "75");
		Order hi = order(c, 408, "50");
		run(main);
		assertEquals("75 LI + 50 HI exactly fills a camp", 0, li.getErrorResults().size() + hi.getErrorResults().size());
		// 408 resolves first: 50 HI leaves room for 50 more heavy troops
		assertTrue(anyContains(hi.getWarnResults(), "still has 50 troops available"));
		assertEquals(0, li.getWarnResults().size());
	}

	@Test
	public void newRules100HeavyPlusOneLightGoesOverTheCap() {
		Main main = mainWithRules(RECRUIT_RULES);
		Character c = recruiter(main, true, 1);
		Order hi = order(c, 408, "100");
		Order li = order(c, 412, "1");
		run(main);
		assertEquals(0, hi.getErrorResults().size());
		assertTrue(String.valueOf(li.getErrorResults()), anyContains(li.getErrorResults(), "Maximum 150 Light Infantry available"));
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

		Main main2 = mainWithRules("770,,TROOPRECRUIT,1,2,0,5");
		Character c2 = recruiter(main2, true, 1);
		Order over = order(c2, 770, "151", "LC");
		run(main2);
		assertTrue(String.valueOf(over.getErrorResults()), anyContains(over.getErrorResults(), "Maximum 150 Light Cavalry available"));
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


	@Test
	public void flagDefaultsOffAndIsSettable() {
		Main main = new Main(true, new Data());
		assertFalse(main.isNewTroopRules());
		main.setNewTroopRules(true);
		assertTrue(main.isNewTroopRules());
	}
}
