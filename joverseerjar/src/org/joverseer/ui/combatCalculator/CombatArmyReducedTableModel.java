package org.joverseer.ui.combatCalculator;

import org.springframework.context.MessageSource;
import org.springframework.richclient.form.Form;

public class CombatArmyReducedTableModel extends CombatArmyTableModel {

	public CombatArmyReducedTableModel(Form parentForm, MessageSource arg1) {
		super(parentForm, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "commander", "nationNo", "troops"};
	}
	
	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class};
	}
	
	@Override
	public int[] getColumnWidths() {
		return new int[] { 100, 48, 150};
	}
	
//	@Override
//	protected Object getValueAtInternal(Object arg0, int arg1) {
//		if(arg1 == iStr || arg1 == iCon || arg1 == iLosses) {
//			return null
//		}
//	}
	
	

}
