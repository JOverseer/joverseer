package org.joverseer.ui.listviews;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.command.SendArmyToCombatCalculatorCommand;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.support.transferHandlers.CharIdTransferHandler;
import org.joverseer.ui.support.transferHandlers.DragAndDropMouseInputHandler;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;
import org.joverseer.ui.support.transferHandlers.StringTransferHandler;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.table.SortableTableModel;

public class ArmyListView extends ItemListView {

    public ArmyListView() {
        super(TurnElementsEnum.Army, ArmyTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {48, 64, 120, 64, 120, 48, 48, 48, 150};
    }

    protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][] {NationFilter.createNationFilters()};
    }

    public JPopupMenu getPopupMenu() {
        int idx = table.getSelectedRow();
        if (idx == -1) return null;
        idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(idx);
        Army a = (Army)tableModel.getRow(idx);
        CommandGroup cg = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "contextMenu", 
                new Object[]{
                        new SendArmyToCombatCalculatorCommand(a)
                });
        return cg.createPopupMenu();
    }

}
