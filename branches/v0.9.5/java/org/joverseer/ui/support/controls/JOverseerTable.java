package org.joverseer.ui.support.controls;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

public class JOverseerTable extends javax.swing.JTable {
	boolean overwriteOnCellEdit = true;
	
	public JOverseerTable() {
		super();
                init();
	}

	public JOverseerTable(TableModel dm) {
		super(dm);
                init();
	}
        
        private void init() {
            setSurrendersFocusOnKeystroke(true);
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e)
                {
                    onKeyPressed(e);
                } 
            });
            
        }
        
        private void onKeyPressed( final KeyEvent e )
        {
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    CellEditor ed = getCellEditor();
                }
            });
        }

        public boolean editCellAt(int row, int col, EventObject event)
        {
            boolean editing = super.editCellAt(row, col, event);
            if (editing)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Component comp = getEditorComponent();
                        if (comp != null) comp.requestFocus();
                    }
                });
            }
            return editing;
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
