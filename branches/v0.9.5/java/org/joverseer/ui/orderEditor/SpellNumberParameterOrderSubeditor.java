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
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class SpellNumberParameterOrderSubeditor extends AbstractOrderSubeditor {
    String paramName;
    JComboBox parameter;
    JTextField spellNo;
    int orderNo;
    
    public SpellNumberParameterOrderSubeditor(String paramName, Order o, int orderNo) {
        super(o);
        this.paramName = paramName;
        this.orderNo = orderNo;
    }
    
    protected void loadSpellCombo() {
    	GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        parameter.addItem("");
    	for (SpellInfo si : (ArrayList<SpellInfo>)gm.getSpells().getItems()) {
            if (si.getOrderNumber() == orderNo) {
                parameter.addItem(si.getNumber() + " - " + si.getName());
            }
        }
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(parameter = new JComboBox(), "colspec=left:180px");
        parameter.setPreferredSize(new Dimension(180, 18));
        tlb.row();
        tlb.cell(spellNo = new JTextField());
        spellNo.setVisible(false);
        tlb.row();
        
        loadSpellCombo();
        
        // find and preload current spell (from order)
        if (o.getParameter(paramNo) != null) {
            String spellId = o.getParameter(paramNo);
            for (int i=0; i<parameter.getItemCount(); i++) {
                if (parameter.getItemAt(i).toString().startsWith(spellId + " ")) {
                    parameter.setSelectedIndex(i);
                    spellNo.setText(o.getParameter(paramNo));
                }
            }
        }
        components.add(spellNo);
        parameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (parameter.getSelectedItem() != null) {
                    String spId = parameter.getSelectedItem().toString();
                    spellNo.setText(spId.substring(0, spId.indexOf(" ")));
                } else {
                    spellNo.setText("");
                }
                updateEditor();
            }
        });
    }
}

