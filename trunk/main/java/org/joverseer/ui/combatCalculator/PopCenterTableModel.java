package org.joverseer.ui.combatCalculator;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.springframework.context.MessageSource;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.table.BeanTableModel;


public class PopCenterTableModel extends BeanTableModel {
    public static int iDefense = 6;
    public static int iAttackerStr = 5;
    
    Form parentForm;
    
    public PopCenterTableModel(Form parentForm, MessageSource arg1) {
        super(CombatPopCenter.class, arg1);
        this.parentForm = parentForm; 
        setRowNumbers(false);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"name", "nationNo", "loyalty", "size", "fort", "strengthOfAttackingArmies", "defense", "capturedStr"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
    }
    
    public int[] getColumnWidths() {
        return new int[]{60, 48, 48, 60, 60, 60, 60, 48};
    }

    protected Combat getCombat() {
        return (Combat)parentForm.getFormObject();
    }
    
    protected Object getValueAtInternal(Object arg0, int arg1) {
        CombatPopCenter pc = (CombatPopCenter)arg0;
        if (arg1 == iDefense) {
            return getCombat().computePopCenterStrength(pc);
        } else {
            return super.getValueAtInternal(arg0, arg1);
        }
    }
    
    protected boolean isCellEditableInternal(Object arg0, int arg1) {
        return false;
    }
}