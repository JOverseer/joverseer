package org.joverseer.tools.combatCalc;

import org.joverseer.ui.JOverseerJIDEClient;

import junit.framework.TestCase;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;

public class TestCombat  extends TestCase  {

	public void testCombat() {
		TestCombat combat = new TestCombat() ;
		assertNotNull(combat) ;
	}
	
/**
 * Grasty (CL) vs Tarondor (NG) at 2928
 * Cloud Lord will be considered side 1.
Climate: Hot
Terrain: Shore

Tarondor challenged Grasty and was refused.

Tactics
Standard tactic for both armies

Relations
CL hates NG
NG dislikes CL


Result:
Grasty won and was left with 283HC, 283LC, 395HI, 283LI, 283AR, 283MA
 */
	public void testArmyBattleOneOnOne() {
		Combat combat = new Combat() ;
		final int maxAll = Combat.MAX_ALL ;
		combat.setClimate(ClimateEnum.Hot) ;
		combat.setHexNo(2928) ;
		combat.setDescription("Test of One on One combat") ;
		combat.setAttackPopCenter(false) ;
		
	    NationRelationsEnum[][] side1Relations = combat.getSide1Relations() ;
	    NationRelationsEnum[][] side2Relations = combat.getSide2Relations() ;
	    
//	    side1Relations[5][13] = NationRelationsEnum.Hated ; // This should set Cloud to hate North Gondor.
//	    side2Relations[13][5] = NationRelationsEnum.Hated ; // This should set Cloud to hate North Gondor.
	    // All other relations are disliked by default so North Gondor's dislike of Cloud is all set.
	    
		combat.setSide1Relations(side1Relations) ;
		combat.setSide2Relations(side2Relations) ;
		
	    CombatArmy cloudArmy = new CombatArmy();
	    cloudArmy.setNationNo(14);
	    cloudArmy.setCommandRank(44);
	    cloudArmy.setCommander("Grasty");
	    cloudArmy.setMorale(71);
	    cloudArmy.setDefensiveAddOns(0);
	    cloudArmy.setOffensiveAddOns(0);

	    ArmyElement heavyCavalryElement = cloudArmy.getHC() ;
	    heavyCavalryElement.setNumber(499);
	    heavyCavalryElement.setTraining(35);
	    heavyCavalryElement.setWeapons(30);
	    heavyCavalryElement.setArmor(10);

	    ArmyElement lightCavalryElement = cloudArmy.getLC() ;
	    lightCavalryElement.setNumber(499);
	    lightCavalryElement.setTraining(30);
	    lightCavalryElement.setWeapons(30);
	    lightCavalryElement.setArmor(10);
	    
	    ArmyElement heavyInfantryElement = cloudArmy.getHI() ;
	    heavyInfantryElement.setNumber(697);
	    heavyInfantryElement.setTraining(23);
	    heavyInfantryElement.setWeapons(24);
	    heavyInfantryElement.setArmor(7);
	    
	    ArmyElement LightInfantryElement = cloudArmy.getLI() ;
	    LightInfantryElement.setNumber(499);
	    LightInfantryElement.setTraining(30);
	    LightInfantryElement.setWeapons(30);
	    LightInfantryElement.setArmor(10);
	    
	    ArmyElement archersElement = cloudArmy.getAR() ;
	    archersElement.setNumber(499);
	    archersElement.setTraining(31);
	    archersElement.setWeapons(60);
	    archersElement.setArmor(0);
	    
	    ArmyElement menAtArmsElement = cloudArmy.getMA() ;
	    menAtArmsElement.setNumber(499);
	    menAtArmsElement.setTraining(30);
	    menAtArmsElement.setWeapons(30);
	    menAtArmsElement.setArmor(10);
		
	    CombatArmy[] side1 = combat.getSide1() ;
	    side1[0] = cloudArmy ;
	    combat.setSide1(side1) ;

	    CombatArmy northGondorArmy = new CombatArmy();
	    northGondorArmy.setNationNo(6);
	    northGondorArmy.setCommandRank(95);
	    northGondorArmy.setCommander("Tarondor");
	    northGondorArmy.setMorale(18);
	    northGondorArmy.setDefensiveAddOns(0);
	    northGondorArmy.setOffensiveAddOns(0);
	    
	    ArmyElement heavyCavalryElement2 = northGondorArmy.getHC() ;
	    heavyCavalryElement2.setNumber(1000);
	    heavyCavalryElement2.setTraining(22);
	    heavyCavalryElement2.setWeapons(10);
	    heavyCavalryElement2.setArmor(5);
	    
	    CombatArmy[] side2 = combat.getSide2() ;
	    side2[0] = northGondorArmy ;
	    combat.setSide2(side2) ;
	    
	    combat.runArmyBattle() ;
	    
	    CombatArmy resultingCloudArmy = combat.getSide1()[0] ;
	    CombatArmy resultingNorthGondorArmy = combat.getSide2()[0] ;
	    
	    assertEquals(283, resultingCloudArmy.getHC().getNumber()) ;
	    assertEquals(0, resultingNorthGondorArmy.getHC().getNumber()) ;
	    
		fail("incomplete test") ;
	}

// Removed to simplify the output. Eventually use to make some other battle.	
//	public void testSecondCombatTest() {
//		Combat combat = new Combat() ;
//		final int maxAll = Combat.MAX_ALL ;
//		combat.setClimate(ClimateEnum.Hot) ;
//		combat.setHexNo(2928) ;
//		combat.setDescription("Test of One on One combat") ;
//		combat.setAttackPopCenter(false) ;
//		
//	    NationRelationsEnum[][] side1Relations = combat.getSide1Relations() ;
//	    NationRelationsEnum[][] side2Relations = combat.getSide2Relations() ;
//	    
////	    side1Relations[5][13] = NationRelationsEnum.Hated ; // This should set Cloud to hate North Gondor.
////	    side2Relations[13][5] = NationRelationsEnum.Hated ; // This should set Cloud to hate North Gondor.
//	    // All other relations are disliked by default so North Gondor's dislike of Cloud is all set.
//	    
//		combat.setSide1Relations(side1Relations) ;
//		combat.setSide2Relations(side2Relations) ;
//		
//	    CombatArmy cloudArmy = new CombatArmy();
//	    cloudArmy.setNationNo(14);
//	    cloudArmy.setCommandRank(44);
//	    cloudArmy.setCommander("Grasty");
//	    cloudArmy.setMorale(71);
//	    cloudArmy.setDefensiveAddOns(0);
//	    cloudArmy.setOffensiveAddOns(0);
//
//	    ArmyElement heavyCavalryElement = new ArmyElement();
//	    heavyCavalryElement.setArmyElementType(ArmyElementType.HeavyCavalry);
//	    heavyCavalryElement.setNumber(499);
//	    heavyCavalryElement.setTraining(35);
//	    heavyCavalryElement.setWeapons(30);
//	    heavyCavalryElement.setArmor(10);
//	    cloudArmy.addElement(heavyCavalryElement);
//
//	    ArmyElement lightCavalryElement = new ArmyElement();
//	    lightCavalryElement.setArmyElementType(ArmyElementType.LightCavalry);
//	    lightCavalryElement.setNumber(499);
//	    lightCavalryElement.setTraining(30);
//	    lightCavalryElement.setWeapons(30);
//	    lightCavalryElement.setArmor(10);
//	    cloudArmy.addElement(lightCavalryElement);
//	    
//	    ArmyElement heavyInfantryElement = new ArmyElement();
//	    heavyInfantryElement.setArmyElementType(ArmyElementType.HeavyInfantry);
//	    heavyInfantryElement.setNumber(697);
//	    heavyInfantryElement.setTraining(23);
//	    heavyInfantryElement.setWeapons(24);
//	    heavyInfantryElement.setArmor(7);
//	    cloudArmy.addElement(heavyInfantryElement);
//	    
//	    ArmyElement LightInfantryElement = new ArmyElement();
//	    LightInfantryElement.setArmyElementType(ArmyElementType.LightInfantry);
//	    LightInfantryElement.setNumber(499);
//	    LightInfantryElement.setTraining(30);
//	    LightInfantryElement.setWeapons(30);
//	    LightInfantryElement.setArmor(10);
//	    cloudArmy.addElement(LightInfantryElement);
//	    
//	    ArmyElement archersElement = new ArmyElement();
//	    archersElement.setArmyElementType(ArmyElementType.Archers);
//	    archersElement.setNumber(499);
//	    archersElement.setTraining(31);
//	    archersElement.setWeapons(60);
//	    archersElement.setArmor(0);
//	    cloudArmy.addElement(archersElement);
//	    
//	    ArmyElement menAtArmsElement = new ArmyElement();
//	    menAtArmsElement.setArmyElementType(ArmyElementType.MenAtArms);
//	    menAtArmsElement.setNumber(499);
//	    menAtArmsElement.setTraining(30);
//	    menAtArmsElement.setWeapons(30);
//	    menAtArmsElement.setArmor(10);
//	    cloudArmy.addElement(menAtArmsElement);
//		
//	    CombatArmy[] side1 = combat.getSide1() ;
//	    side1[0] = cloudArmy ;
//	    combat.setSide1(side1) ;
//
//	    CombatArmy northGondorArmy = new CombatArmy();
//	    northGondorArmy.setNationNo(6);
//	    northGondorArmy.setCommandRank(95);
//	    northGondorArmy.setCommander("Tarondor");
//	    northGondorArmy.setMorale(18);
//	    northGondorArmy.setDefensiveAddOns(0);
//	    northGondorArmy.setOffensiveAddOns(0);
//	    
//	    ArmyElement heavyCavalryElement2 ;
//	    heavyCavalryElement2 = new ArmyElement();
//	    heavyCavalryElement2.setArmyElementType(ArmyElementType.HeavyCavalry);
//	    heavyCavalryElement2.setNumber(1000);
//	    heavyCavalryElement2.setTraining(22);
//	    heavyCavalryElement2.setWeapons(10);
//	    heavyCavalryElement2.setArmor(5);
//	    northGondorArmy.addElement(heavyCavalryElement2);
//	    
//	    CombatArmy[] side2 = combat.getSide2() ;
//	    side2[0] = northGondorArmy ;
//	    combat.setSide2(side2) ;
//	    
//	    combat.runArmyBattle() ;
//	    
//	    CombatArmy resultingCloudArmy = combat.getSide1()[0] ;
//	    CombatArmy resultingNorthGondorArmy = combat.getSide2()[0] ;
//	    
//	    assertEquals(283, resultingCloudArmy.getHC().getNumber()) ;
//	    assertEquals(0, resultingNorthGondorArmy.getHC().getNumber()) ;		fail("nothing to test") ;
//	}
	
	public void setUp() {
		JOverseerJIDEClient.launchTestFramework() ;
	}
}
