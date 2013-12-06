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

/**
 * Subeditor for parameters that use a drop down list to select the value
 * 
 * @author Marios Skounakis
 */
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
    
    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.combo = new AutoCompletionComboBox(), "colspec=left:205px");
        this.combo.setPreferredSize(new Dimension(100, 18));
        this.combo.addItem("");
        for (String v : this.descriptions) {
            this.combo.addItem(v);
        }
        
        tlb.row();
        tlb.cell(this.parameter = new JTextField());
        this.parameter.setVisible(false);
        String val = o.getParameter(paramNo);
        this.parameter.setText(val);
        for (int i=0; i<this.descriptions.length; i++) {
            if (this.values[i].equals(val)) {
                this.combo.setSelectedItem(this.descriptions[i]);
                break;
            }
        }
        this.combo.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                String v = DropDownParameterOrderSubeditor.this.combo.getSelectedItem().toString();
                if (v == null || v.equals("")) {
                    DropDownParameterOrderSubeditor.this.parameter.setText("");
                } else {
                    for (int i=0; i<DropDownParameterOrderSubeditor.this.descriptions.length; i++) {
                        if (DropDownParameterOrderSubeditor.this.descriptions[i].equals(v)) {
                            DropDownParameterOrderSubeditor.this.parameter.setText(DropDownParameterOrderSubeditor.this.values[i]);
                        }
                    }
                }
                updateEditor();
            }
        });
        components.add(this.parameter);
        tlb.row();
    }

}
