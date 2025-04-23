package org.joverseer.ui.viewers;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.joverseer.domain.NationMessage;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows nation messages in the Current Hex View
 *
 * @author Marios Skounakis
 */
public class NationMessageViewer extends ObjectViewer {
    public static final String FORM_PAGE = "NationMessageViewer";

    JTextArea rumor;
    JTextField nation;

    public NationMessageViewer(FormModel formModel,GameHolder gameHolder) {
        super(formModel, FORM_PAGE,gameHolder);
    }

    @Override
	public boolean appliesTo(Object obj) {
        return NationMessage.class.isInstance(obj);
    }

    @Override
	protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));
        this.rumor = new JTextArea(2, 1);
        //rumor.setPreferredSize(new Dimension(220, 80));
        this.rumor.setLineWrap(true);
        this.rumor.setWrapStyleWord(true);
        JScrollPane scp = new JScrollPane(this.rumor);
        scp.setPreferredSize(new Dimension(240, 36));
        scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scp.getVerticalScrollBar().setPreferredSize(new Dimension(16, 10));
        //rumor.setBorder(null);
        scp.setBorder(null);
        glb.append(scp);

//        nation = new JTextField();
//        nation.setSize(new Dimension(50, 12));
//        nation.setBorder(null);
//        glb.append(nation);
        JPanel p = glb.getPanel();
        p.setBackground(UIManager.getColor("Panel.background"));
        return p;
    }

    @Override
	public void setFormObject(Object obj) {
        NationMessage nm = (NationMessage)obj;
        Game game = this.gameHolder.getGame();
        if (game == null) return;
        GameMetadata gm = game.getMetadata();
        String nationName = gm.getNationByNum(nm.getNationNo()).getShortName();
        this.rumor.setText(nm.getMessage() + " [" + nationName + "]");
        this.rumor.setCaretPosition(0);
        //nation.setText();
    }
}
