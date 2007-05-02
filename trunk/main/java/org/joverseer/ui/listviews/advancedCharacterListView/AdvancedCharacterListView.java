package org.joverseer.ui.listviews.advancedCharacterListView;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterInfoCollector;
import org.joverseer.ui.listviews.AbstractListViewFilter;
import org.joverseer.ui.listviews.BaseItemListView;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.table.SortableTableModel;

public class AdvancedCharacterListView extends BaseItemListView {

    public AdvancedCharacterListView() {
        super(AdvancedCharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {96, 48, 48, 48, 48, 48, 48, 48, 48, 48, 32, 32, 32, 32, 32, 32, 120, 48, 96, 48};
    }

    protected void setItems() {
        ArrayList items = CharacterInfoCollector.instance().getWrappers();
        ArrayList filteredItems = new ArrayList();
        for (Object o : items) {
            if (getActiveFilter() == null || getActiveFilter().accept(o)) {
                filteredItems.add(o);
            }
        }
        tableModel.setRows(filteredItems);
    }

    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(CharacterAttributeWrapper.class, new CharacterAttributeWrapperTableCellRenderer(
                tableModel));
        table.setDefaultRenderer(ArtifactWrapper.class, new ArtifactWrapperTableCellRenderer(tableModel));
        return c;
    }


    protected AbstractListViewFilter[][] getFilters() {
        ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][] {
                filters.toArray(new AbstractListViewFilter[] {}),
                TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(),
                new AbstractListViewFilter[]{
                    new DeathFilter("Not Dead & Dead", null),
                    new DeathFilter("Not Dead", new CharacterDeathReasonEnum[]{CharacterDeathReasonEnum.NotDead, null}),
                    new DeathFilter("Dead", new CharacterDeathReasonEnum[]{CharacterDeathReasonEnum.Assassinated, CharacterDeathReasonEnum.Executed, CharacterDeathReasonEnum.Dead, CharacterDeathReasonEnum.Cursed}),
                }
        };
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
                        "advancedCharacterListViewCommandGroup", new Object[] {new CopyToClipboardCommand()});
                return group.createPopupMenu();
            }
        });
        comps.add(popupMenu);
        return comps.toArray(new JComponent[] {});
    }

    class CopyToClipboardCommand extends ActionCommand implements ClipboardOwner {

        String DELIM = "\t";
        String NL = "\n";
        Game game;

        protected void doExecuteCommand() {
            game = GameHolder.instance().getGame();
            String txt = "";
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(i);
                AdvancedCharacterWrapper aw = (AdvancedCharacterWrapper) tableModel.getRow(idx);
                txt += getRow(aw) + NL;
            }
            StringSelection stringSelection = new StringSelection(txt);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        private String getStatText(CharacterAttributeWrapper caw) {
            String v = caw == null || caw.getValue() == null ? "" : caw.getValue().toString();
            if (caw != null && caw.getValue() != null) {
                InfoSource is = caw.getInfoSource();
                if (DerivedFromTitleInfoSource.class.isInstance(is)) {
                    v += "+";
                } else if (RumorActionInfoSource.class.isInstance(is)) {
                    v += "+";
                }
            }
            if (caw != null && caw.getTotalValue() != null) {
                if (!caw.getTotalValue().toString().equals(caw.getValue().toString())
                        && !caw.getTotalValue().toString().equals("0")) {
                    v += "(" + caw.getTotalValue().toString() + ")";
                }
            }
            if (v == null || v.equals("0"))
                return v;

            if (caw != null && caw.getInfoSource() != null) {
                InfoSource is = caw.getInfoSource();
                if (MetadataSource.class.isInstance(is)) {
                    v += "*";
                } else if (DerivedFromTitleInfoSource.class.isInstance(is)) {
                    v += "*";
                } else if (RumorActionInfoSource.class.isInstance(is)) {
                    v += "*";
                }
            }
            return v;
        }

        private String getArtifactText(ArtifactWrapper aw) {
            if (aw == null)
                return "";
            String txt = String.valueOf(aw.getNumber());
            if (aw.getInfoSource().getTurnNo() < game.getCurrentTurn()) {
                txt += " (t" + aw.getInfoSource().getTurnNo() + ")";
            }
            return txt;
        }

        private String getDeathReasonStr(CharacterDeathReasonEnum cd) {
            if (cd == null)
                return "?";
            if (cd == CharacterDeathReasonEnum.NotDead)
                return "";
            return cd.toString();
        }

        private String getRow(AdvancedCharacterWrapper aw) {
            Nation n = game.getMetadata().getNationByNum(aw.getNationNo());
            String nationName = n == null || n.getNumber() == 0 ? "" : n.getShortName();
            return aw.getHexNo() + DELIM + aw.getName() + DELIM + aw.getNationNo() + DELIM + nationName + DELIM
                    + getStatText(aw.getCommand()) + DELIM + getStatText(aw.getAgent()) + DELIM
                    + getStatText(aw.getEmmisary()) + DELIM + getStatText(aw.getMage()) + DELIM
                    + getStatText(aw.getStealth()) + DELIM + getStatText(aw.getChallenge()) + DELIM
                    + getStatText(aw.getHealth()) + DELIM + getArtifactText(aw.getA0()) + DELIM
                    + getArtifactText(aw.getA1()) + DELIM + getArtifactText(aw.getA2()) + DELIM
                    + getArtifactText(aw.getA3()) + DELIM + getArtifactText(aw.getA4()) + DELIM
                    + getArtifactText(aw.getA5()) + DELIM + aw.getTravellingWith() + DELIM
                    + getDeathReasonStr(aw.getDeathReason()) + DELIM + aw.getTurnNo() + DELIM
                    + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource());
        }

        public void lostOwnership(Clipboard arg0, Transferable arg1) {
        }
    }

    class DeathFilter extends AbstractListViewFilter {
        ArrayList<CharacterDeathReasonEnum> deathReasons = new ArrayList<CharacterDeathReasonEnum>();
        
        public DeathFilter(String descr, CharacterDeathReasonEnum[] deathReasons) {
            super(descr);
            if (deathReasons == null) {
                this.deathReasons = null;
            } else {
                this.deathReasons.addAll(Arrays.asList(deathReasons));
            }
        }

        public boolean accept(Object obj) {
            return deathReasons == null || deathReasons.contains(((AdvancedCharacterWrapper)obj).getDeathReason());
        }
    }

}
