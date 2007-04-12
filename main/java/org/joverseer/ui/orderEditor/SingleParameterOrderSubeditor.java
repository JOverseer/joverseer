package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

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
    
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(parameter = new JTextField(o.getParameter(paramNo)), "colspec=left:150px");
        parameter.setPreferredSize(new Dimension(60, 18));
        attachAutoUpdateDocumentListener(parameter);
        components.add(parameter);
        GraphicUtils.addOverwriteDropListener(parameter);
        tlb.row();
    }

        
}
