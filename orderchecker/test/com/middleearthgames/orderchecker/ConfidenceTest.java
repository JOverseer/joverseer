package com.middleearthgames.orderchecker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import com.middleearthgames.orderchecker.io.Data;
import com.middleearthgames.orderchecker.io.ImportRulesCsv;

public class ConfidenceTest {
	final static String RANK_RULE ="RANK,0";
	final static String COMMAND_RANK_MIN=",0,1";
	final static String AGENT_RANK_MIN=",1,1";
	final static String EMISSARY_RANK_MIN=",2,1";
	final static String MAGE_RANK_MIN=",3,1";
	final static String EXCLUSIVE = ",1";
	final static String NOT_EXCLUSIVE = ",0";
	final static String AT_CAPITAL=",1";
	final static String ANYWHERE=",0";
	@Test
	public void readRulesTest() {
		Data data = new Data();
		final Main main = new Main(true,data);
		Main.main = main;
		Ruleset ruleSet = new Ruleset();
		main.setRuleSet(ruleSet);
		ImportRulesCsv rules = new ImportRulesCsv("ruleset.csv", ruleSet);
		boolean result = rules.getRules();
		if (!result) {
			// maybe we're running under eclipse.
			rules.closeFile();
			rules = new ImportRulesCsv("bin/metadata/orderchecker/ruleset.csv", ruleSet);
			result = rules.getRules();
			assertTrue("Failed to read ruleset", result);
		}
		String error = rules.parseRules();
		rules.closeFile();
		assertNull(error);
	}

