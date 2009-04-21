package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.collections.CollectionUtils;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.SpellcasterWrapper;
import org.joverseer.ui.listviews.commands.GenericCopyToClipboardCommand;
import org.joverseer.ui.listviews.commands.PopupMenuCommand;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.ColumnToSort;

/**
 * SpellCaster list view Based on the selected spell list, which is displayed as a filter, it populates the table with
 * the names of the casters that know the respective spells. Proficiency in each spell is shown in the table columns,
 * which dynamically adjust based on the number of spells. For Spirit Mastery spells, the health effect is also shown
 * 
 * @author Marios Skounakis
 */
public class SpellcasterListView extends BaseItemListView {

    JComboBox combo;

    public SpellcasterListView() {
        super(SpellcasterTableModel.class);
    }

    protected int[] columnWidths() {
        int[] widths = new int[105];
        widths[0] = 96;
        widths[1] = 32;
        widths[2] = 42;
        widths[3] = 64;
        widths[4] = 42;
        for (int i = 0; i < 100; i++) {
            widths[i + 5] = 64;
        }
        return widths;
    }
    
    

    /**
     * The available spell lists
     * The health effect of a spirit mastery spell is represented as 1000 + spell id
     */
    protected ArrayList createSpellLists() {
        ArrayList spellLists = new ArrayList();
        spellLists.add(new SpellList("Artifact/Character tracking", new Integer[] {418, 428, 420, 430}, new String[] {
                "LA", "LAT", "RC", "RCT"}));
        spellLists.add(new SpellList("Healing", new Integer[] {2, 4, 6, 8}, new String[] {"Minor Heal", "Major Heal",
                "Great. Heal", "Heal True"}));
        // spellLists.add(new SpellListFromSpellMetadata("Movement", new String[]{"Movement Mastery", "Return Mastery",
        // "Teleport"}));
        spellLists.add(new SpellList("Movement", new Integer[] {302, 304, 306, 308, 310, 312, 314}, new String[] {
                "Long Strd", "Fast Strd", "Path Mstr", "Capital Ret", "Major Ret", "Ret True", "Teleport"}));
        // spellLists.add(new SpellListFromSpellMetadata("Defense", new String[]{"Barrier Mastery", "Resistance
        // Mastery"}));
        spellLists.add(new SpellList("Defense", new Integer[] {102, 106, 112, 114, 104, 108, 110, 116},
                new String[] {"Barriers", "Deflect.", "Shields", "Barr. Walls", "Resistances", "Blessings", "Protect.",
                        "Force Walls"}));
        // spellLists.add(new SpellListFromSpellMetadata("Fire Mastery", new String[]{"Fire Mastery"}));
        spellLists.add(new SpellList("Fire Mastery", new Integer[] {202, 204, 206, 232, 234, 236, 240},
                new String[] {"Call Fire", "Wild Flames", "Wall of Fire", "Fire Bolts", "Fire Balls", "Fire Storms",
                        "Smn Fire Sprts"}));
        // spellLists.add(new SpellListFromSpellMetadata("Word Mastery", new String[]{"Word Mastery"}));
        spellLists.add(new SpellList("Words of Pain", new Integer[] {208, 210, 212, 220, 222, 224, 242}, new String[] {
                "Pain", "Calm", "Paralysis", "Agony", "Stun", "Command", "Death"}));
        // spellLists.add(new SpellListFromSpellMetadata("Wind Mastery", new String[]{"Wind Mastery"}));
        spellLists.add(new SpellList("Wind Mastery", new Integer[] {214, 216, 218, 226, 228, 230, 238}, new String[] {
                "Call Winds", "Wild Winds", "Wall of Wind", "Chill Bolts", "Frost Balls", "Wind Storms",
                "Smn Wind Sprts"}));
        // spellLists.add(new SpellListFromSpellMetadata("Dark Summons", new String[]{"Dark Summons"}));
        spellLists.add(new SpellList("Dark Summons", new Integer[] {244, 246, 248}, new String[] {"Frfl Hearts",
                "Smn Storms", "Fanaticism"}));
        // spellLists.add(new SpellListFromSpellMetadata("Conjuring Ways", new String[]{"Conjuring Ways"}));
        spellLists.add(new SpellList("Conjuring Ways", new Integer[] {508, 510, 512}, new String[] {"Mounts", "Food",
                "Hordes"}));
        spellLists.add(new SpellList("Spirit Mastery", new Integer[] {502, 1502, 504, 1504, 506, 1506}, new String[] {
                "Weakness", "Weak HE", "Sickness", "Sick HE", "Curses", "Curse HE"}));
        // spellLists.add(new SpellListFromSpellMetadata("Spirit Mastery", new String[]{"Spirit Mastery"}));
        // spellLists.add(new SpellListFromSpellMetadata("Lore Spells", new String[]{"Lore Spells"}));
        spellLists.add(new SpellList("Lore Spells", new Integer[] {402, 404, 408, 422, 424, 432}, new String[] {
                "Allegiance", "Relations", "Nationality", "Power", "Mission", "Secrets"}));
        // spellLists.add(new SpellListFromSpellMetadata("Divinations", new String[]{"Divinations"}));
        spellLists.add(new SpellList("Divinations", new Integer[] {406, 410, 417, 419, 426}, new String[] {"Army",
                "Algnce Forces", "Character", "Nation", "Army True"}));
        // spellLists.add(new SpellListFromSpellMetadata("Artifact Mastery", new String[]{"Artifact Mastery"}));
        spellLists.add(new SpellList("Artifact Mastery", new Integer[] {412, 418, 428},
                new String[] {"RA", "LA", "LAT"}));
        // spellLists.add(new SpellListFromSpellMetadata("Scrying & Hidden Visions", new String[]{"Scrying", "Hidden
        // Visions"}));
        spellLists.add(new SpellList("Scrying & Hidden Visions",
                new Integer[] {413, 414, 415, 436, 416, 420, 430, 434}, new String[] {"Scry PC", "Scry Hex",
                        "Scry Area", "Scry Char", "Rev Prod", "Rev Char", "Rev Char True", "Rev PC"}));

        // all spells
        if (GameHolder.instance().hasInitializedGame()) {
            SpellList all = new SpellList("All", new Integer[] {}, new String[] {});

            for (SpellInfo si : (ArrayList<SpellInfo>) GameHolder.instance().getGame().getMetadata().getSpells()
                    .getItems()) {
                all.getSpells().add(si.getNumber());
                all.getSpellDescrs().add(si.getName());
                if (si.getList().equals("Spirit Mastery")) {
                    all.getSpells().add(si.getNumber() + 100);
                    all.getSpellDescrs().add(si.getName() + " ΗΕ");
                }
            }
            spellLists.add(all);
        }
        return spellLists;
    }


    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[] {new ColumnToSort(0, 2), new ColumnToSort(1, 0)};
    }
    
    protected JComponent[] getButtons() {
    	return new JComponent[]{
    			new PopupMenuCommand().getButton(new Object[]{
    					new GenericCopyToClipboardCommand(table)})};
	}

    /**
     * Resets the columns - setting widths and header values
     */
    protected void resetColumns() {
        try {
            SpellList sl = (SpellList) combo.getSelectedItem();
            if (sl == null)
                return;
            sl = getSpellsFromFilters();
            for (int i = 5; i < sl.getSpells().size() + 5; i++) {
                TableColumn col = table.getColumnModel().getColumn(i);
                col.setMaxWidth(100);
                col.setPreferredWidth(64);
                col.setHeaderValue(sl.getSpellDescrs().get(i - 5));
            }
            for (int i = sl.getSpells().size() + 5; i < 105; i++) {
                TableColumn col = table.getColumnModel().getColumn(i);
                col.setMaxWidth(0);
                col.setPreferredWidth(0);
            }
            table.updateUI();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(combo = new JComboBox(), "align=left");
        combo.setPreferredSize(new Dimension(200, 24));
        combo.setOpaque(true);
        combo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // resetColumns();
                setItems();
            }
        });
        
        tlb.cell(textFilterField = new JTextField(), "align=left");
        textFilterField.setPreferredSize(new Dimension(200, 24));
        textFilterField.setOpaque(true);
        textFilterField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	if (combo.getItemCount() > 0) {
            		combo.setSelectedIndex(combo.getItemCount()-1);
            	}
                setItems();
            }
        });
        textFilterField.setToolTipText("Enter comma separated list of spells");
        
        JTableHeader header = table.getTableHeader();
        header.addMouseMotionListener(new MouseMotionAdapter() {

            TableColumn curCol;

            public void mouseMoved(MouseEvent evt) {
                TableColumn col = null;
                JTableHeader header = (JTableHeader) evt.getSource();
                JTable table = header.getTable();
                TableColumnModel colModel = table.getColumnModel();
                int vColIndex = colModel.getColumnIndexAtX(evt.getX());

                // Return if not clicked on any column header
                if (vColIndex >= 0) {
                    col = colModel.getColumn(vColIndex);
                }

                if (col != curCol) {
                    // tool tip for the column header
                    // so we can display the details for this column 
                    // these are: 
                    // - spell description and details for spells
                    // - the string "Health drop effect for <spell>" for health effect columns
                    String toolTip = "";
                    SpellList sl = getSpellsFromFilters();
                    if (sl != null) {
                        if (vColIndex - 5 >= 0 && vColIndex - 5 < sl.getSpells().size()) {
                            int spellId = sl.getSpells().get(vColIndex - 5);
                            if (spellId < 1000) {
                                SpellInfo si = (SpellInfo) GameHolder.instance().getGame().getMetadata().getSpells()
                                        .findFirstByProperty("number", spellId);
                                if (si == null) {
                                    toolTip = String.valueOf(spellId);
                                } else {
                                    toolTip = si.getNumber() + " - " + si.getName() + ": " + si.getDescription();
                                }
                            } else {
                                SpellInfo si = (SpellInfo) GameHolder.instance().getGame().getMetadata().getSpells()
                                        .findFirstByProperty("number", spellId - 1000);
                                if (si == null) {
                                    toolTip = String.valueOf(spellId);
                                } else {
                                    toolTip = "Health Drop Effect for " + si.getNumber() + " - " + si.getName() + ": "
                                            + si.getDescription();
                                }
                            }
                        }
                    }
                    header.setToolTipText(toolTip);
                    curCol = col;
                }
            }
        });
        tlb.row();
        tlb.cell(tableComp);
        tlb.row();
        return tlb.getPanel();
    }
    
    SpellList getSpellsFromFilters() {
    	SpellList sl = (SpellList) combo.getSelectedItem();
    	if (sl == null) return null;
        ArrayList<Integer> spells = sl.getSpells();
        ArrayList<String> spellDescrs = sl.getSpellDescrs();
        ArrayList<Integer> retSpells = new ArrayList<Integer>();
        ArrayList<String> retSpellDescrs = new ArrayList<String>();
    	String txt = textFilterField.getText().trim();
    	if (txt.equals("")) return sl; 
		ArrayList<Integer> textSpells = new ArrayList<Integer>();
		String[] parts = txt.split(",");
		for (String p : parts) {
			try {
				int spellNo = Integer.parseInt(p.trim());
				for (int i=0; i<spells.size(); i++) {
					if (spells.get(i).equals(spellNo)) {
						retSpells.add(spells.get(i));
						retSpellDescrs.add(spellDescrs.get(i));
					}
				}
			}
			catch (Exception exc) {
				// do nothing
			}
		}
    	SpellList ret = new SpellList("Custom", retSpells, retSpellDescrs);
    	return ret;
    }

    protected void setItems() {
        if (combo == null)
            return;
        if (tableModel == null)
            return;
        SpellList sl = (SpellList) combo.getSelectedItem();
        if (sl == null)
            return;
        sl = getSpellsFromFilters();
        ArrayList<Integer> spells = sl.getSpells();
        ArrayList items = new ArrayList();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g))
            return;
        Turn t = g.getTurn();
        if (t == null)
            return;
        SpellcasterWrapper sw;
        for (Character c : (ArrayList<Character>) t.getContainer(TurnElementsEnum.Character).getItems()) {
            sw = new SpellcasterWrapper();
            //TODO move SpellcasterWrapper creation outside this class
            sw.setCharacter(c.getName());
            sw.setHexNo(c.getHexNo());
            sw.setArtifactBonus(c.getMageTotal() - c.getMage());
            sw.setNationNo(c.getNationNo());
            sw.setMageRank(c.getMage());
            for (int i = 0; i < spells.size(); i++) {
                for (SpellProficiency sp : c.getSpells()) {
                    if (sp.getSpellId() == spells.get(i)) {
                        sw.setProficiency(spells.get(i), sp.getProficiency());
                    }
                }
            }
            if (sw.getProficiencies().size() > 0) {
                items.add(sw);
            }
        }
        ((SpellcasterTableModel) tableModel).getSpells().clear();
        ((SpellcasterTableModel) tableModel).getSpellDescrs().clear();
        for (int i = 0; i < spells.size(); i++) {
            ((SpellcasterTableModel) tableModel).getSpells().add(spells.get(i));
            ((SpellcasterTableModel) tableModel).getSpellDescrs().add(sl.getSpellDescrs().get(i));
        }
        tableModel.setRows(items);
        tableModel.fireTableStructureChanged();
        tableModel.fireTableDataChanged();
        resetColumns();
        // try {
        // for (int i=1; i<11; i++) {
        // table.getColumnModel().getColumn(i+3).setHeaderValue(tableModel.getColumnName(i+3));
        // }
        // } catch (Exception exc) {};
    }
    
    protected void resetFilters() {
    	combo.removeAllItems();
        for (SpellList sl : (ArrayList<SpellList>) createSpellLists()) {
            combo.addItem(sl);
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        super.onApplicationEvent(applicationEvent);
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						resetFilters();
					}
                });
            }
        }
    }

    /**
     * SpellList - acts as a filter
     * Contains an arraylist of integers for the spell ids and an
     * arraylist of strings for the spell names (which are often abbreviated)
     * @author Marios Skounakis
     */
    private class SpellList {

        String name;
        ArrayList<Integer> spells = new ArrayList<Integer>();
        ArrayList<String> spellDescrs = new ArrayList<String>();

        public SpellList(String name, Integer[] spellNos, String[] spellDescrs) {
            this.name = name;
            for (int i = 0; i < spellNos.length; i++) {
                this.spells.add(spellNos[i]);
                this.spellDescrs.add(spellDescrs[i]);
            }
        }
        
        public SpellList(String name, ArrayList<Integer> spellNos, ArrayList<String> spellDescrs) {
            this.name = name;
            this.spells = spellNos;
            this.spellDescrs = spellDescrs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Integer> getSpells() {
            return spells;
        }

        public ArrayList<String> getSpellDescrs() {
            return spellDescrs;
        }

        public String toString() {
            return getName();
        }
    }

    /**
     * SpellList that is derived from one or more actual game Spell Lists
     * You give it the names of the Spell Lists you want and it retrieves the
     * ids and names from the game metadata SpellInfo
     */
    private class SpellListFromSpellMetadata extends SpellList {

        ArrayList<String> spellLists = new ArrayList<String>();;

        public SpellListFromSpellMetadata(String name, String[] spellLists) {
            super(name, new Integer[] {}, new String[] {});
            this.spellLists.addAll(Arrays.asList(spellLists));
            initSpells();
        }

        protected void initSpells() {
            GameMetadata gm = GameHolder.instance().getGame().getMetadata();

            for (String spellList : spellLists) {
                ArrayList<SpellInfo> sis = (ArrayList<SpellInfo>) gm.getSpells().findAllByProperty("list", spellList);
                for (SpellInfo si : sis) {
                    getSpells().add(si.getNumber());
                    getSpellDescrs().add(si.getName());
                }
            }
        }

    }
}
