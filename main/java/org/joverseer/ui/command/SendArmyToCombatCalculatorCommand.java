package org.joverseer.ui.command;

import org.joverseer.domain.Army;
import org.joverseer.ui.combatCalculator.CombatForm;
import org.joverseer.ui.combatCalculator.CombatFormHolder;
import org.springframework.richclient.command.ActionCommand;


public class SendArmyToCombatCalculatorCommand extends ActionCommand {
    Army army;
    
    public SendArmyToCombatCalculatorCommand(Army army) {
        super("sendArmyToCombatCalculatorCommand");
        this.army = army;
    }

    protected void doExecuteCommand() {
        final CombatForm form = CombatFormHolder.instance().getCombatForm();
        form.addArmy(army);
    }

}
