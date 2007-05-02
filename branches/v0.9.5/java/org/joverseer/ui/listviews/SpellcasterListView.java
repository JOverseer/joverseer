package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.SpellcasterWrapper;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;

public class SpellcasterListView extends BaseItemListView {
    JComboBox combo;
    
    public SpellcasterListView() {
        super(SpellcasterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{96, 32, 32, 32, 32, 62, 62, 62, 62, 62, 62, 62, 62, 140};
    }
    
    protected ArrayList createSpellLists() {
        ArrayList spellLists = new ArrayList();
        spellLists.add(new SpellList("Artifact/Character tracking", new Integer[]{418, 428, 420, 430}, new String[]{"LA", "LAT", "RC", "RCT"}));
        spellLists.add(new SpellList("Healing", new Integer[]{2, 4, 6, 8}, new String[]{"Minor Heal", "Major Heal", "Great. Heal", "Heal True"}));
        //spellLists.add(new SpellListFromSpellMetadata("Movement", new String[]{"Movement Mastery", "Return Mastery", "Teleport"}));
        spellLists.add(new SpellList("Movement", new Integer[]{302, 304, 306, 308, 310, 312, 314}, 
        											new String[]{"Long Strd", "Fast Strd", "Path Mstr", "Capital Ret", "Major Ret", "Ret True", "Teleport"}));
        //spellLists.add(new SpellListFromSpellMetadata("Defense", new String[]{"Barrier Mastery", "Resistance Mastery"}));
        spellLists.add(new SpellList("Defense", new Integer[]{102, 106, 112, 114, 104, 108, 110, 116}, 
												new String[]{"Barriers", "Deflect.", "Shields", "Barr. Walls", "Resistances", "Blessings", "Protect.", "Force Walls"}));
        //spellLists.add(new SpellListFromSpellMetadata("Fire Mastery", new String[]{"Fire Mastery"}));
        spellLists.add(new SpellList("Fire Mastery", new Integer[]{202, 204, 206, 232, 234, 236, 240}, 
				new String[]{"Call Fire", "Wild Flames", "Wall of Fire", "Fire Bolts", "Fire Balls", "Fire Storms", "Smn Fire Sprts"}));
        //spellLists.add(new SpellListFromSpellMetadata("Word Mastery", new String[]{"Word Mastery"}));
        spellLists.add(new SpellList("Words of Pain", new Integer[]{208, 210, 212, 220, 222, 224, 242}, 
				new String[]{"Pain", "Calm", "Paralysis", "Agony", "Stun", "Command", "Death"}));
        //spellLists.add(new SpellListFromSpellMetadata("Wind Mastery", new String[]{"Wind Mastery"}));
        spellLists.add(new SpellList("Wind Mastery", new Integer[]{214, 216, 218, 226, 228, 230, 238}, 
				new String[]{"Call Winds", "Wild Winds", "Wall of Wind", "Chill Bolts", "Frost Balls", "Wind Storms", "Smn Wind Sprts"}));
        //spellLists.add(new SpellListFromSpellMetadata("Dark Summons", new String[]{"Dark Summons"}));
        spellLists.add(new SpellList("Dark Summons", new Integer[]{244, 246, 248}, 
				new String[]{"Frfl Hearts", "Smn Storms", "Fanaticism"}));
        //spellLists.add(new SpellListFromSpellMetadata("Conjuring Ways", new String[]{"Conjuring Ways"}));
        spellLists.add(new SpellList("Conjuring Ways", new Integer[]{508, 510, 512}, 
				new String[]{"Mounts", "Food", "Hordes"}));
        spellLists.add(new SpellList("Spirit Mastery", new Integer[]{502, 504, 506}, 
				new String[]{"Weakness", "Sickness", "Curses"}));
        //spellLists.add(new SpellListFromSpellMetadata("Spirit Mastery", new String[]{"Spirit Mastery"}));
        //spellLists.add(new SpellListFromSpellMetadata("Lore Spells", new String[]{"Lore Spells"}));
        spellLists.add(new SpellList("Lore Spells", new Integer[]{402, 404, 408, 422, 424, 432}, 
				new String[]{"Allegiance", "Relations", "Nationality", "Power", "Mission", "Secrets"}));
        //spellLists.add(new SpellListFromSpellMetadata("Divinations", new String[]{"Divinations"}));
        spellLists.add(new SpellList("Divinations", new Integer[]{406, 410, 417, 419, 426}, 
				new String[]{"Army", "Algnce Forces", "Character", "Nation", "Army True"}));
        //spellLists.add(new SpellListFromSpellMetadata("Artifact Mastery", new String[]{"Artifact Mastery"}));
        spellLists.add(new SpellList("Artifact Mastery", new Integer[]{412, 418, 428}, 
				new String[]{"RA", "LA", "LAT"}));
        //spellLists.add(new SpellListFromSpellMetadata("Scrying & Hidden Visions", new String[]{"Scrying", "Hidden Visions"}));
        spellLists.add(new SpellList("Scrying & Hidden Visions", new Integer[]{413, 414, 415, 436, 416, 420, 430, 434}, 
				new String[]{"Scry PC", "Scry Hex", "Scry Area", "Scry Char", "Rev Prod", "Rev Char", "Rev Char True", "Rev PC"}));
        return spellLists;
    }
    
    

    @Override
    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{
                new ColumnToSort(0, 2),
                new ColumnToSort(1, 0)
        };
    }

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(combo = new JComboBox(), "align=left");
        combo.setPreferredSize(new Dimension(200, 24));
        combo.setOpaque(true);
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setItems();
            }
        });
        JTableHeader header = table.getTableHeader();
        header.addMouseMotionListener(new MouseMotionAdapter() {
        	TableColumn curCol;
        	
        	public void mouseMoved(MouseEvent evt) {
                TableColumn col = null;
                JTableHeader header = (JTableHeader)evt.getSource();
                JTable table = header.getTable();
                TableColumnModel colModel = table.getColumnModel();
                int vColIndex = colModel.getColumnIndexAtX(evt.getX());
        
                // Return if not clicked on any column header
                if (vColIndex >= 0) {
                    col = colModel.getColumn(vColIndex);
                }
        
                if (col != curCol) {
                	String toolTip = "";
                	SpellList sl = (SpellList)combo.getSelectedItem();
                	if (sl != null) {
                		if (vColIndex - 5 >= 0 && vColIndex - 5 < sl.getSpells().size()) {
                			int spellId = sl.getSpells().get(vColIndex - 5);
	                		SpellInfo si = (SpellInfo)GameHolder.instance().getGame().getMetadata().getSpells().findFirstByProperty("number", spellId);
	                		if (si == null) {
	                			toolTip = String.valueOf(spellId);
	                		} else {
	                			toolTip = si.getNumber() + " - " + si.getName() + ": " + si.getDescription();
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
    
    protected void setItems() {
        if (combo == null) return;
        SpellList sl = (SpellList)combo.getSelectedItem();
        if (sl == null) return;
        ArrayList<Integer> spells = sl.getSpells();
        ArrayList items = new ArrayList();
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return;
        Turn t = g.getTurn();
        if (t == null) return;
        SpellcasterWrapper sw;
        for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            sw = new SpellcasterWrapper();
            sw.setCharacter(c.getName());
            sw.setHexNo(c.getHexNo());
            sw.setArtifactBonus(c.getMageTotal() - c.getMage());
            sw.setNationNo(c.getNationNo());
            sw.setMageRank(c.getMage());
            for (int i=0; i<spells.size(); i++) {
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
        ((SpellcasterTableModel)tableModel).getSpells().clear();
        ((SpellcasterTableModel)tableModel).getSpellDescrs().clear();
        for (int i=0; i<spells.size(); i++) {
            ((SpellcasterTableModel)tableModel).getSpells().add(spells.get(i));
            ((SpellcasterTableModel)tableModel).getSpellDescrs().add(sl.getSpellDescrs().get(i));
        }
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
        try {
            for (int i=1; i<11; i++) {
                table.getColumnModel().getColumn(i+3).setHeaderValue(tableModel.getColumnName(i+3));
            }
        } catch (Exception exc) {};
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
    	super.onApplicationEvent(applicationEvent);
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) 
            {
            	combo.removeAllItems();
            	for (SpellList sl : (ArrayList<SpellList>)createSpellLists()) {
            		combo.addItem(sl);
            	}
            }
        }
    }
    
    private class SpellList {
        String name;
        ArrayList<Integer> spells = new ArrayList<Integer>();
        ArrayList<String> spellDescrs = new ArrayList<String>();
        
        public SpellList(String name, Integer[] spellNos, String[] spellDescrs) {
            this.name = name;
            for (int i=0; i<spellNos.length; i++) {
                this.spells.add(spellNos[i]);
                this.spellDescrs.add(spellDescrs[i]);
            }
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
    
    private class SpellListFromSpellMetadata extends SpellList {
    	ArrayList<String> spellLists = new ArrayList<String>();;
    	
    	public SpellListFromSpellMetadata(String name, String[] spellLists) {
    		super(name, new Integer[]{}, new String[]{});
    		this.spellLists.addAll(Arrays.asList(spellLists));
    		initSpells();
    	}
    	
    	protected void initSpells() {
    		GameMetadata gm = GameHolder.instance().getGame().getMetadata();
    		
    		for (String spellList : spellLists) {
    			ArrayList<SpellInfo> sis = (ArrayList<SpellInfo>)gm.getSpells().findAllByProperty("list", spellList);
    			for (SpellInfo si : sis) {
    				getSpells().add(si.getNumber());
    				getSpellDescrs().add(si.getName());
    			}
    		}
    	}
    	
    }
}
