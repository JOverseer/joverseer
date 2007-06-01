package org.joverseer.ui.orderEditor;


import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Base class for order subeditors.
 * 
 * The subeditor is an editor for one or more parameters of the order
 * 
 * @author Marios Skounakis
 */
public abstract class AbstractOrderSubeditor {
    Order order;
    OrderEditor editor;

    public void updateEditor() {
        getEditor().updateParameters();
    }

    public AbstractOrderSubeditor(Order o) {
    	order = o;
    }

    public OrderEditor getEditor() {
        return editor;
    }

    public void setEditor(OrderEditor editor) {
        this.editor = editor;
    }
    
    public void attachAutoUpdateDocumentListener(JTextComponent c) {
        // call updateEditor whenever the text component is changed
        c.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent arg0) {
            }
    
            public void insertUpdate(DocumentEvent arg0) {
                updateEditor();
            }
    
            public void removeUpdate(DocumentEvent arg0) {
                updateEditor();
            }
        });
    }
    
    public abstract void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo);

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
        }


   public void valueChanged() {
       
   }
        
}
