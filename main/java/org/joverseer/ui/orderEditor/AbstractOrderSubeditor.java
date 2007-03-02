package org.joverseer.ui.orderEditor;


import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.joverseer.domain.Order;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;

public abstract class AbstractOrderSubeditor extends AbstractForm {

    OrderEditor editor;

    public abstract void updateEditor();

    public AbstractOrderSubeditor(Order o) {
        super(FormModelHelper.createFormModel(o));
    }

    public OrderEditor getEditor() {
        return editor;
    }

    public void setEditor(OrderEditor editor) {
        this.editor = editor;
    }

    public void attachAutoUpdateDocumentListener(JTextComponent c) {
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

}