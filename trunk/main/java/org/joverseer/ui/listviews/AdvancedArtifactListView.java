package org.joverseer.ui.listviews;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactTrueInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactInfoCollector;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.ui.listviews.advancedCharacterListView.AdvancedCharacterTableModel;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.table.SortableTableModel;

/**
 * The advanced artifact information tab
 * Shows ArtifactWrappers from the ArtifactInfoCollector
 * 
 * @author Marios Skounakis
 */
public class AdvancedArtifactListView extends BaseItemListView {

    public AdvancedArtifactListView() {
        super(AdvancedArtifactTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {32, 96, 48, 132, 48, 48, 120, 120, 48, 120};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        ArrayList aws = ArtifactInfoCollector.instance().getWrappersForTurn(g.getCurrentTurn());
        ArrayList filteredItems = new ArrayList();
        AbstractListViewFilter filter = getActiveFilter();
        for (Object obj : aws) {
            if (filter == null || filter.accept(obj)) {
                filteredItems.add(obj);
            }
        }
        tableModel.setRows(filteredItems);
    }
    
    protected JComponent[] getButtons() {
        ArrayList<JComponent> comps = new ArrayList<JComponent>();
        comps.addAll(Arrays.asList(super.getButtons()));
        JLabelButton popupMenu = new JLabelButton();
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        popupMenu.setIcon(ico);
        popupMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                        "advancedArtifactListViewCommandGroup", new Object[] {
                        		new CopyToClipboardCommand(),
                        		});
                return group.createPopupMenu();
            }
        });
        comps.add(popupMenu);
        return comps.toArray(new JComponent[] {});
    }
    
    protected AbstractListViewFilter getTextFilter(String txt) {
		if (txt == null || txt.equals("")) return super.getTextFilter(txt);
		try {
			int hexNo = Integer.parseInt(txt.trim());
			return new HexFilter("", hexNo);
		}
		catch (Exception exc) {
			// do nothing
		}
		return new TextFilter("Name", "name", txt);
	}
    
    protected boolean hasTextFilter() {
    	return true;
    }

    protected AbstractListViewFilter[][] getFilters() {
        ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        filters.add(new OwnedArtifactFilter("Owned", true));
        filters.add(new OwnedArtifactFilter("Not Owned", false));
        return new AbstractListViewFilter[][] {
                filters.toArray(new AbstractListViewFilter[] {}),
                TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(),
                new AbstractListViewFilter[] {
                        new InfoSourceClassFilter("All sources", null),
                        new InfoSourceClassFilter("LA/LAT", new Class[] {DerivedFromLocateArtifactInfoSource.class,
                                DerivedFromLocateArtifactTrueInfoSource.class}),
                        new InfoSourceClassFilter("Xml/Pdf", new Class[] {XmlTurnInfoSource.class}),
                        new InfoSourceClassFilter("Starting", new Class[] {MetadataSource.class}),},
                new AbstractListViewFilter[] {new ArtifactPowerFilter("All Powers", null),
                        new ArtifactPowerFilter("Combat", "Combat "), new ArtifactPowerFilter("Agent", "Agent "),
                        new ArtifactPowerFilter("Command", "Command "), new ArtifactPowerFilter("Stealth", "Stealth "),
                        new ArtifactPowerFilter("Mage", "Mage "), new ArtifactPowerFilter("Emissary", "Emissary "),
                        new ArtifactPowerFilter("Scrying", "Scry"),
                        new ArtifactPowerFilter("Curse", "Spirit Mastery"),
                        new ArtifactPowerFilter("Conjuring", "Conjuring Ways"),
                        new ArtifactPowerFilter("Teleport", " Teleport")}};
    }

    /**
     * Filter based on the class of the info source
     * @author Marios Skounakis
     */
    class InfoSourceClassFilter extends AbstractListViewFilter {

        Class[] classes;

        public InfoSourceClassFilter(String descr, Class[] classes) {
            super(descr);
            this.classes = classes;
        }

        public boolean accept(Object obj) {
            if (classes == null)
                return true;
            InfoSource is = ((ArtifactWrapper) obj).getInfoSource();
            for (Class c : classes) {
                if (c.isInstance(is))
                    return true;
            }
            return false;
        }
    }

    /**
     * Filter for owned/not owned artifacts
     * @author Marios Skounakis
     */
    class OwnedArtifactFilter extends AbstractListViewFilter {

        Boolean owned;

        public OwnedArtifactFilter(String descr, Boolean owned) {
            super(descr);
            this.owned = owned;
        }

        public boolean accept(Object obj) {
            ArtifactWrapper aw = (ArtifactWrapper) obj;
            if (owned == null)
                return true;
            if (owned) {
                return aw.getOwner() != null && !aw.getOwner().equals("");
            } else {
                return aw.getOwner() == null || aw.getOwner().equals("");
            }
        }
    }

    /**
     * Filter based no the artifact power
     * 
     * @author Marios Skounakis
     */
    class ArtifactPowerFilter extends AbstractListViewFilter {

        String powerStr;

        public ArtifactPowerFilter(String descr, String power) {
            super(descr);
            this.powerStr = power;
        }

        public boolean accept(Object obj) {
            if (powerStr == null)
                return true;
            ArtifactWrapper aw = (ArtifactWrapper) obj;
            return (aw.getPower1().indexOf(powerStr) > -1 || aw.getPower2().indexOf(powerStr) > -1);
        }
    }
    
    class CopyToClipboardCommand extends ActionCommand implements ClipboardOwner {

        String DELIM = "\t";
        String NL = "\n";
        Game game;

        protected void doExecuteCommand() {
            game = GameHolder.instance().getGame();
            String txt = "";
            for (int j=0; j<tableModel.getDataColumnCount(); j++) {
            	txt += (txt.equals("") ? "" : DELIM) + tableModel.getDataColumnHeaders()[j];
            	if (j == 2) {
            		// duplicate column "nation"
            		txt += (txt.equals("") ? "" : DELIM) + tableModel.getDataColumnHeaders()[j];
            	}
            }
            txt += NL;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(i);
                ArtifactWrapper aw = (ArtifactWrapper) tableModel.getRow(idx);
                txt += getRow(aw) + NL;
            }
            StringSelection stringSelection = new StringSelection(txt);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        

       

        private String getRow(ArtifactWrapper aw) {
            Nation n = game.getMetadata().getNationByNum(aw.getNationNo());
            String nationName = n == null || n.getNumber() == 0 ? "" : n.getShortName();
            return aw.getNumber() + DELIM + aw.getName() + DELIM + aw.getNationNo() + DELIM + nationName + DELIM
                    + aw.getOwner() + DELIM + aw.getHexNo() + DELIM
                    + aw.getAlignment() + DELIM + aw.getPower1() + DELIM
                    + aw.getPower2() + DELIM + aw.getTurnNo() + DELIM
                    + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource()) + NL;
        }

        public void lostOwnership(Clipboard arg0, Transferable arg1) {
        }
    }
}
