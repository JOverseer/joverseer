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
import javax.swing.JPanel;
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

    public ArtifactNumberParameterOrderSubeditor(OrderEditor oe,String paramName, Order o,GameHolder gameHolder) {
        super(oe,o,gameHolder);
        this.paramName = paramName;
    }

    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        try {
            JPanel pnl = new JPanel();
            //pnl.setBorder(new EmptyBorder(0, 0, 0, 0));
            TableLayoutBuilder tlb2 = new TableLayoutBuilder(pnl);
            tlb2.cell(this.parameter = (JFormattedTextField)getPrimaryComponent(o.getParameter(paramNo)), "colspec=left:35px");
            attachAutoUpdateDocumentListener(this.parameter);
            components.add(this.parameter);

            this.artifactName = new JTextField();
            this.artifactName.setEditable(false);
            this.artifactName.setPreferredSize(new Dimension(145, 18));
            this.artifactName.setFocusable(false);
            tlb2.cell(this.artifactName, "colspec=left:160px rowspec=20px");
            updateArtifactNumber();
            tlb.cell(tlb2.getPanel(), "colspec=left:225px");
            tlb.gapCol();
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
    	String artiNoStr = this.parameter.getText();
    	String artiName = "";
    	try {
    		int artiNo = Integer.parseInt(artiNoStr);
    		Game game = this.gameHolder.getGame();
    		ArtifactInfo ai = (ArtifactInfo)game.getMetadata().findFirstArtifactByNumber(artiNo);
    		if (ai != null) {
    			artiName = ai.getName();
    		}
    	}
    	catch (Exception exc) {

    	}
    	this.artifactName.setText(artiName);
    	if (!artiName.equals("")) {
    		this.artifactName.setSelectionStart(0);
    		this.artifactName.setSelectionEnd(0);
    	}
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
        DecimalFormat f = new DecimalFormat();
        f.setDecimalSeparatorAlwaysShown(false);
        f.setGroupingUsed(false);
		final JFormattedTextField com = new JFormattedTextField(f);
        com.setText(initValue);
        com.setPreferredSize(new Dimension(30, 18));
        com.setDropTarget(new DropTarget(this.parameter, new DropTargetAdapter() {
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
	                	com.setText(txt);
	                	com.requestFocus();
	                    updateArtifactNumber();
	                }
	                catch (Exception exc) {

	                }
			}
        }));
		return com;
	}
}
