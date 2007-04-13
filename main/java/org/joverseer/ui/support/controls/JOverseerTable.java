package org.joverseer.ui.support.controls;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class JOverseerTable extends javax.swing.JTable {
	boolean overwriteOnCellEdit = true;
	
	public JOverseerTable() {
		super();
                setSurrendersFocusOnKeystroke(true);
	}

	public JOverseerTable(TableModel dm) {
		super(dm);
                setSurrendersFocusOnKeystroke(true);
	}

	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component c = super.prepareEditor(editor, row, column);
		if (getOverwriteOnCellEdit()) {
			if (JTextField.class.isInstance(c)) {
				((JTextField)c).selectAll();
			}
		}
		return c;
	}

	public boolean getOverwriteOnCellEdit() {
		return overwriteOnCellEdit;
	}

	public void setOverwriteOnCellEdit(boolean overwriteOnCellEdit) {
		this.overwriteOnCellEdit = overwriteOnCellEdit;
	}
	
	
}
