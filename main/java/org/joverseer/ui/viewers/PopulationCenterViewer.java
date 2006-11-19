package org.joverseer.ui.viewers;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 18, 2006
 * Time: 8:57:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenterViewer extends AbstractForm {
    public static final String FORM_PAGE = "PopulationCenterViewer";

    JTextField nation;
    JTextField sizeFort;

    public PopulationCenterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);

        PopulationCenter pc = (PopulationCenter)object;
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) return;
        GameMetadata gm = game.getMetadata();
        nation.setText(gm.getNationByNum(pc.getNationNo()).getShortName());

        sizeFort.setText(pc.getSize().toString() + " - " + pc.getFortification().toString());
    }

    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        BindingFactory bf = getBindingFactory();
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        JComponent c;

        glb.append(c = new JTextField());
        c.setPreferredSize(new Dimension(100, 12));
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        c.setBorder(null);
        bf.bindControl(c, "name");

        glb.append(sizeFort = new JTextField());
        c = sizeFort;
        c.setPreferredSize(new Dimension(100, 12));
        c.setBorder(null);

        glb.nextLine();

        glb.append(nation = new JTextField());
        c = nation;
        c.setBorder(null);
        //bf.bindControl(c, "nationNo");

        glb.nextLine();

        glb.append(c = new JTextField());
        c.setBorder(null);
        bf.bindControl(c, "loyalty");
        glb.nextLine();

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }
}

