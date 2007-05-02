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
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(parameter = new JComboBox(), "colspec=left:150px");
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        parameter.addItem("");
        parameter.setPreferredSize(new Dimension(60, 18));
        tlb.row();
        tlb.cell(nationNo = new JTextField());
        nationNo.setVisible(false);
        tlb.row();
        
        for (Nation n : (ArrayList<Nation>)gm.getNations()) {
            if (n.getNumber() > 0) {
                parameter.addItem(n.getName());
            }
        }

        String nno = o.getParameter(paramNo);
        if (nno != null && !nno.equals("")) {
            parameter.setSelectedIndex(Integer.parseInt(nno));
            nationNo.setText(nno);
        }
        parameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                nationNo.setText(parameter.getSelectedItem() == null || parameter.getSelectedIndex() == 0 ? "" : String.valueOf(parameter.getSelectedIndex()));
                updateEditor();
            }
        }); 
        
        components.add(nationNo);
    }
}
