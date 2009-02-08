package org.joverseer.ui.listviews;

import javax.swing.JPopupMenu;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.command.ShowCombatCalculatorCommand;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.table.SortableTableModel;

/**
 * List view for Combats for the Combat Calc
 * 
 * @author Marios Skounakis
 */
public class CombatCalcCombatListView extends ItemListView {

    public CombatCalcCombatListView() {
        super(TurnElementsEnum.CombatCalcCombats, CombatCalcCombatTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{250, 48, 150, 150};
    }

    @Override
    public JPopupMenu getPopupMenu(boolean hasSelectedItem) {
    	if (hasSelectedItem) {
	        CommandGroup cg = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
	                "combatPopupMenu",
	                new Object[]{
	                    new AddCombatCommand(),
	                    new EditSelectedCombatCommand(),
	                    new DeleteSelectedCombatCommand()
	                }
	            );
	        return cg.createPopupMenu();
    	} else {
    		CommandGroup cg = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
	                "combatPopupMenu",
	                new Object[]{
	                    new AddCombatCommand(),
	                }
	            );
	        return cg.createPopupMenu();
    	}
    }

    class AddCombatCommand extends ActionCommand {
        protected void doExecuteCommand() {
            Combat c = new Combat();
            c.setMaxRounds(20);
            new ShowCombatCalculatorCommand(c).execute();
            GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(c);
            setItems();
        }
        
    }
    
    class EditSelectedCombatCommand extends ActionCommand {
        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Combat c = (Combat) obj;
                    new ShowCombatCalculatorCommand(c).execute();
                } catch (Exception exc) {

                }
            }
        }
        
    }
    
    class DeleteSelectedCombatCommand extends ActionCommand {
        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Combat c = (Combat) obj;
                    GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).removeItem(c);
                    setItems();
                } catch (Exception exc) {

                }
            }
        }
        
    }

}
