package org.joverseer.ui.combatCalculator;

import org.joverseer.tools.combatCalc.Combat;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.FormModelHelper;


public class CombatFormHolder {
    CombatForm form;
    
    public void setCombatForm(CombatForm f) {
        this.form = f;
    }
    
    public CombatForm getCombatForm() {
        if (this.form == null) {
            FormModel formModel = FormModelHelper.createFormModel(new Combat());
            this.form = new CombatForm(formModel);
        }
        return this.form;
    }
    
    public static CombatFormHolder instance() {
        return (CombatFormHolder)Application.instance().getApplicationContext().getBean("combatCalculatorHolder");
    }

}
