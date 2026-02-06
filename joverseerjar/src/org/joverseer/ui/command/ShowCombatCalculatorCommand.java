package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.combatCalculator.CombatForm;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;

/**
 * Shows the combat calculator
 *
 * @author Marios Skounakis
 */
public class ShowCombatCalculatorCommand extends ActionCommand {
    Combat combat;
    //dependencies
    GameHolder gameHolder;

    public ShowCombatCalculatorCommand(GameHolder gameHolder) {
        super("showCombatCalculatorCommand");
        this.combat = null;
        this.gameHolder = gameHolder;
    }

    public ShowCombatCalculatorCommand(Combat combat,GameHolder gameHolder) {
        super("showCombatCalculatorCommand");
        this.combat = combat;
        this.gameHolder = gameHolder;
    }


    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = this.gameHolder.getGame();
        if (this.combat == null) {
            this.combat = new Combat();
            this.combat.setMaxRounds(10);
            g.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(this.combat);
        }
        final CombatForm form = JOApplication.getCombatFormHolder().getCombatForm();
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                form.setFormObject(ShowCombatCalculatorCommand.this.combat);
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] { 
//                new ActionCommand("showLog") { //$NON-NLS-1$
//					@Override
//					protected void doExecuteCommand() {
//						form.openLog();
//					}
//				}, 
                new ActionCommand("refreshCombat") { //$NON-NLS-1$
					@Override
					protected void doExecuteCommand() {
						form.refreshCombat();
					}
				}, 
                        getFinishCommand()
                };
            }
        };
        dialog.setTitle(Messages.getString("combatCalculator.title"));
        dialog.setModal(false);
        dialog.showDialog();
    }

}
