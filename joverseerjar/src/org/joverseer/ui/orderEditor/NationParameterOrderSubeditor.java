package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.controls.NationComboBox;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for order params that take a nation number as a value
 * Provides a combo box that translates between nation names and nation numbers
 *
 * @author Marios Skounakis
 */
public class NationParameterOrderSubeditor extends AbstractOrderSubeditor {
	NationComboBox parameter;
    JTextField nationNo;
    String paramName;

    public NationParameterOrderSubeditor(OrderEditor oe,String paramName, Order o,GameHolder gameHolder) {
        super(oe,o,gameHolder);
        this.paramName = paramName;
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {
        String nno = o.getParameter(paramNo);
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.parameter = (NationComboBox)getPrimaryComponent(nno), "colspec=left:150px");
        tlb.row();
        tlb.cell(this.nationNo = new JTextField());
        this.nationNo.setVisible(false);
        tlb.row();

        int init = -1;
        if (nno != null && !nno.equals("")) {
            this.nationNo.setText(nno);
            init = Integer.valueOf(nno);
        }
        this.parameter.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
            	Nation n = NationParameterOrderSubeditor.this.parameter.getSelectedNation();
            	if (n == null) {
            		NationParameterOrderSubeditor.this.nationNo.setText("");
            	} else {
            		NationParameterOrderSubeditor.this.nationNo.setText(String.valueOf(n.getNumber()));
            	}
                updateEditor();
            }
        });
        this.parameter.load(false, false,init);
        components.add(this.nationNo);
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
		NationComboBox com = new NationComboBox(this.gameHolder);
		com.setPreferredSize(new Dimension(120, 18));
		return com;
	}
}
