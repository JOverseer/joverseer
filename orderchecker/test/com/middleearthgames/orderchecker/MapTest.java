package com.middleearthgames.orderchecker;

import static org.junit.Assert.*;

import org.junit.Test;

public class MapTest {

	@Test
	public final void testCalcArmyMovement() {
		final boolean CAV = true;
		final boolean NO_CAV = false;
		
		Map map = new Map();
		Hex a = new Hex(1);
		Hex b = new Hex(2);
		map.addHex(a);
		map.addHex(b);
		//target hex should be 101, but it's not there
		assertEquals(-1, map.calcArmyMovement(1, Hex.DIRECTION_E,NO_CAV));
		b = new Hex(101);
		map.addHex(b);
		
		// this would go bang without terrain set.
		//assertEquals(-1, map.calcArmyMovement(1, Hex.DIRECTION_E,false));
		
		a.setTerrain(Hex.TERRAIN_FOREST);
		b.setTerrain(Hex.TERRAIN_PLAINS);
		assertEquals(3, map.calcArmyMovement(1, Hex.DIRECTION_E,NO_CAV));
		assertEquals(2, map.calcArmyMovement(1, Hex.DIRECTION_E,CAV));
		
		a.addFeature(Hex.FEATURE_ROAD);
		a.addDirection(Hex.DIRECTION_E);
		assertEquals(2, map.calcArmyMovement(1, Hex.DIRECTION_E,NO_CAV));
		assertEquals(1, map.calcArmyMovement(1, Hex.DIRECTION_E,CAV));

		a.addFeature(Hex.FEATURE_FORD);
		a.addDirection(Hex.DIRECTION_E);
		assertEquals(3, map.calcArmyMovement(1, Hex.DIRECTION_E,NO_CAV));
		assertEquals(2, map.calcArmyMovement(1, Hex.DIRECTION_E,CAV));
		
		
	}

}
