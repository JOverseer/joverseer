package org.joverseer.ui.listviews;

import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.springframework.context.MessageSource;

/**
 * Table model for Combats for the Combat Calc
 * 
 * @author Marios Skounakis
 */
public class CombatCalcCombatTableModel extends ItemTableModel {
	private static final long serialVersionUID = 1L;
	public static int iSide1 = 2;
	public static int iSide2 = 3;

	public CombatCalcCombatTableModel(MessageSource messageSource) {
		super(Combat.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "description", "hexNo", "side1", "side2" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class };
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (i == iSide1) {
			Combat c = (Combat) object;
			String commanders = "";
			for (CombatArmy ca : c.getSide1()) {
				if (ca == null)
					continue;
				String cn = ca.getCommander();
				if (cn == null) {
					cn = "";
				}
				commanders += (commanders.equals("") ? "" : ", ") + cn;
			}
			return commanders;
		} else if (i == iSide2) {
			Combat c = (Combat) object;
			String commanders = "";
			for (CombatArmy ca : c.getSide2()) {
				if (ca == null)
					continue;
				String cn = ca.getCommander();
				if (cn == null) {
					cn = "";
				}
				commanders += (commanders.equals("") ? "" : ", ") + cn;
			}
			return commanders;
		}
		return super.getValueAtInternal(object, i);
	}

}
