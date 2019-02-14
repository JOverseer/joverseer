package org.joverseer.ui.economyCalculator;

import org.joverseer.JOApplication;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Table model for population centers for the Economy Calculator
 * 
 * @author Marios Skounakis
 */
public class LostPopsTableModel extends BeanTableModel {

	private static final long serialVersionUID = 1L;
	int selectedNationNo = -1;
	final static int iHex=1;

	public LostPopsTableModel() {
		super(PopulationCenter.class, Messages.getMessageSource());
		setRowNumbers(false);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "name", "hexNo", "size", "fortification", "loyalty", "lostThisTurn" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, Boolean.class };
	}

	public int getSelectedNationNo() {
		return this.selectedNationNo;
	}

	public void setSelectedNationNo(int selectedNationNo) {
		this.selectedNationNo = selectedNationNo;
	}

	@Override
	protected boolean isCellEditableInternal(Object arg0, int arg1) {
		return arg1 == 5;
	}

	@Override
	protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
		super.setValueAtInternal(arg0, arg1, arg2);
		JOApplication.publishEvent(LifecycleEventsEnum.EconomyCalculatorUpdate, this, this);

	}

	@Override
	protected Object getValueAtInternal(Object row, int columnIndex) {
		Object v = super.getValueAtInternal(row, columnIndex);
		if (columnIndex == 2) {
			return UIUtils.enumToString(v);
		} else if (columnIndex == 3) {
			return UIUtils.enumToString(v);
		}
		return v;
	}

}
