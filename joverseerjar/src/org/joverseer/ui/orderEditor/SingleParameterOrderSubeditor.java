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

    public SingleParameterOrderSubeditor(OrderEditor oe, String paramName, Order o) {
        super(oe,o);
        this.paramName = paramName;
    }
    
    public SingleParameterOrderSubeditor(OrderEditor oe, String paramName, Order o, int width) {
        super(oe,o);
        this.paramName = paramName;
        this.width = width;
    }
    
    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.parameter = (JTextField)getPrimaryComponent(o.getParameter(paramNo)), "colspec=left:220px");
        attachAutoUpdateDocumentListener(this.parameter);
        components.add(this.parameter);
        //GraphicUtils.addOverwriteDropListener(parameter);
        tlb.row();
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
		final JTextField lbl = new JTextField(initValue);
        lbl.setPreferredSize(new Dimension(this.width, PREFERRED_HEIGHT));
        lbl.setDropTarget(new DropTarget(lbl, new DropTargetAdapter() {
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
	                	lbl.setText(txt);
	                	lbl.requestFocus();
	                }
	                catch (Exception exc) {
	                    
	                }
			}
        }));
		
		return lbl;
	}

        
}
