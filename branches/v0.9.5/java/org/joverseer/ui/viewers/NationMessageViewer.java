package org.joverseer.ui.viewers;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.NationMessage;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

import javax.swing.*;
import java.awt.*;


public class NationMessageViewer extends ObjectViewer {
    public static final String FORM_PAGE = "NationMessageViewer";

    JTextArea rumor;
    JTextField nation;

    public NationMessageViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    public boolean appliesTo(Object obj) {
        return NationMessage.class.isInstance(obj);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));
        rumor = new JTextArea(2, 1);
        //rumor.setPreferredSize(new Dimension(220, 80));
        rumor.setLineWrap(true);
        rumor.setWrapStyleWord(true);
        JScrollPane scp = new JScrollPane(rumor);
        scp.setPreferredSize(new Dimension(240, 36));
        scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scp.getVerticalScrollBar().setPreferredSize(new Dimension(16, 10));
        //rumor.setBorder(null);
        scp.setBorder(null);
        glb.append(scp);

//        nation = new JTextField();
//        nation.setSize(new Dimension(50, 12));
//        nation.setBorder(null);
//        glb.append(nation);
        JPanel p = glb.getPanel();
        p.setBackground(Color.white);
        return p;
    }

    public void setFormObject(Object obj) {
        NationMessage nm = (NationMessage)obj;
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) return;
        GameMetadata gm = game.getMetadata();
        String nationName = gm.getNationByNum(nm.getNationNo()).getShortName();
        rumor.setText(nm.getMessage() + " [" + nationName + "]");
        rumor.setCaretPosition(0);
        //nation.setText();
    }
}
