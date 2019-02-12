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
import org.joverseer.support.GameHolder;
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
    String possibleInitValue;

    public NumberParameterOrderSubeditor(OrderEditor oe,String paramName, Order o,String initValue,GameHolder gameHolder) {
        super(oe,o,gameHolder);
        this.paramName = paramName;
        this.possibleInitValue = initValue;
    }
    public NumberParameterOrderSubeditor(OrderEditor oe,String paramName, Order o,GameHolder gameHolder) {
    	this(oe,paramName,o,null,gameHolder);
    }

    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {
    	if ((this.possibleInitValue != null) && (applyInitValue)) {
    		o.setParameter(paramNo, this.possibleInitValue);
    	}
        tlb.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb.cell(this.parameter = (JFormattedTextField)getPrimaryComponent(o.getParameter(paramNo)), "colspec=left:220px");
        attachAutoUpdateDocumentListener(this.parameter);
        components.add(this.parameter);
        tlb.row();
    }

	@Override
	public JComponent getPrimaryComponent(final String initValue) {
        DecimalFormat f = new DecimalFormat();
        f.setDecimalSeparatorAlwaysShown(false);
        f.setGroupingUsed(false);
		final JFormattedTextField com = new JFormattedTextField(f);
        com.setText(initValue);
        com.setPreferredSize(new Dimension(50, 18));
        com.setDropTarget(new DropTarget(com, new DropTargetAdapter() {
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
	                }
	                catch (Exception exc) {
	                	NumberParameterOrderSubeditor.this.logger.error("drop target exception for order "+initValue);
	                }
			}
        }));
		return com;
	}

}
