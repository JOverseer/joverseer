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
import org.joverseer.ui.support.Messages;
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

    public SpellNumberParameterOrderSubeditor(OrderEditor oe,String paramName, Order o, int orderNo) {
        super(oe,o);
        this.paramName = paramName;
        this.orderNo = orderNo;
    }

    protected void loadSpellCombo(JComboBox com) {
    	GameMetadata gm = GameHolder.instance().getGame().getMetadata();
    	Character c = getOrder().getCharacter();
        com.addItem(""); //$NON-NLS-1$
    	for (SpellInfo si : (ArrayList<SpellInfo>)gm.getSpells().getItems()) {
            if (si.getOrderNumber() == this.orderNo) {
	        	 boolean found = false;
	             for (SpellProficiency sp : c.getSpells()) {
	                 if (sp.getSpellId() == si.getNumber()) {
	                     found = true;
	                 }
	             }
	             com.addItem(si.getNumber() + " - " + si.getName() + (found ? Messages.getString("SpellNumberParameterOrderSubeditor.2") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px"); //$NON-NLS-1$
        tlb.cell(this.parameter = (JComboBox) getPrimaryComponent(""), "colspec=left:180px"); //$NON-NLS-1$
        tlb.row();
        tlb.cell(this.spellNo = new JTextField());
        this.spellNo.setVisible(false);
        tlb.row();

        // find and preload current spell (from order)
        if (o.getParameter(paramNo) != null) {
            String spellId = o.getParameter(paramNo);
            for (int i=0; i<this.parameter.getItemCount(); i++) {
                if (this.parameter.getItemAt(i).toString().startsWith(spellId + " ")) { //$NON-NLS-1$
                    this.parameter.setSelectedIndex(i);
                    this.spellNo.setText(o.getParameter(paramNo));
                    valueChanged();
                }
            }
        }
        components.add(this.spellNo);
        this.parameter.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                if (SpellNumberParameterOrderSubeditor.this.parameter.getSelectedItem() != null) {
                    String spId = SpellNumberParameterOrderSubeditor.this.parameter.getSelectedItem().toString();
                    SpellNumberParameterOrderSubeditor.this.spellNo.setText(spId.substring(0, spId.indexOf(" "))); //$NON-NLS-1$
                } else {
                    SpellNumberParameterOrderSubeditor.this.spellNo.setText(""); //$NON-NLS-1$
                }
                updateEditor();
            }
        });
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
		final JComboBox com = new JComboBox();
        loadSpellCombo(com);
		com.setPreferredSize(new Dimension(180, 18));
		com.setDropTarget(new DropTarget(com, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
                try {
                	Transferable t = dtde.getTransferable();
                	CharacterDataFlavor characterDataFlavor = new CharacterDataFlavor();
                	ArtifactInfoDataFlavor artifactInfoDataFlavor = new ArtifactInfoDataFlavor();
                	String txt = ""; //$NON-NLS-1$
                	if (t.isDataFlavorSupported(characterDataFlavor)) {
                		txt = ((Character)t.getTransferData(characterDataFlavor)).getId();
                		txt = Character.getSpacePaddedIdFromId(txt);
                	} else if (t.isDataFlavorSupported(artifactInfoDataFlavor)) {
                		txt = String.valueOf(((ArtifactInfo)t.getTransferData(artifactInfoDataFlavor)).getNo());
                	} else {
                		txt = (t.getTransferData(DataFlavor.stringFlavor)).toString();
                	}
                	for (int i=0; i<com.getItemCount(); i++) {
                		if (com.getItemAt(i).toString().startsWith(txt + " ")) { //$NON-NLS-1$
                			com.setSelectedIndex(i);
                		}
                	};
                }
                catch (Exception exc) {

                }
			}
        }));
		return com;
	}
}

