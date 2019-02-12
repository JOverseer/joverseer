package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.combatCalculator.CombatForm;
import org.joverseer.ui.combatCalculator.CombatFormHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
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
                joApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
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
