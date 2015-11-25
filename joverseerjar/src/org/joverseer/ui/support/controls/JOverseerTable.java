package org.joverseer.ui.support.controls;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 * Extends JTable adding functionality for overwritting cell contents upon cell edit
 * (contents start selected so a single key stroke overwrites them)
 * 
 * @author Marios Skounakis
 */
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
                @Override
				public void keyPressed(KeyEvent e)
                {
                    onKeyPressed(e);
                } 
            });
            
        }
        
        private void onKeyPressed( final KeyEvent e )
        {
        }

        @Override
		public boolean editCellAt(int row, int col, EventObject event)
        {
            boolean editing = super.editCellAt(row, col, event);
            if (editing)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
					public void run()
                    {
                        Component comp = getEditorComponent();
                        if (comp != null) comp.requestFocus();
                    }
                });
            }
            return editing;
        }

	@Override
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
		return this.overwriteOnCellEdit;
	}

	public void setOverwriteOnCellEdit(boolean overwriteOnCellEdit) {
		this.overwriteOnCellEdit = overwriteOnCellEdit;
	}
	
	
}
