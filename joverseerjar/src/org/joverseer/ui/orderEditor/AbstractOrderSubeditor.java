package org.joverseer.ui.orderEditor;


import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.domain.Order;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Base class for order subeditors.
 *
 * The subeditor is an editor for one or more parameters of the order
 *
 * @author Marios Skounakis
 */
public abstract class AbstractOrderSubeditor {
	protected static final int PREFERRED_HEIGHT = 23; // was 18 but cuts off descenders
    Order order;
    OrderEditor editor;
    protected final Log logger = LogFactory.getLog(this.getClass());
    //dependencies
    protected final GameHolder gameHolder;

    public void updateEditor() {
        getEditor().updateParameters();
    }

    public AbstractOrderSubeditor(OrderEditor oe,Order o,GameHolder gameHolder) {
    	this.editor = oe;
    	this.order = o;
    	this.gameHolder = gameHolder;
    }

    public OrderEditor getEditor() {
        return this.editor;
    }

    public void setEditor(OrderEditor editor) {
        this.editor = editor;
    }

    public void attachAutoUpdateDocumentListener(JTextComponent c) {
        // call updateEditor whenever the text component is changed
        c.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void changedUpdate(DocumentEvent arg0) {
            	// Nothing to do but must override pure virtual
            }

            @Override
			public void insertUpdate(DocumentEvent arg0) {
                updateEditor();
            }

            @Override
			public void removeUpdate(DocumentEvent arg0) {
                updateEditor();
            }
        });
    }

    public abstract void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue);

    public abstract JComponent getPrimaryComponent(String initValue);

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
        }


	   public void valueChanged() {
	   // empty hook by default
   }

}
