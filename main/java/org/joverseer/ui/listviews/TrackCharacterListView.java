package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationMessage;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.TrackCharacterInfo;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.layout.TableLayoutBuilder;
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
    
    
    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(character = new JTextField(), "align=left");
        character.setPreferredSize(new Dimension(200, 24));
        character.addActionListener(new ActionListener() {
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
        ArrayList items = new ArrayList();
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return;
        String charName = character.getText();
        for (Turn t : (ArrayList<Turn>)g.getTurns().getItems()) {
            // find in characters
            Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", charName);
            if (c != null) {
                TrackCharacterInfo tci = new TrackCharacterInfo();
                tci.setTurnNo(t.getTurnNo());
                tci.setInfo(String.format("Character was located at %s.", c.getHexNo()));
                tci.setHexNo(Integer.parseInt(c.getHexNo()));
                items.add(tci);
                if (c.getOrderResults() != null && !c.getOrderResults().equals("")) {
                    tci = new TrackCharacterInfo();
                    tci.setTurnNo(t.getTurnNo());
                    tci.setInfo(c.getOrderResults());
                    tci.setHexNo(Integer.parseInt(c.getHexNo()));
                    items.add(tci);
                }
            }
            // find in armies
            Army a = (Army)t.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", charName);
            if (a != null) {
                TrackCharacterInfo tci = new TrackCharacterInfo();
                tci.setTurnNo(t.getTurnNo());
                tci.setInfo(String.format("Character was leading an army at %s.", a.getHexNo()));
                tci.setHexNo(Integer.parseInt(a.getHexNo()));
                items.add(tci);
            }
            // find in rumors
            for (NationMessage nm : (ArrayList<NationMessage>)t.getContainer(TurnElementsEnum.NationMessage).getItems()) {
                if (nm.getMessage().indexOf(charName) >= 0) {
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
            tableModel.setRows(items);
            tableModel.fireTableDataChanged();
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == 1) {
            selectRowCommandExecutor.execute();
        }
        if (e.getClickCount() == 1 && e.getButton() == 3) {
            showContextMenu();
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
