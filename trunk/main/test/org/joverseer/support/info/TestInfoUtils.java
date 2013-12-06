package org.joverseer.support.info;

import org.joverseer.domain.ArmyElementType;

import junit.framework.TestCase;

import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.JOverseerJIDEClient;

public class TestInfoUtils extends TestCase {

	public void testGetTroopStrength() {
		String strengthType = "Defense" ;
		ArmyElementType armyElementType = ArmyElementType.HeavyCavalry ;
		Integer troopStrength = InfoUtils.getTroopStrength(armyElementType, strengthType) ;
		assertEquals(16, troopStrength.intValue()) ;
	}
	
	@Override
	public void setUp() {
		JOverseerJIDEClient.launchTestFramework() ;
	}
}
