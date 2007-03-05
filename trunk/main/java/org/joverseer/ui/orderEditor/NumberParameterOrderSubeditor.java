package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class NumberParameterOrderSubeditor extends AbstractOrderSubeditor {
    JFormattedTextField parameter;
    String paramName;

    public NumberParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }
    
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName));
        try {
            DecimalFormat f = new DecimalFormat();
            f.setDecimalSeparatorAlwaysShown(false);
            tlb.cell(parameter = new JFormattedTextField(f));
            parameter.setText(o.getParameter(paramNo));
            parameter.setPreferredSize(new Dimension(50, 18));
            
            attachAutoUpdateDocumentListener(parameter);
            components.add(parameter);
        }
        catch (Exception exc) {
            
        }
        tlb.row();
    }

}
