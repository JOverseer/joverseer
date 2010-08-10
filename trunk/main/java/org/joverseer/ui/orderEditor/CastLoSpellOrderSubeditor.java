package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for the 940 order
 * 
 * @author Marios Skounakis
 */
public class CastLoSpellOrderSubeditor extends AbstractOrderSubeditor {
    String paramName;
    JComboBox parameter;
    JTextField spellNo;
    JLabel paramLabel;
    int orderNo;
    TableLayoutBuilder tlb;
    ArrayList<JComponent> components;
    JPanel secondParamPanel;
    JComponent currentComp;
    JPanel currentCompPanel;
    String currentType = null;
    
    public CastLoSpellOrderSubeditor(Order o) {
        super(o);
        this.paramName = "Spell";
        this.orderNo = 940;
    }
    
    protected void loadSpellCombo() {
    	GameMetadata gm = GameHolder.instance().getGame().getMetadata();
    	Character c = getOrder().getCharacter();
        parameter.addItem("");
    	for (SpellInfo si : (ArrayList<SpellInfo>)gm.getSpells().getItems()) {
            if (si.getOrderNumber() == orderNo) {
	        	 boolean found = false;
	             for (SpellProficiency sp : c.getSpells()) {
	                 if (sp.getSpellId() == si.getNumber()) {
	                     found = true;
	                 }
	             }
	             parameter.addItem(si.getNumber() + " - " + si.getName() + (found ? " (known)" : ""));
            }
        }
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        this.tlb = tlb;
        this.components = components;
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(parameter = new JComboBox(), "colspec=left:230px");
        parameter.setPreferredSize(new Dimension(180, 18));
        tlb.row();
        tlb.cell(spellNo = new JTextField());
        spellNo.setVisible(false);
        tlb.row();
        
        loadSpellCombo();
        
        components.add(spellNo);
        
        tlb.row();
        
        secondParamPanel = new JPanel();
        secondParamPanel.setBackground(Color.white);
        tlb.cell(secondParamPanel, "colspan=2");
        tlb.row();
        
        // find and preload current spell (from order)
        if (o.getParameter(paramNo) != null) {
            String spellId = o.getParameter(paramNo);
            for (int i=0; i<parameter.getItemCount(); i++) {
                if (parameter.getItemAt(i).toString().startsWith(spellId + " ")) {
                    parameter.setSelectedIndex(i);
                    spellNo.setText(o.getParameter(paramNo));
                    refreshSecondParameter();
                }
            }
        }
        parameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (parameter.getSelectedItem() != null) {
                    String spId = parameter.getSelectedItem().toString();
                    spellNo.setText(spId.substring(0, spId.indexOf(" ")));
                } else {
                    spellNo.setText("");
                }
                refreshSecondParameter();
                updateEditor();
            }
        });
    }
    
    /**
     * Refreshes the component that edits the second parameter of the 940 order
     * according to the selected spell
     */
    private void refreshSecondParameter() {
        try {
            int spellId = Integer.parseInt(spellNo.getText());
            String paramType = "";
            if (",406,408,417,420,422,424,426,430,436,".indexOf("," + spellId + ",") > -1) {
                paramType = "cid";
            } else if (",416,".indexOf("," + spellId + ",") > -1) {
                paramType = "pro";
            } else if (",404,419,432,".indexOf("," + spellId + ",") > -1) {
                paramType = "nat";
            } else if (",402,410,".indexOf("," + spellId + ",") > -1) {
                paramType = "alg";
            } else if (",412,418,428,".indexOf("," + spellId + ",") > -1) {
                paramType = "b";
            } else if (",413,414,415,434,".indexOf("," + spellId + ",") > -1) {
                paramType = "hex";
            }
            // a lot of hacking is going on here to adjust the components
            // and update the OrderEditor appropriately
            if (currentComp != null) {
                secondParamPanel.remove(currentCompPanel);
                components.remove(currentComp);
            };
            AbstractOrderSubeditor sub = null;
            if (currentType != null && !currentType.equals(paramType)) {
                getOrder().setParameter(1, "");
                currentType = paramType;
            } else {
                currentType = paramType;
            }
            if (paramType.equals("cid")) {
                sub = new SingleParameterOrderSubeditor("Char", getOrder());
            } else if (paramType.equals("b")) {
                sub = new SingleParameterOrderSubeditor("Arti No", getOrder());
            } else if (paramType.equals("hex")) {
                sub = new SingleParameterOrderSubeditor("Hex", getOrder());
            } else if (paramType.equals("nat")) {
                sub = new NationParameterOrderSubeditor("Nation", getOrder());
            } else if (paramType.equals("pro")) {
                sub = new DropDownParameterOrderSubeditor("Product", getOrder(), new String[]{"le", "br", "st", "mi", "fo", "ti", "mo", "go"}, new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold"});
            } else if (paramType.equals("alg")) {
                sub = new DropDownParameterOrderSubeditor("Alleg", getOrder(), new String[]{"g", "e", "n"}, new String[]{"Good", "Evil", "Neutral"});
            }
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            sub.addComponents(tlb, components, getOrder(), 1);
            tlb.row();
            tlb.cell(new JLabel(" "));
            tlb.row();
            sub.setEditor(getEditor());
            currentComp = components.get(1);
            currentCompPanel = tlb.getPanel();
            currentCompPanel.setBackground(Color.white);
            secondParamPanel.add(currentCompPanel);
            secondParamPanel.updateUI();
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
