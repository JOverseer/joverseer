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

import com.jidesoft.swing.AutoCompletionComboBox;


public class DropDownParameterOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox combo;
    JTextField parameter;
    String paramName;
    String[] values;
    String[] descriptions;
    
    public DropDownParameterOrderSubeditor(String paramName, Order o, String[] values, String descriptions[]) {
        super(o);
        this.paramName = paramName;
        this.values = values;
        this.descriptions = descriptions;
    }
    
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(combo = new AutoCompletionComboBox(), "colspec=left:150px");
        combo.setPreferredSize(new Dimension(100, 18));
        combo.addItem("");
        for (String v : descriptions) {
            combo.addItem(v);
        }
        
        tlb.row();
        tlb.cell(parameter = new JTextField());
        parameter.setVisible(false);
        String val = o.getParameter(paramNo);
        parameter.setText(val);
        for (int i=0; i<descriptions.length; i++) {
            if (values[i].equals(val)) {
                combo.setSelectedItem(descriptions[i]);
                break;
            }
        }
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String v = combo.getSelectedItem().toString();
                if (v == null || v.equals("")) {
                    parameter.setText("");
                } else {
                    for (int i=0; i<descriptions.length; i++) {
                        if (descriptions[i].equals(v)) {
                            parameter.setText(values[i]);
                        }
                    }
                }
                updateEditor();
            }
        });
        components.add(parameter);
        tlb.row();
    }

}
