package org.joverseer.ui.listviews;

import javax.swing.JPopupMenu;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.command.ShowCombatCalculatorCommand;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;

/**
 * List view for Combats for the Combat Calc
 *
 * @author Marios Skounakis
 */
public class CombatCalcCombatListView extends ItemListView {

    public CombatCalcCombatListView() {
        super(TurnElementsEnum.CombatCalcCombats, CombatCalcCombatTableModel.class);
    }

    @Override
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

    private Combat getSelectedCombat()
    {
    	Combat c=null;
        try {
        	c = (Combat) this.getSelectedObject();
        } catch (Exception exc) {
        	// do nothing;
        }
        return c;
    }

    class AddCombatCommand extends ActionCommand {
        @Override
		protected void doExecuteCommand() {
            Combat c = new Combat();
            c.setMaxRounds(20);
            new ShowCombatCalculatorCommand(c,CombatCalcCombatListView.this.gameHolder).execute();
            CombatCalcCombatListView.this.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(c);
            setItems();
        }

    }

    class EditSelectedCombatCommand extends ActionCommand {
        @Override
		protected void doExecuteCommand() {
            Combat c = CombatCalcCombatListView.this.getSelectedCombat();
            if (c != null) {
            	new ShowCombatCalculatorCommand(c,CombatCalcCombatListView.this.gameHolder).execute();
            }
        }
    }

    class DeleteSelectedCombatCommand extends ActionCommand {
        @Override
		protected void doExecuteCommand() {
            Combat c = CombatCalcCombatListView.this.getSelectedCombat();
            if (c != null) {
            	CombatCalcCombatListView.this.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).removeItem(c);
                setItems();
            }
        }
    }

}
