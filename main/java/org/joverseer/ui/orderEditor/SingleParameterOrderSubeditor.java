package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class SingleParameterOrderSubeditor extends AbstractOrderSubeditor {
    JTextField parameter;
    String paramName;

    public SingleParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }
    
    public void setFormObject(Object obj) {
        super.setFormObject(obj);
        Order o = (Order)obj;
        parameter.setText(o.getParameter(0));
    }
    
    public void updateEditor() {
        getEditor().setParameters(parameter.getText());
    }

    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel(paramName));
        tlb.cell(parameter = new JTextField());
        parameter.setPreferredSize(new Dimension(60, 20));
        attachAutoUpdateDocumentListener(parameter);
        GraphicUtils.addOverwriteDropListener(parameter);
        JPanel p = tlb.getPanel();
        p.setBackground(Color.white);
        return p;
    }
}
