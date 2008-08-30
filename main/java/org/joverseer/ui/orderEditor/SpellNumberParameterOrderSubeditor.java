package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;
import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for Spell Number parameters
 * 
 * @author Marios Skounakis
 */

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
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        tlb.cell(parameter = new JComboBox(), "colspec=left:180px");
        parameter.setPreferredSize(new Dimension(180, 18));
        parameter.setDropTarget(new DropTarget(parameter, new DropTargetAdapter() {
			public void drop(DropTargetDropEvent dtde) {
                try {
                	Transferable t = dtde.getTransferable();
                	CharacterDataFlavor characterDataFlavor = new CharacterDataFlavor();
                	ArtifactInfoDataFlavor artifactInfoDataFlavor = new ArtifactInfoDataFlavor();
                	String txt = "";
                	if (t.isDataFlavorSupported(characterDataFlavor)) {
                		txt = ((Character)t.getTransferData(characterDataFlavor)).getId();
                		txt = Character.getSpacePaddedIdFromId(txt);
                	} else if (t.isDataFlavorSupported(artifactInfoDataFlavor)) {
                		txt = String.valueOf(((ArtifactInfo)t.getTransferData(artifactInfoDataFlavor)).getNo());
                	} else {
                		txt = (t.getTransferData(DataFlavor.stringFlavor)).toString();
                	}
                	JComboBox cmb = (JComboBox)parameter;
                	for (int i=0; i<cmb.getItemCount(); i++) {
                		if (cmb.getItemAt(i).toString().startsWith(txt + " ")) {
                			cmb.setSelectedIndex(i);
                		}
                	};
                }
                catch (Exception exc) {
                    
                }
			}
        }));
        
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
                    valueChanged();
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