	//use ByteArrayInputStream to get the bytes of the String and convert them to InputStream.
	private InputStreamReader makeStreamReader(String s) {
        InputStream is = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));
        return new InputStreamReader(is);
	}

	private void invokeOrderchecker(Main main) {
		String error = main.getNation().implementPhase(1, main);
		assertNull(error);

		boolean done;
		int safety;
		Vector requests = main.getNation().getArmyRequests(main);
		main.getNation().processArmyRequests( requests );
		done = false;
		safety = 0;
		do {
			if (done || safety >= 20) {
				break;
			}
			safety++;
			error = main.getNation().implementPhase( 2, main );
			assertNull(error);
			requests = main.getNation().getInfoRequests();
//			parseInfoRequests(requests);
			if (requests.size() > 0) {
//				this.infoRequests(main, requests); //hook
			}
			done = main.getNation().isProcessingDone();
		} while (true);
		assertNotEquals("order checking stuck in loop",20, safety);

	}
	/**
	 * Dont end the string with \r\n
	 * @param rulesAsSingleString
	 * @return
	 */
	private Main createRuleset(String rulesAsSingleString) {
		Data data = new Data();
		final Main main = new Main(true,data);
		Ruleset ruleSet = new Ruleset();
		main.setRuleSet(ruleSet);
		ImportRulesCsv rules = new ImportRulesCsv("", ruleSet);
		boolean result = rules.openStream(makeStreamReader(rules.makeRuleString(rulesAsSingleString)));
		assertTrue(result);
		String error = rules.parseRules();
		rules.closeFile();
		assertNull(error);
		return main;
	}
	private Nation createMainOrderNation(Main main) {
		Nation nation = new Nation();
		main.setNation(nation);
		main.getData().setGameType("1650");
		nation.setGameType("1650");
		return nation;
	}
	/**
	 * Create character and add it to the nation.
	 * @param nation
	 * @param id
	 * @param name
	 * @return
	 */
	private com.middleearthgames.orderchecker.Character createCharacter(Nation nation,String id,String name) {
		com.middleearthgames.orderchecker.Character character = new com.middleearthgames.orderchecker.Character(id);
		character.setName(name);
		nation.addCharacter(character);
		return character;
	}
	private com.middleearthgames.orderchecker.PopCenter createPopCenter(Nation nation,int location) {
		com.middleearthgames.orderchecker.PopCenter popCenter = new com.middleearthgames.orderchecker.PopCenter(location);
		popCenter.setNation(nation.getNation());
		return popCenter;
		
	}
	private void assertOrderResultsCounts(Order order,int error,int help,int info,int warning) {
		String message;
		assertTrue("No rules applied",order.getRules().size() > 0);
		if (error != order.getErrorResults().size() ) {
			message = "Error results count ";
			if (error == 0) {
				fail(message + order.getErrorResults().get(0));
			}
		}
		assertEquals("Error results count",error,order.getErrorResults().size());
		assertEquals("Help results count",help,order.getHelpResults().size());
		assertEquals("Info results count",info,order.getInfoResults().size());
		assertEquals("Warning results count",warning,order.getWarnResults().size());
	}
	@Test
	public void simpleTest() {
		final Main main = createRuleset("605,GrdLoc,PC,0,1,\n" +
			    "605,,"+RANK_RULE+AGENT_RANK_MIN+",0,1"+ANYWHERE);

		Main.main = main;

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testc","no name");

		Order order1= new Order(character,605);
		Order order2= new Order(character,605);
		character.addOrder(order1);
		character.addOrder(order2);

		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),3,0,0,1);
		assertEquals("605 error","Could not determine the location.",order1.getErrorResults().get(0));
		assertEquals("605 error","no name has no agent rank.",order1.getErrorResults().get(1));
		assertEquals("605 error","Can't have a duplicate order",order1.getErrorResults().get(2));
		assertEquals("605 warning","no name should have at least a agent rank of 1.",order1.getWarnResults().get(0));
		assertOrderResultsCounts(nation.getOrder(1),3,0,0,1);
	}

	@Test
	public void agentTest() {
		final Main main = createRuleset("605,GrdLoc,PC,0,1,\n" +
			    "605,,"+RANK_RULE+AGENT_RANK_MIN+",0"+EXCLUSIVE+ANYWHERE);

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testA","Agent Orange");
		Order order1= new Order(character,605);
		character.addOrder(order1);

		invokeOrderchecker(main);

		assertOrderResultsCounts(order1,2,0,0,1);
		assertEquals("605 error","Could not determine the location.",order1.getErrorResults().get(0));
		assertEquals("605 error","Agent Orange has no agent rank.",order1.getErrorResults().get(1));
		assertEquals("605 warning","Agent Orange should have at least a agent rank of 1.",order1.getWarnResults().get(0));

		character.setAgentRank(1);
		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),1,0,0,0);
		assertEquals("605 error","Could not determine the location.",order1.getErrorResults().get(0));
	}
	@Test
	public void commandTest() {
		final Main main = createRuleset("745,CreCmpy,COMMANDERNOT,0,0,0,5\r\n" +
				"745,,LAND,0,,,\r\n" +
				"745,,SETCOMMAND,0,1,1,"
//				+ "745,," + RANK_RULE + COMMAND_RANK_MIN + ",0" + EXCLUSIVE + ANYWHERE) // RANK is implied by SETCOMMAND
				);

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testC","Commander Orange");
		Order order1= new Order(character,745);
		character.addOrder(order1);

		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),3,0,0,0);
		assertEquals("745 error","Could not determine the location.",order1.getErrorResults().get(0));
		assertEquals("745 error","Could not determine the location.",order1.getErrorResults().get(1));
		assertEquals("745 error","Commander Orange has no command rank.",order1.getErrorResults().get(2));

		character.setCommandRank(1);
		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),2,0,0,0);
		assertEquals("745 error","Could not determine the location.",order1.getErrorResults().get(0));
		assertEquals("745 error","Could not determine the location.",order1.getErrorResults().get(1));
	}
	@Test
	public void emissaryTest() {
		final Main main = createRuleset("585,Uncover," + RANK_RULE + EMISSARY_RANK_MIN + ",0" + EXCLUSIVE + ANYWHERE + "\n"
				+ "555,,RANK,0,2,30,1,1,0");

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testA","Emmy Orange");
		Order order1= new Order(character,585);
		character.addOrder(order1);

		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),1,0,0,1);
		assertEquals("585 error","Emmy Orange has no emissary rank.",order1.getErrorResults().get(0));
		assertEquals("585 warning","Emmy Orange should have at least a emissary rank of 1.",order1.getWarnResults().get(0));

		character.setEmissaryRank(1);
		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),0,0,0,0);

		character.getOrders().clear();
		order1 = new Order(character,555);
		Order order2 = new Order(character,810);
		order2.addParameter("2115");
		character.addOrder(order1);
		character.addOrder(order2);
		invokeOrderchecker(main);
		assertOrderResultsCounts(nation.getOrder(0),0,0,0,1);
		assertEquals("555 warning","New PC will have low loyalty if emissary rank is less than 30.",order1.getWarnResults().get(0));
	}
	@Test
	public void mageTest() {
		final Main main = createRuleset("710,PrenMgy,PC,0,0,,\r\n" +
				"710,," + RANK_RULE + MAGE_RANK_MIN + ",0" + EXCLUSIVE + ANYWHERE);

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testA","Agent Orange");
		Order order1= new Order(character,710);
		character.addOrder(order1);

		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),2,0,0,1);
		assertEquals("710 error","Could not determine the location.",order1.getErrorResults().get(0));
		assertEquals("710 error","Agent Orange has no mage rank.",order1.getErrorResults().get(1));
		assertEquals("710 warning","Agent Orange should have at least a mage rank of 1.",order1.getWarnResults().get(0));

		character.setMageRank(1);
		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),1,0,0,0);
		assertEquals("710 error","Could not determine the location.",order1.getErrorResults().get(0));
	}
	@Test
	public void TwoCommandOrderTest() {
		final Main main = createRuleset(
				"185,DnStNat,NATION,1,1,,,,,,,"+"\r\n" +
				"185,,RANK,0,0,1,1,1,1"+"\r\n"+
				"725,NamChar,NEWCHAR,3,4,,"+"\r\n"+
				"725,,RANK,0,0,1,1,1,1"+"\r\n"
				);

		Nation nation = createMainOrderNation(main);

		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testC","Commander Orange");
		Order order1= new Order(character,185);
		order1.addParameter("1");
		character.addOrder(order1);
		Order order2= new Order(character,725);
		order2.addParameter("");
		order2.addParameter("m");
		order2.addParameter("30");
		order2.addParameter("0");
		order2.addParameter("0");
		order2.addParameter("0");
		character.addOrder(order2);
		character.setCommandRank(30);

		invokeOrderchecker(main);

		assertOrderResultsCounts(nation.getOrder(0),1,1,0,0);
		assertEquals("185 error","Duplicate order skill",order1.getErrorResults().get(0));
		assertEquals("185 error"," (Free People)",order1.getHelpResults().get(0));
		assertOrderResultsCounts(nation.getOrder(1),1,1,0,0);
		assertEquals("725 error","Duplicate order skill",order2.getErrorResults().get(0));
		assertEquals("725 error","Creating new character: 30 Command.",order2.getHelpResults().get(0));

	}
	@Test
	public void GoldToHiddenPCTest() {
		final Main main = createRuleset(
				"948,TranCar,SIEGENOT,0,1,,,"+"\r\n" +
				"948,,SIEGENOT,1,0,,,"+"\r\n" +
				"948,,SIEGENOT,2,0,,,"+"\r\n" +
				"948,,PC,0,0,,,"+"\r\n" +
				"948,,PC,1,0,,,"+"\r\n" +
				"948,,PC,2,11,3,,"+"\r\n" +
				"948,,PRODUCTINFO,0,2,3,4,0"+"\r\n"
				);
		Nation nation = createMainOrderNation(main);
		nation.SetNation(1);
		com.middleearthgames.orderchecker.PopCenter popcenter = createPopCenter(nation, 1234);
		popcenter.setCapital(1);
		popcenter.setName("Bree");
		popcenter.setNewPC(); // stop the default string generation.
		popcenter.setFortification(com.middleearthgames.orderchecker.PopCenter.FORT_CASTLE);
		assertEquals("setup failure","1234",popcenter.toString());
		com.middleearthgames.orderchecker.PopCenter targetPopcenter = createPopCenter(nation, 1111);
		targetPopcenter.setNewPC(); // stop the default string generation.
		nation.setCapital(popcenter.getLocation());
		nation.addPopulationCenter(popcenter);
		nation.addPopulationCenter(targetPopcenter);
		assertEquals("setup failure","1111",targetPopcenter.toString());
		com.middleearthgames.orderchecker.Character character = createCharacter(nation,"testA","Agent Orange");
		Order order= new Order(character,948);
		order.addParameter("1234");
		order.addParameter("1111");
		order.addParameter("go");
		order.addParameter("15000");
		character.addOrder(order);
		character.setLocation(popcenter.getLocation());
		character.setNation(nation.getNation());

		invokeOrderchecker(main);
		assertOrderResultsCounts(nation.getOrder(0),0,1,0,0);
		assertEquals("948 error same nation","Transporting 15000 Gold to 1111.",order.getHelpResults().get(0));
		
		// move the target to another nation
		Nation nation2 = new Nation();
		nation2.SetNation(2);
		assertNotEquals("948 nation",nation.getNation(),nation2.getNation());
		targetPopcenter.setNation(nation2.getNation());

		invokeOrderchecker(main);
		assertOrderResultsCounts(nation.getOrder(0),0,1,0,0);
		assertEquals("948 error other nation","Transporting 15000 Gold to 1111.",order.getHelpResults().get(0));
		
		// now hidden
		targetPopcenter.setHidden(1);
		
		invokeOrderchecker(main);
		assertOrderResultsCounts(nation.getOrder(0),0,1,0,0);
		assertEquals("948 error hidden","Transporting 15000 Gold to 1111.",order.getHelpResults().get(0));
		
		
	}

}
