package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.SpellcasterWrapper;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;

public class SpellcasterListView extends BaseItemListView {
    JComboBox combo;
    
    public SpellcasterListView() {
        super(SpellcasterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{96, 32, 32, 32, 48, 48, 48, 48, 48, 48, 48, 48};
    }
    
    protected ArrayList createSpellLists() {
        ArrayList spellLists = new ArrayList();
        spellLists.add(new SpellList("Artifact/Character tracking", new Integer[]{418, 428, 420, 430}, new String[]{"LA", "LAT", "RC", "RCT"}));
        spellLists.add(new SpellList("Curses", new Integer[]{506, 504, 502}, new String[]{"Curse", "Weak", "Sick"}));
        spellLists.add(new SpellList("Reveal PC", new Integer[]{434}, new String[]{"RPC"}));
        spellLists.add(new SpellList("Scrying", new Integer[]{415}, new String[]{"Scry Area"}));
        spellLists.add(new SpellList("Conjuring", new Integer[]{508, 510, 512}, new String[]{"Cj Mo", "Cj Fo", "Cj Hd"}));
        return spellLists;
    }

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(combo = new JComboBox(createSpellLists().toArray()), "align=left");
        combo.setPreferredSize(new Dimension(200, 24));
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setItems();
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
        for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            SpellcasterWrapper sw = new SpellcasterWrapper();
            sw.setCharacter(c.getName());
            sw.setHexNo(Integer.parseInt(c.getHexNo()));
            sw.setArtifactBonus(c.getMageTotal() - c.getMage());
            sw.setNationNo(c.getNationNo());
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
            for (int i=1; i<9; i++) {
                table.getColumnModel().getColumn(i+3).setHeaderValue(tableModel.getColumnName(i+3));
            }
        } catch (Exception exc) {};
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

}
