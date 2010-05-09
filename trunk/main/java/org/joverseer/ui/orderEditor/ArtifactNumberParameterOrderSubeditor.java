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
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;
import org.joverseer.ui.support.dataFlavors.SpellInfoDataFlavor;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class ArtifactNumberParameterOrderSubeditor extends AbstractOrderSubeditor {
	JFormattedTextField parameter;
	JTextField artifactName;
    String paramName;

    public ArtifactNumberParameterOrderSubeditor(String paramName, Order o) {
        super(o);
        this.paramName = paramName;
    }
    
    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        tlb.cell(new JLabel(paramName), "colspec=left:70px");
        try {
            DecimalFormat f = new DecimalFormat();
            f.setDecimalSeparatorAlwaysShown(false);
            f.setGroupingUsed(false);
            tlb.cell(parameter = new JFormattedTextField(f), "colspec=left:35px");
            parameter.setText(o.getParameter(paramNo));
            parameter.setPreferredSize(new Dimension(30, 18));
            parameter.setDropTarget(new DropTarget(parameter, new DropTargetAdapter() {
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
    	                	parameter.setText(txt);
    	                    parameter.requestFocus();
    	                    updateArtifactNumber();
    	                }
    	                catch (Exception exc) {
    	                    
    	                }
    			}
            }));
            attachAutoUpdateDocumentListener(parameter);
            components.add(parameter);
            
            artifactName = new JTextField();
            artifactName.setEditable(false);
            artifactName.setPreferredSize(new Dimension(140, 18));
            artifactName.setFocusable(false);
            tlb.cell(artifactName, "colspec=left:140px");
            updateArtifactNumber();
            
        }
        catch (Exception exc) {
            
        }
        tlb.row();
    }

	@Override
	public void updateEditor() {
		super.updateEditor();
		updateArtifactNumber();
	}
    
    protected void updateArtifactNumber() {
    	String artiNoStr = parameter.getText();
    	String artiName = "";
    	try {
    		int artiNo = Integer.parseInt(artiNoStr);
    		Game game = GameHolder.instance().getGame();
    		ArtifactInfo ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
    		if (ai != null) {
    			artiName = ai.getName();
    		}
    	}
    	catch (Exception exc) {
    		
    	}
    	artifactName.setText(artiName);
    	if (!artiName.equals("")) {
    		artifactName.setSelectionStart(0);
    		artifactName.setSelectionEnd(0);
    	}
    }
}
