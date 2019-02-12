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
import org.joverseer.support.GameHolder;
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
    int start;

    public DropDownParameterOrderSubeditor(OrderEditor oe,String paramName, Order o, String[] values, String descriptions[],int startAt,GameHolder gameHolder) {
    	this(oe,paramName,o,values,descriptions,gameHolder);
    	this.start = startAt;
    }
    public DropDownParameterOrderSubeditor(OrderEditor oe,String paramName, Order o, String[] values, String descriptions[],GameHolder gameHolder) {
        super(oe,o,gameHolder);
        this.paramName = paramName;
        this.values = values;
        this.descriptions = descriptions;
        this.start = -1;
    }

    @Override
	public JComponent getPrimaryComponent(String val) {
        if ((this.start != -1) && (val == null)) {
        	val = this.values[this.start];
        }
    	JComboBox box = new AutoCompletionComboBox();
        box.addItem("");
        box.setPreferredSize(new Dimension(100, 18));
        for (String v : this.descriptions) {
            box.addItem(v);
        }
        for (int i=0; i<this.descriptions.length; i++) {
            if (this.values[i].equals(val)) {
                box.setSelectedItem(this.descriptions[i]);
                break;
            }
        }
        box.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {

                DropDownParameterOrderSubeditor.this.valueChanged();
                updateEditor();
            }
        });
        return box;
    }
    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {
        String val = o.getParameter(paramNo);
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.combo = (AutoCompletionComboBox)getPrimaryComponent(val), "colspec=left:205px");
        tlb.row();
        tlb.cell(this.parameter = new JTextField());
        this.parameter.setVisible(false);
        this.parameter.setText(val);
        components.add(this.parameter);
        tlb.row();
    }
    @Override
    public void valueChanged() {
        String v = this.combo.getSelectedItem().toString();
        if (v == null || v.equals("")) {
            this.parameter.setText("");
        } else {
            for (int i=0; i<this.descriptions.length; i++) {
                if (this.descriptions[i].equals(v)) {
                    this.parameter.setText(DropDownParameterOrderSubeditor.this.values[i]);
                    break;
                }
            }
        }
    }

}
