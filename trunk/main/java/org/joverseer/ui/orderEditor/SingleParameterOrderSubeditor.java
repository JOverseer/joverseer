package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;
import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for generic single parameter
 * 
 * @author Marios Skounakis
 */
public class SingleParameterOrderSubeditor extends AbstractOrderSubeditor {
    JTextField parameter;
    String paramName;
    int width = 60;

    public SingleParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }
    
    public SingleParameterOrderSubeditor(String paramName, Order o, int width) {
        super(o);
        this.paramName = paramName;
        this.width = width;
    }
    
    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.parameter = new JTextField(o.getParameter(paramNo)), "colspec=left:220px");
        this.parameter.setPreferredSize(new Dimension(this.width, PREFERRED_HEIGHT));
        attachAutoUpdateDocumentListener(this.parameter);
        components.add(this.parameter);
        //GraphicUtils.addOverwriteDropListener(parameter);
        this.parameter.setDropTarget(new DropTarget(this.parameter, new DropTargetAdapter() {
			@Override
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
	                	SingleParameterOrderSubeditor.this.parameter.setText(txt);
	                    SingleParameterOrderSubeditor.this.parameter.requestFocus();
	                }
	                catch (Exception exc) {
	                    
	                }
			}
        }));
        tlb.row();
    }

        
}
