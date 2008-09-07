package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

public class HexProductionListView extends ItemListView {
	ArrayList averages = new ArrayList();
	
    public HexProductionListView() {
        super(TurnElementsEnum.PopulationCenter, HexProductionTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 64, 64, 
        		64, 64, 64, 64, 64, 64, 64};
    }
    
    

	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
		filters.add(new AbstractListViewFilter("Terrain average") {
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper)obj;
				return hpw.getHexNo() == null && hpw.getClimate() == null;
			}
			
		});
		filters.add(new AbstractListViewFilter("Terrain/climate average") {
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper)obj;
				return hpw.getHexNo() == null && hpw.getClimate() != null;
			}
			
		});
		filters.add(new AbstractListViewFilter("Individual hexes") {
			public boolean accept(Object obj) {
				HexProductionWrapper hpw = (HexProductionWrapper)obj;
				return hpw.getHexNo() != null && hpw.getHexNo() > 0;
			}
			
		});
		 return new AbstractListViewFilter[][] {
	                filters.toArray(new AbstractListViewFilter[] {})};
	}

	protected void setItems() {
		averages.clear();
		ArrayList items = new ArrayList();
		HexProductionTableModel hptm = (HexProductionTableModel)tableModel;
		if (GameHolder.instance().hasInitializedGame()) {
			Game game = GameHolder.instance().getGame();
			for (PopulationCenter pc : (ArrayList<PopulationCenter>)game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
				boolean hasProduction = false;
	            for (ProductEnum p : ProductEnum.values()) {
	                if (pc.getProduction(p) != null && pc.getProduction(p) > 0) {
	                    hasProduction = true;
	                }
	            }
	            if (hasProduction) {
	            	HexProductionWrapper pw = new HexProductionWrapper(pc, game, game.getTurn());
	            	if (getActiveFilter() != null && getActiveFilter().accept(pw)) {
	            		items.add(pw);
	            	}
	            	addToAverage(pw);
	            }
			}
		}
		for (HexProductionWrapper av : (ArrayList<HexProductionWrapper>)averages) {
			av.divideByCount();
			if (getActiveFilter() != null && getActiveFilter().accept(av)) {
        		items.add(av);
        	}
		}
		tableModel.setRows(items);
		
	}
    
    protected void addToAverage(HexProductionWrapper pw) {
    	boolean foundTerrain = false;
    	boolean foundClimate = false;
    	for (HexProductionWrapper av : (ArrayList<HexProductionWrapper>)averages) {
    		if (av.getTerrain() != null && av.getTerrain().equals(pw.getTerrain())) {
    			if (av.getClimate() == null) {
    				foundTerrain = true;
    				av.add(pw);
    			}
    			if (av.getClimate() != null && av.getClimate().equals(pw.getClimate())) {
    				foundClimate = true;
    				av.add(pw);
    			}
    		}
    	}
    	if (!foundTerrain) {
    		HexProductionWrapper av = new HexProductionWrapper();
    		av.setTerrain(pw.getTerrain());
    		av.add(pw);
    		averages.add(av);
    	}
    	if (!foundClimate && pw.getClimate() != null) {
    		HexProductionWrapper av = new HexProductionWrapper();
    		av.setTerrain(pw.getTerrain());
    		av.setClimate(pw.getClimate());
    		av.add(pw);
    		averages.add(av);
    	}
    }
}
