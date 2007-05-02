package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationMessage;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.TrackCharacterInfo;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;


public class TrackCharacterListView extends BaseItemListView {
    JTextField character;
    protected SelectRowCommandExecutor selectRowCommandExecutor = new SelectRowCommandExecutor();
    
    
    public TrackCharacterListView() {
        super(TrackCharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 64, 400};
    }
    
    
    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{
                new ColumnToSort(0, 0),
                new ColumnToSort(0, 1)
        };
    }

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Character : "), "colspec=left:80px");
        tlb.gapCol();
        tlb.cell(character = new JTextField(), "colspec=left:150px");
        character.setPreferredSize(new Dimension(200, 20));
        character.setDragEnabled(true);
        character.setOpaque(true);
        
        JButton btn = new JButton("Track");
        btn.setPreferredSize(new Dimension(70, 20));
        tlb.gapCol();
        tlb.cell(btn, "align=left");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setItems();
            }
        });
        tlb.row();
        tlb.relatedGapRow();
        tlb.cell(tableComp);
        tlb.row();
        return tlb.getPanel();
    }
    
    private Character findChar(Turn t, String name) {
        String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
        if (pv == null || pv.equals("accented")) {
            Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name);
            return c;
        } else {
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                if (AsciiUtils.convertNonAscii(c.getName()).toLowerCase().equals(name.toLowerCase())) {
                    return c;
                }
            }
        }
        return null;
    }
    
    private Army findInArmies(Turn t, String name) {
        String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
        if (pv == null || pv.equals("accented")) {
            return (Army)t.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", name);
        } else {
            for (Army a : (ArrayList<Army>)t.getContainer(TurnElementsEnum.Army).getItems()) {
                if (AsciiUtils.convertNonAscii(a.getCommanderName()).toLowerCase().equals(name.toLowerCase())) {
                    return a;
                }
            }
        }
        return null;
    }

    protected void setItems() {
        ArrayList items = new ArrayList();
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return;
        String charName = character.getText();
        if (!charName.equals("")) {
	        for (Turn t : (ArrayList<Turn>)g.getTurns().getItems()) {
	            // find in characters
                    Character c = findChar(t, charName);
	            if (c != null) {
	                TrackCharacterInfo tci = new TrackCharacterInfo();
	                tci.setTurnNo(t.getTurnNo());
	                tci.setInfo(String.format("Character was located at %s.", c.getHexNo()));
	                tci.setHexNo(c.getHexNo());
	                items.add(tci);
	                if (c.getOrderResults() != null && !c.getOrderResults().equals("")) {
	                    tci = new TrackCharacterInfo();
	                    tci.setTurnNo(t.getTurnNo());
	                    tci.setInfo(c.getOrderResults());
	                    tci.setHexNo(c.getHexNo());
	                    items.add(tci);
	                }
                        if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
                            tci = new TrackCharacterInfo();
                            tci.setTurnNo(t.getTurnNo());
                            tci.setInfo("Character died (" + c.getDeathReason().toString() + ").");
                            tci.setHexNo(c.getHexNo());
                            items.add(tci);
                        }
	            }
	            // find in armies
	            Army a = findInArmies(t, charName);
	            if (a != null) {
	                TrackCharacterInfo tci = new TrackCharacterInfo();
	                tci.setTurnNo(t.getTurnNo());
	                tci.setInfo(String.format("Character was leading an army at %s.", a.getHexNo()));
	                tci.setHexNo(Integer.parseInt(a.getHexNo()));
	                items.add(tci);
	            }
	            // find in rumors
	            for (NationMessage nm : (ArrayList<NationMessage>)t.getContainer(TurnElementsEnum.NationMessage).getItems()) {
                        boolean found = false;
                        String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
                        if (pv == null || pv.equals("accented")) {
                            found = nm.getMessage().indexOf(charName) >= 0; 
                        } else {
                            found = AsciiUtils.convertNonAscii(nm.getMessage()).toLowerCase().indexOf(charName.toLowerCase()) >= 0;
                        }
	                if (found) {
	                    TrackCharacterInfo tci = new TrackCharacterInfo();
	                    tci.setTurnNo(t.getTurnNo());
	                    tci.setInfo(nm.getMessage());
	                    if (nm.getX() > 0) {
	                        tci.setHexNo(nm.getX() * 100 + nm.getY());
	                    } else {
	                        tci.setHexNo(0);
	                    }
	                    items.add(tci);
	                }
	            }
	            // find in LA/LAT results
	            Container artis = t.getContainer(TurnElementsEnum.Artifact);
	            for (Artifact arti : (ArrayList<Artifact>)artis.getItems()) {
                        boolean found = false;
                        String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
                        if (pv == null || pv.equals("accented")) {
                            found = arti.getOwner().indexOf(charName) >= 0;
                        } else {
                            found = AsciiUtils.convertNonAscii(arti.getOwner()).toLowerCase().indexOf(charName.toLowerCase()) >= 0;
                        }
	            	if (found) {
	            	    TrackCharacterInfo tci = new TrackCharacterInfo();
	                    tci.setTurnNo(t.getTurnNo());
	                    tci.setInfo(arti.getOwner() + " possesses #" + arti.getNumber() + " " + arti.getName());
	                    tci.setHexNo(arti.getHexNo());
	                    items.add(tci);
	            	}
	            }
	        }
            tableModel.setRows(items);
            tableModel.fireTableDataChanged();
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == 1) {
            selectRowCommandExecutor.execute();
        }
        if (e.getClickCount() == 1 && e.getButton() == 3) {
            showContextMenu(e);
        }
    }
    
    private class SelectRowCommandExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount()) return;
                try {
                    Object obj = tableModel.getRow(idx);
                    TrackCharacterInfo tci = (TrackCharacterInfo)obj;
                    if (tci.getHexNo() > 0) {
                        Point selectedHex = new Point(tci.getX(), tci.getY());
                        Application.instance().getApplicationContext().publishEvent(
                                new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
                    }
                    Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                    g.setCurrentTurn(tci.getTurnNo());
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
                    
                }
                catch (Exception exc) {
                    // do nothing
                }
            }
        }
    }

}
