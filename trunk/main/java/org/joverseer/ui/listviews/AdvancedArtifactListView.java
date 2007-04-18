package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.Arrays;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactTrueInfoSource;
import org.joverseer.ui.domain.ArtifactWrapper;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.springframework.richclient.application.Application;


public class AdvancedArtifactListView extends BaseItemListView {

    public AdvancedArtifactListView() {
        super(AdvancedArtifactTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96, 48, 132, 48, 48, 120, 120, 48, 120};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;
        // initialize with metadata info
        MetadataSource ms = new MetadataSource();
        Container aws = new Container(new String[]{"number"});
        for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)g.getMetadata().getArtifacts().getItems()) {
            ArtifactWrapper aw = new ArtifactWrapper();
            aw.setNumber(ai.getNo());
            aw.setName(ai.getName());
            aw.setOwner(ai.getOwner());
            aw.setPower1(ai.getPower1());
            aw.setPower2(ai.getPower2());
            aw.setInfoSource(ms);
            aw.setAlignment(ai.getAlignment());
            aw.setTurnNo(0);
            aws.addItem(aw);
        }
        
        // loop over turns
        for (int i=0; i<=g.getCurrentTurn(); i++) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            for (Artifact a : (ArrayList<Artifact>)t.getContainer(TurnElementsEnum.Artifact).getItems()) {
                ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", a.getNumber());
                aw.setOwner(a.getOwner());
                aw.setHexNo(a.getHexNo());
                aw.setInfoSource(a.getInfoSource());
                aw.setTurnNo(t.getTurnNo());
                Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", aw.getOwner());
                if (c != null) {
                    aw.setNationNo(c.getNationNo());
                    if (c.getNationNo() == null || c.getNationNo() == 0) {
                        c = (Character)g.getMetadata().getCharacters().findFirstByProperty("id", c.getId());
                        if (c != null) {
                            aw.setNationNo(c.getNationNo());
                        }
                    }
                }
            }
            
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                for (Integer aid : c.getArtifacts()) {
                    ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", aid);
                    aw.setOwner(c.getName());
                    aw.setHexNo(c.getHexNo());
                    aw.setInfoSource(c.getInfoSource());
                    aw.setNationNo(c.getNationNo());
                    aw.setTurnNo(t.getTurnNo());
                }
            }
        }
        ArrayList filteredItems = new ArrayList();
        AbstractListViewFilter filter = getActiveFilter();
        for (Object obj : aws.getItems()) {
        	if (filter == null || filter.accept(obj)) {
        		filteredItems.add(obj);
        	}
        }
        tableModel.setRows(filteredItems);
    }

	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        filters.add(new OwnedArtifactFilter("Owned", true));
        filters.add(new OwnedArtifactFilter("Not Owned", false));
        return new AbstractListViewFilter[][]{
        		filters.toArray(new AbstractListViewFilter[]{}),
        		TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(),
        		new AbstractListViewFilter[]{
        			new LASourceFilter("All sources", null),
        			new LASourceFilter("LA/LAT", new Class[]{DerivedFromLocateArtifactInfoSource.class, DerivedFromLocateArtifactTrueInfoSource.class}),
        			new LASourceFilter("Xml/Pdf", new Class[]{XmlTurnInfoSource.class}),
        			new LASourceFilter("Starting", new Class[]{MetadataSource.class}),
        		},
        		new AbstractListViewFilter[]{
        			new ArtifactPowerFilter("All Powers", null),
        			new ArtifactPowerFilter("Combat", "Combat "),
        			new ArtifactPowerFilter("Agent", "Agent "),
        			new ArtifactPowerFilter("Command", "Command "),
        			new ArtifactPowerFilter("Stealth", "Stealth "),
        			new ArtifactPowerFilter("Mage", "Mage "),
        			new ArtifactPowerFilter("Emissary", "Emissary "),
        			new ArtifactPowerFilter("Curse", "Spirit Mastery"),
        			new ArtifactPowerFilter("Conjuring", "Conjuring Ways"),
        			new ArtifactPowerFilter("Teleport", " Teleport")
        		}
    		};
	}
	
	class LASourceFilter extends AbstractListViewFilter {
		Class[] classes;
		public LASourceFilter(String descr, Class[] classes) {
			super(descr);
			this.classes = classes;
		}
		
		public boolean accept(Object obj) {
			if (classes == null) return true;
			InfoSource is = ((ArtifactWrapper)obj).getInfoSource();
			for (Class c : classes) {
				if (c.isInstance(is)) return true;
			}
			return false;
		}
	}

    class OwnedArtifactFilter extends AbstractListViewFilter {
    	Boolean owned;
    	
    	public OwnedArtifactFilter(String descr, Boolean owned) {
    		super(descr);
    		this.owned = owned; 
    	}
    	
    	public boolean accept(Object obj) {
    		ArtifactWrapper aw = (ArtifactWrapper)obj;
    		if (owned == null) return true;
    		if (owned) {
    			return aw.getOwner() != null && !aw.getOwner().equals("");
    		} else {
    			return aw.getOwner() == null || aw.getOwner().equals("");
    		}
    	}
    }
    
    class ArtifactPowerFilter extends AbstractListViewFilter {
    	String powerStr;
    	
    	public ArtifactPowerFilter(String descr, String power) {
    		super(descr);
    		this.powerStr = power;
    	}
    	
    	public boolean accept(Object obj) {
    		if (powerStr == null) return true;
    		ArtifactWrapper aw = (ArtifactWrapper)obj;
    		return (aw.getPower1().indexOf(powerStr) > -1 || aw.getPower2().indexOf(powerStr) > -1);
    	}
    }
}
