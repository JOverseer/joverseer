package org.joverseer.ui.support.controls;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jidesoft.swing.AutoCompletionComboBox;

public class AutocompletionComboBox extends AutoCompletionComboBox {
	public static final int ACTION_PERFORMED_ONLY_ON_ENTER = 1;
	public static final int ACTION_PERFORMED_ON_SELECTION_CHANGE = 2;
	
	int actionPerformedPolicy = ACTION_PERFORMED_ONLY_ON_ENTER;
	
	protected void fireActionEvent() {
		if (actionPerformedPolicy == ACTION_PERFORMED_ON_SELECTION_CHANGE) {
			super.fireActionEvent();
		}
	}
	
	protected void fireActionEvent(boolean doFire) {
		if (actionPerformedPolicy == ACTION_PERFORMED_ONLY_ON_ENTER) {
			super.fireActionEvent();
		}
	}

	private void init() {
		
		getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Autocomp combo received enter.");
					fireActionEvent(true);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		getEditor().getEditorComponent().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void focusLost(FocusEvent e) {
				fireActionEvent(true);
                                if (e.getOppositeComponent() != null) {
                                    e.getOppositeComponent().requestFocus();
                                }
			}
			
		});
		
		addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				fireActionEvent(true);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public AutocompletionComboBox() {
		super();
		init();
	}

	public AutocompletionComboBox(ComboBoxModel arg0) {
		super(arg0);
		init();
	}

	public AutocompletionComboBox(Object[] arg0) {
		super(arg0);
		init();
	}

	public AutocompletionComboBox(Vector arg0) {
		super(arg0);
		init();
	}

	public int getActionPerformedPolicy() {
		return actionPerformedPolicy;
	}

	public void setActionPerformedPolicy(int actionPerformedPolicy) {
		this.actionPerformedPolicy = actionPerformedPolicy;
	}

	

}
