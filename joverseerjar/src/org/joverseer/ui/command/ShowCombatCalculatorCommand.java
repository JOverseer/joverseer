package org.joverseer.ui.command;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.combatCalculator.CombatForm;
import org.joverseer.ui.combatCalculator.CombatFormHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;

/**
 * Shows the combat calculator
 * 
 * @author Marios Skounakis
 */
public class ShowCombatCalculatorCommand extends ActionCommand {
    Combat combat;
    
    public ShowCombatCalculatorCommand() {
        super("showCombatCalculatorCommand");
        this.combat = null;
        
    }
    
    public ShowCombatCalculatorCommand(Combat combat) {
        super("showCombatCalculatorCommand");
        this.combat = combat;
    }
    

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (this.combat == null) {
            this.combat = new Combat();
            this.combat.setMaxRounds(10);
            g.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(this.combat);
        }
        final CombatForm form = CombatFormHolder.instance().getCombatForm();
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                form.setFormObject(ShowCombatCalculatorCommand.this.combat);
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));
                return true;
            }
            
            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        dialog.setTitle(Messages.getString("combatCalculator.title"));
        dialog.setModal(false);
        dialog.showDialog();
    }
    
}
