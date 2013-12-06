package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.joverseer.domain.Artifact;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.transferHandlers.ArtifactExportTransferHandler;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows artifacts in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class ArtifactViewer extends ObjectViewer {

    public static final String FORM_PAGE = "ArtifactViewer";

    JTextField artifactName;
    JTextField owner;
    JTextField infoSource;
    
    public ArtifactViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    @Override
	public boolean appliesTo(Object obj) {
        return Artifact.class.isInstance(obj);
    }
    
    @Override
	public void setFormObject(Object obj) {
        super.setFormObject(obj);

        Artifact a = (Artifact)obj;
        this.artifactName.setTransferHandler(new ArtifactExportTransferHandler(a));
        this.artifactName.setText("#" + String.valueOf(a.getNumber()) + " " + a.getName());
        this.artifactName.setCaretPosition(0);
        this.owner.setText(a.getOwner() != null ? a.getOwner() : "unclaimed");
        // show info sources, if needed
        InfoSource is = a.getInfoSource();
        if (DerivedFromSpellInfoSource.class.isInstance(is)) {
            DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource)is;
            String infoSourcesStr = sis.getSpell() + " at " + sis.getHexNo();
            this.infoSource.setVisible(true);
            for (InfoSource dsis : sis.getOtherInfoSources()) {
                if (DerivedFromSpellInfoSource.class.isInstance(dsis)) {
                    infoSourcesStr += ", " + ((DerivedFromSpellInfoSource)dsis).getSpell() + " at " + ((DerivedFromSpellInfoSource)dsis).getHexNo();
                }
            }
            this.infoSource.setText(infoSourcesStr);
        } else {
            this.infoSource.setVisible(false);
        }
    }
    
    @Override
	protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));
        
        glb.append(this.artifactName = new JTextField());
        this.artifactName.setPreferredSize(new Dimension(150, 16));
        this.artifactName.setFont(GraphicUtils.getFont(this.artifactName.getFont().getName(), Font.BOLD, this.artifactName.getFont().getSize()));
        this.artifactName.setBorder(null);
        this.artifactName.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
            	if (e.getClickCount() == 1) {
            		TransferHandler handler = ArtifactViewer.this.artifactName.getTransferHandler();
                	handler.exportAsDrag(ArtifactViewer.this.artifactName, e, TransferHandler.COPY);
            	}
            }

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() ==  MouseEvent.BUTTON1 &&
						arg0.getClickCount() == 2) {
					Artifact a = (Artifact)getFormObject();
                    if (a == null) return;
                    ArtifactInfo ai = (ArtifactInfo)GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", a.getNumber());
                    if (ai == null) return;
                    final String descr = "#" + ai.getNo() + " - " + ai.getName() + "\n" +
                                    ai.getAlignment() + "\n" +
                                    ai.getPower1() + ", " + ai.getPower2(); 
                    MessageDialog dlg = new MessageDialog("Artifact Info", descr);
                    dlg.showDialog();
				}
			}
            
            
        });
        
        glb.append(this.owner = new JTextField());
        this.owner.setPreferredSize(new Dimension(70, 12));
        this.owner.setBorder(null);
        
        glb.nextLine();
        
        glb.append(this.infoSource = new JTextField(), 2, 1);
        this.infoSource.setPreferredSize(new Dimension(100, 12));
        this.infoSource.setBorder(null);
        
        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }
        
}
