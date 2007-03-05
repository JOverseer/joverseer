package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class DropDownParameterOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox parameter;
    String paramName;
    String[] values;
    
    public DropDownParameterOrderSubeditor(String paramName, Order o, String[] values) {
        super(o);
        this.paramName = paramName;
        this.values = values;
    }
    
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName));
        tlb.cell(parameter = new JComboBox());
        parameter.setPreferredSize(new Dimension(50, 18));
        components.add(parameter);
        parameter.addItem("");
        for (String v : values) {
            parameter.addItem(v);
        }
        parameter.setSelectedItem(o.getParameter(paramNo));
        parameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                updateEditor();
            }
        });
        tlb.row();
    }

}
