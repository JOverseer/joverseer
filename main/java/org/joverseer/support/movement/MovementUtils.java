package org.joverseer.support.movement;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.springframework.richclient.application.Application;

public class MovementUtils {
	static int[] infMovementCost = 	new int[]{3, 3, 5, 6, 5, 12, 4, -1, -1};
	static int[] infRoadMovementCost =	new int[]{2, 2, 3, 3, 3,  6, 2, -1, -1};
	static int[] cavMovementCost =		new int[]{2, 2, 5, 5, 3, 12, 2, -1, -1};
	static int[] cavRoadMovementCost = new int[]{1, 1, 2, 2, 1,  3, 1, -1, -1};

	static int bridgeFordCost = 1;
	static int riverCost = 1;
	static int majorRiverCost = -1;
	
	public static int calculateMovementCostForArmy(int startHexNo, int destHexNo, boolean isCavalry, boolean isFed) {
		GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
		//retrieve hexes
		Hex start = gm.getHex(startHexNo);
		Hex dest = gm.getHex(destHexNo);
		
		// decide if road exists
		// TODO
		boolean roadExists = false;
		
		// find appropriate cost matrix
		int[] movementCosts;
		if (!isCavalry) {
			if (roadExists) {
				movementCosts = infRoadMovementCost;
			} else {
				movementCosts = infMovementCost;
			}
		} else {
			if (roadExists) {
				movementCosts = cavRoadMovementCost;
			} else {
				movementCosts = cavMovementCost;
			}
		}
		
		return (int)0;
	}
}
