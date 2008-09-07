package org.joverseer.ui.listviews;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;

public class HexProductionTableModel  extends ItemTableModel {
    private static final long serialVersionUID = -7154147184547454802L;

	public HexProductionTableModel(MessageSource messageSource) {
        super(HexProductionWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "terrain", "climate", "leather", "bronze", "steel", "mithril", "food", "timber", "mounts", "gold"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class, 
        					Integer.class};
    }

    
    
}
