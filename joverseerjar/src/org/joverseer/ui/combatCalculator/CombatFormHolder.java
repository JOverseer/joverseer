package org.joverseer.ui.combatCalculator;

import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.FormModelHelper;


public class CombatFormHolder {
    CombatForm form;

    //injected dependencies
    GameHolder gameHolder;

    public CombatFormHolder(GameHolder gameHolder) {
        this.gameHolder = gameHolder;
    }
    public void setCombatForm(CombatForm f) {
        this.form = f;
    }

    public CombatForm getCombatForm() {
        if (this.form == null) {
            FormModel formModel = FormModelHelper.createFormModel(new Combat());
            this.form = new CombatForm(formModel,this.gameHolder);
        }
        return this.form;
    }

}
