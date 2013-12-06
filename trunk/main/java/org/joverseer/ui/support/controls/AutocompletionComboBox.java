package org.joverseer.ui.support.controls;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jidesoft.swing.AutoCompletionComboBox;

/**
 * Auto-complete combo box
 * 
 * Adds the ActionPerformedPolicy to the standard Jidesof AutoCompletionComboBox which controls
 * when ActionPerformed events are thrown
 * 
 * @author Marios Skounakis
 */
public class AutocompletionComboBox extends AutoCompletionComboBox {

    public static final int ACTION_PERFORMED_ONLY_ON_ENTER = 1;
    public static final int ACTION_PERFORMED_ON_SELECTION_CHANGE = 2;

    int actionPerformedPolicy = ACTION_PERFORMED_ONLY_ON_ENTER;

    @Override
	protected void fireActionEvent() {
        if (this.actionPerformedPolicy == ACTION_PERFORMED_ON_SELECTION_CHANGE) {
            super.fireActionEvent();
        }
    }

    protected void fireActionEvent(boolean doFire) {
        if (this.actionPerformedPolicy == ACTION_PERFORMED_ONLY_ON_ENTER) {
            super.fireActionEvent();
        }
    }

    private void init() {

        getEditor().getEditorComponent().addKeyListener(new KeyListener() {

            @Override
			public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Autocomp combo received enter.");
                    fireActionEvent(true);
                }
            }

            @Override
			public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
			public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

        });

        getEditor().getEditorComponent().addFocusListener(new FocusListener() {

            @Override
			public void focusGained(FocusEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
			public void focusLost(FocusEvent e) {
                fireActionEvent(true);
                if (e.getOppositeComponent() != null) {
                    e.getOppositeComponent().requestFocus();
                }
            }

        });

        addPopupMenuListener(new PopupMenuListener() {

            @Override
			public void popupMenuCanceled(PopupMenuEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                fireActionEvent(true);
            }

            @Override
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
        return this.actionPerformedPolicy;
    }

    public void setActionPerformedPolicy(int actionPerformedPolicy) {
        this.actionPerformedPolicy = actionPerformedPolicy;
    }


}
