package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class MoveCharSubeditor extends AbstractOrderSubeditor {
    JTextField destinationHex;
    
    public MoveCharSubeditor(Order o) {
        super(o);
    }
    
    public void setFormObject(Object o) {
        Order order = (Order)o;
        destinationHex.setText(order.getParameters());
    }
    
    public void updateEditor() {
        getEditor().setParameters(destinationHex.getText());
    }

    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Destination :"));
        tlb.cell(destinationHex = new JTextField());
        destinationHex.setPreferredSize(new Dimension(30, 20));
        attachAutoUpdateDocumentListener(destinationHex);
        GraphicUtils.addOverwriteDropListener(destinationHex);
        JPanel p = tlb.getPanel();
        p.setBackground(Color.white);
        return p;
    }
}
