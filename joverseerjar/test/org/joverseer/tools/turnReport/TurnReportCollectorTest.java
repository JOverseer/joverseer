package org.joverseer.tools.turnReport;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TurnReportCollectorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testTurnReportCollector() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectNatSells() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectNonFriendlyChars() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectDragons() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectSpells() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectAgentActions() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectChallenges() {
		GameHolder gh=null;
		Turn t=null;
		TurnReportCollector coll = new TurnReportCollector(gh);
		boolean thrown=false;
		try {
			ArrayList<BaseReportObject> reports = coll.CollectCombats(t);
		} catch (Exception e) {
			thrown =true;
		}
		gh = new GameHolder();
		Game g = new Game();

		assertTrue("expecting exception from null parameter.",thrown);
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectCharacters() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectPopCenters() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetPopCenterReport() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectEncounters() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectCompanies() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectArtifacts() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetArtifactOwner() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectTransports() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectGoldSteals() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectNations() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectUpcomingCombats() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetArmyDescription() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectCombats() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetPopInfo() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCollectBridges() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetAdvCharWrapperStr() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testPopPlusNationPopulationCenter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testPopPlusNationStringTurn() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCharPlusNationCharacter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCharPlusNationStringTurn() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testNamePlusNation() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderCollectionStringArrayListOfBaseReportObject() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderCollectionStringStringArrayListOfBaseReportObject() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderStealsSummary() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderCharsSummary() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderPopsSummary() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFilterReports() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testShortenPopDocks() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testShortenPopSize() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testShortenPopFort() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRenderReport() {
		fail("Not yet implemented"); // TODO
	}

}
