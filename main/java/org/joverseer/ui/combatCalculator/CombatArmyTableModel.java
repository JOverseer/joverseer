package org.joverseer.ui.combatCalculator;

import org.joverseer.domain.ArmyElement;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.springframework.context.MessageSource;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.table.BeanTableModel;


public class CombatArmyTableModel extends BeanTableModel {
    public static int iTroops = 2;
    public static int iStr = 3;
    public static int iCon = 4;
    public static int iLosses = 5;
    
    Form parentForm;

    public CombatArmyTableModel(Form parentForm, MessageSource arg1) {
        super(CombatArmy.class, arg1);
        this.parentForm = parentForm; 
        setRowNumbers(false);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"commander", "nationNo", "troops", "strength", "constitution", "losses"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, String.class};
    }
    
    public int[] getColumnWidths() {
        return new int[]{100, 48, 200, 64, 64, 64};
    }
    
    protected Combat getCombat() {
        return (Combat)parentForm.getFormObject();
    }

    protected Object getValueAtInternal(Object arg0, int arg1) {
        CombatArmy ca = (CombatArmy)arg0;
        if (arg1 == iTroops) {
            String ret = "";
            for (ArmyElement ae : ca.getElements()) {
                if (ae.getNumber() > 0) {
                    ret += (ret.equals("") ? "" : " ") + 
                    (int)Math.round(ae.getNumber() * (100 - ca.getLosses()) / 100) + ae.getArmyElementType().getType();
                }
            }
            int eHI = CombatUtils.getNakedHeavyInfantryEquivalent(new CombatArmy(ca));
            if (eHI > 0) ret += " (" + eHI + "enHI)";
            return ret;
        } else if (arg1 == iStr) {
            if (getCombat() == null) {
                return 0;
            } else {
                return getCombat().computeNativeArmyStrength(ca, getCombat().getTerrain(), getCombat().getClimate(), 0d, false);
            }
        } else if (arg1 == iCon) {
            if (getCombat() == null) {
                return 0;
            } else {
                return getCombat().computNativeArmyConstitution(ca, 0d);
            }
        } else if (arg1 == iCon) {
            if (getCombat() == null) {
                return 0;
            } else {
                return (int)Math.round(ca.getLosses());
            }
        } else if (arg1 == iLosses) {
            return Math.round(ca.getLosses()) + "%";
        }
        return super.getValueAtInternal(arg0, arg1);
    }

    protected boolean isCellEditableInternal(Object arg0, int arg1) {
        return false;
    }
    
    

    
}
