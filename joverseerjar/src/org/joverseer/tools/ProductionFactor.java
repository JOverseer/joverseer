package org.joverseer.tools;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;

/**
 * Returns the production factor for a hex given it's pop center size and climate
 * 
 * @author Marios Skounakis
 */

//TODO externalize to data files
public class ProductionFactor {
    static int[][] climateFactors = 
        new int[][]{
            {10, 30, 30, 30, 10, 10, 10, 30},
            {20, 40, 40, 40, 20, 20, 20, 40},
            {30, 60, 60, 60, 30, 30, 30, 60},
            {80, 100, 100, 100, 80, 80, 80, 100},
            {90, 100, 100, 100, 90, 90, 90, 100},
            {100, 100, 100, 100, 100, 100, 100, 100},
            {80, 80, 80, 80, 80, 80, 80, 80}
    };
    
    public int getFactor(PopulationCenterSizeEnum size) {
    	return PopulationCenterSizeEnum.lookupSize(size, new int[]{0,100,80,60,40,20});
    }
    
    public int getFactor(ClimateEnum climate, ProductEnum pe) {
        int y = 0;
        if (climate == ClimateEnum.Polar) y = 0;
        if (climate == ClimateEnum.Severe) y = 1;
        if (climate == ClimateEnum.Cold) y = 2;
        if (climate == ClimateEnum.Cool) y = 3;
        if (climate == ClimateEnum.Mild) y = 4;
        if (climate == ClimateEnum.Warm) y = 5;
        if (climate == ClimateEnum.Hot) y = 6;
        int x = 0;
        if (pe == ProductEnum.Leather) x = 0;
        if (pe == ProductEnum.Bronze) x = 1;
        if (pe == ProductEnum.Steel) x = 2;
        if (pe == ProductEnum.Mithril) x = 3;
        if (pe == ProductEnum.Food) x = 4;
        if (pe == ProductEnum.Timber) x = 5;
        if (pe == ProductEnum.Mounts) x = 6;
        if (pe == ProductEnum.Gold) x = 7;
        
        return climateFactors[y][x];
    }
}
