package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.combatCalculator.CombatForm;
import org.joverseer.ui.combatCalculator.CombatFormHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.views.EditNationMetadataForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class ShowCombatCalculatorCommand extends ActionCommand {
    Combat combat;
    
    public ShowCombatCalculatorCommand() {
        super("showCombatCalculatorCommand");
        combat = new Combat();
        combat.setMaxRounds(10);
    }
    
    public ShowCombatCalculatorCommand(Combat combat) {
        super("showCombatCalculatorCommand");
        this.combat = combat;
    }
    

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        final CombatForm form = CombatFormHolder.instance().getCombatForm();
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
                form.setFormObject(combat);
            }

            protected boolean onFinish() {
                form.commit();
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("combatCalculator.title", new Object[]{}, Locale.getDefault()));
        dialog.setModal(false);
        dialog.showDialog();
    }
    
}