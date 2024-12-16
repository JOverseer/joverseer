package org.joverseer.ui.combatCalculator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.joverseer.domain.ArmyElement;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.context.MessageSource;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;

public class CombatArmyTableModel extends BeanTableModel {
	private static final long serialVersionUID = 1L;
	public static int iTroops = 2;
	public static int iStr = 3;
	public static int iCon = 4;
	public static int iWarM = 5;
	public static int iStrOfAt = 6;	
	public static int iLosses = 7;
	public static int iTactic = 8;

	Form parentForm;

	public CombatArmyTableModel(Form parentForm, MessageSource arg1) {
		super(CombatArmy.class, arg1);
		this.parentForm = parentForm;
		setRowNumbers(false);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "commander", "nationNo", "troops", "strength", "constitution", "warmachines", "strengthOfAttackingArmies", "losses", "tactic" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	}

	public int[] getColumnWidths() {
		return new int[] { 100, 48, 200, 64, 64, 32, 64, 42, 64 };
	}

	protected Combat getCombat() {
		return (Combat) this.parentForm.getFormObject();
	}

	@Override
	protected Object getValueAtInternal(Object arg0, int arg1) {
		CombatArmy ca = (CombatArmy) arg0;
		if (arg1 == iTroops) {
			String ret = "";
			for (ArmyElement ae : ca.getElements()) {
				if(ae.getArmyElementType().getType().equals("WM")) continue;
				if (ae.getNumber() > 0) {
					ret += (ret.equals("") ? "" : " ") + (int) Math.round(ae.getNumber() * (100 - ca.getLosses()) / 100) + ae.getArmyElementType().getType();
				}
			}
			int eHI = CombatUtils.getNakedHeavyInfantryEquivalent(new CombatArmy(ca));
			if (eHI > 0)
				ret += " (" + eHI + "enHI)";
			return ret;
		} else if (arg1 == iStr) {
			if (getCombat() == null) {
				return 0;
			} else {
				getCombat();
				return Combat.computeNativeArmyStrength(ca, getCombat().getTerrain(), getCombat().getClimate(), 0d, true);
			}
		} else if (arg1 == iCon) {
			if (getCombat() == null) {
				return 0;
			} else {
				getCombat();
				return Combat.computNativeArmyConstitution(ca, 0d);
			}
		} else if (arg1 == iCon) {
			if (getCombat() == null) {
				return 0;
			} else {
				return (int) Math.round(ca.getLosses());
			}
		} else if (arg1 == iWarM) {
			if(ca.getWM().equals(null)) return 0;
			return ca.getWM().getNumber();
			
		} else if (arg1 == iStrOfAt) {
			if(getCombat() == null) return 0;
			return ca.getStrOfAttackingArmy();
		} else if (arg1 == iLosses) {
			return Math.round(ca.getLosses()) + "%";
		}
		return super.getValueAtInternal(arg0, arg1);
	}

	@Override
	protected boolean isCellEditableInternal(Object arg0, int arg1) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
	}
	
	protected void startDragAndDropAction(MouseEvent e, JTable table, int side) {
		final CombatArmy[] selectedArmies = new CombatArmy[table.getSelectedRowCount()];
		String copyString = "";
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			int id = table.getSelectedRows()[i];
			int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(id);
			CombatArmy a = (CombatArmy) this.getRow(idx);
			selectedArmies[i] = a;
			String ln = "";
			for (int j = 0; j < table.getColumnCount(); j++) {
				Object v = table.getValueAt(i, j);
				if (v == null)
					v = "";
				ln += UIUtils.OptTab(ln, v.toString());
			}
			copyString += UIUtils.OptNewLine(copyString,ln);
		}
		final String str = copyString;
		System.out.println("Copied String:");	//These prints helped reduced bugs...
		System.out.println(str);

		TransferHandler handler = new GenericExportTransferHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Transferable createTransferable(JComponent arg0) {
				try {
					Transferable t = new GenericTransferable(new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + CombatArmy[].class.getName() + "\""), new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + CombatArmy.class.getName()), DataFlavor.stringFlavor }, new Object[] { selectedArmies, selectedArmies[0], str });
					return t;
				} catch (Exception exc) {
					exc.printStackTrace();
					return null;
				}

			}
		};
		System.out.println("Before Tranfer handler");
		table.setTransferHandler(handler);
		handler.exportAsDrag(table, e, TransferHandler.COPY);
		System.out.println("After Export");
	}
}
