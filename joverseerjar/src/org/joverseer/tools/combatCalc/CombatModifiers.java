package org.joverseer.tools.combatCalc;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.GameHolder;

/**
 * Holds various lookup info with combat modifiers.
 * 
 * TODO: Move this information to a file.
 * 
 * @author Marios Skounakis
 *
 */
public class CombatModifiers {
            
    public static int getModifierFor(int nationNo, HexTerrainEnum terrain, ClimateEnum climate) {
    	
       	GameMetadata gm = GameHolder.instance().getGame().getMetadata();
    			
       	int tm = gm.getTerrainModifier(nationNo, terrain);
       	int cm = gm.getClimateModifier(nationNo, climate); 
       	
       	return tm + cm;
    
    }
    
    public static int getRelationModifier(NationRelationsEnum relations) {
        if (relations == NationRelationsEnum.Hated) {
            return 125;
        } else if (relations == NationRelationsEnum.Disliked) {
            return 110;
        } else if (relations == NationRelationsEnum.Neutral) {
            return 100;
        } else if (relations == NationRelationsEnum.Tolerated) {
            return 90;
        } else if (relations == NationRelationsEnum.Friendly) {
            return 75;
        } 
        return 100;
    }
}
