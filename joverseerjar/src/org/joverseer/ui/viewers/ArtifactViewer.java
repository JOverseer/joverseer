package org.joverseer.ui.viewers;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import org.joverseer.domain.Artifact;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
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

    public static final String FORM_PAGE = "ArtifactViewer"; //$NON-NLS-1$

    JTextField artifactName;
    JTextField owner;
    JTextField infoSource;

    public ArtifactViewer(FormModel formModel,GameHolder gameHolder) {
        super(formModel, FORM_PAGE,gameHolder);
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
        this.artifactName.setText(Messages.getString("ArtifactViewer.NumberAndName", new Object[] { String.valueOf(a.getNumber()),a.getName()})); //$NON-NLS-1$
        this.artifactName.setCaretPosition(0);
        this.owner.setText(a.getOwner() != null ? a.getOwner() : Messages.getString("ArtifactViewer.OwnerUnclaimed")); //$NON-NLS-1$
        // show info sources, if needed
        InfoSource is = a.getInfoSource();
        if (DerivedFromSpellInfoSource.class.isInstance(is)) {
            DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource)is;
            String infoSourcesStr = Messages.getString("ArtifactViewer.SpellAtHex",new Object[] {sis.getSpell(),sis.getHexNoAsString()}); //$NON-NLS-1$
            this.infoSource.setVisible(true);
            for (InfoSource dsis : sis.getOtherInfoSources()) {
                if (DerivedFromSpellInfoSource.class.isInstance(dsis)) {
                    infoSourcesStr += ", " + Messages.getString("ArtifactViewer.SpellAtHex",new Object[] {((DerivedFromSpellInfoSource)dsis).getSpell(), ((DerivedFromSpellInfoSource)dsis).getHexNoAsString()}); //$NON-NLS-1$
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
        this.artifactName.setPreferredSize(this.uiSizes.newDimension(150/16, this.uiSizes.getHeight4()));
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
                    ArtifactInfo ai = (ArtifactInfo)ArtifactViewer.this.gameHolder.getGame().getMetadata().findFirstArtifactByNumber(a.getNumber());
                    if (ai == null) return;
                    final String descr = Messages.getString("ArtifactViewer.text",new Object[] {  //$NON-NLS-1$
                    					ai.getNo(), ai.getName(), ai.getAlignment(), ai.getPower1(), ai.getPower2()});
                    MessageDialog dlg = new MessageDialog(Messages.getString("ArtifactViewer.title"), descr); //$NON-NLS-1$
                    dlg.showDialog();
				}
			}


        });

        glb.append(this.owner = new JTextField());
        this.owner.setPreferredSize(this.uiSizes.newDimension(70/12, this.uiSizes.getHeight3()));
        this.owner.setBorder(null);

        glb.nextLine();

        glb.append(this.infoSource = new JTextField(), 2, 1);
        this.infoSource.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight3()));
        this.infoSource.setBorder(null);

        JPanel panel = glb.getPanel();
        panel.setBackground(UIManager.getColor("Panel.background"));
        return panel;
    }

}
