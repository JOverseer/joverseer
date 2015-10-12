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
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for order params that take a nation number as a value
 * Provides a combo box that translates between nation names and nation numbers
 * 
 * @author Marios Skounakis
 */
public class NationParameterOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox parameter;
    JTextField nationNo;
    String paramName;
    
    public NationParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.parameter = new JComboBox(), "colspec=left:150px");
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        this.parameter.addItem("");
        this.parameter.setPreferredSize(new Dimension(120, 18));
        tlb.row();
        tlb.cell(this.nationNo = new JTextField());
        this.nationNo.setVisible(false);
        tlb.row();
        
        for (Nation n : (ArrayList<Nation>)gm.getNations()) {
            if (n.getNumber() > 0) {
                this.parameter.addItem(n.getName());
            }
        }

        String nno = o.getParameter(paramNo);
        if (nno != null && !nno.equals("")) {
            this.parameter.setSelectedIndex(Integer.parseInt(nno));
            this.nationNo.setText(nno);
        }
        this.parameter.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                NationParameterOrderSubeditor.this.nationNo.setText(NationParameterOrderSubeditor.this.parameter.getSelectedItem() == null || NationParameterOrderSubeditor.this.parameter.getSelectedIndex() == 0 ? "" : String.valueOf(NationParameterOrderSubeditor.this.parameter.getSelectedIndex()));
                updateEditor();
            }
        }); 
        
        components.add(this.nationNo);
    }
}
