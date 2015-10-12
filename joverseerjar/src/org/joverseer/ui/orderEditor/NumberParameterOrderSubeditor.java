package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.joverseer.domain.Order;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;
import org.joverseer.ui.support.dataFlavors.SpellInfoDataFlavor;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for parameters that take a number as parameter
 * 
 * @author Marios Skounakis
 */
//TODO could add some validation here for number ranges
public class NumberParameterOrderSubeditor extends AbstractOrderSubeditor {
    JFormattedTextField parameter;
    String paramName;

    public NumberParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }
    
    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        try {
            DecimalFormat f = new DecimalFormat();
            f.setDecimalSeparatorAlwaysShown(false);
            f.setGroupingUsed(false);
            tlb.cell(this.parameter = new JFormattedTextField(f), "colspec=left:220px");
            this.parameter.setText(o.getParameter(paramNo));
            this.parameter.setPreferredSize(new Dimension(50, 18));
            this.parameter.setDropTarget(new DropTarget(this.parameter, new DropTargetAdapter() {
    			@Override
				public void drop(DropTargetDropEvent dtde) {
    	                try {
    	                	Transferable t = dtde.getTransferable();
    	                	SpellInfoDataFlavor spellInfoDataFlavor = new SpellInfoDataFlavor();
    	                	ArtifactInfoDataFlavor artifactInfoDataFlavor = new ArtifactInfoDataFlavor();
    	                	String txt = "";
    	                	if (t.isDataFlavorSupported(spellInfoDataFlavor)) {
    	                		txt = String.valueOf(((SpellInfo)t.getTransferData(spellInfoDataFlavor)).getNumber());
    	                	} else if (t.isDataFlavorSupported(artifactInfoDataFlavor)) {
    	                		txt = String.valueOf(((ArtifactInfo)t.getTransferData(artifactInfoDataFlavor)).getNo());
    	                	} else {
    	                		txt = (t.getTransferData(DataFlavor.stringFlavor)).toString();
    	                	}
    	                	NumberParameterOrderSubeditor.this.parameter.setText(txt);
    	                    NumberParameterOrderSubeditor.this.parameter.requestFocus();
    	                }
    	                catch (Exception exc) {
    	                    
    	                }
    			}
            }));
            attachAutoUpdateDocumentListener(this.parameter);
            components.add(this.parameter);
        }
        catch (Exception exc) {
            
        }
        tlb.row();
    }

}
