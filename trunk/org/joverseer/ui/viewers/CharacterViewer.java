package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.Character;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 19 Σεπ 2006
 * Time: 11:45:02 μμ
 * To change this template use File | Settings | File Templates.
 */
public class CharacterViewer extends AbstractForm {
    public static final String FORM_PAGE = "CharacterViewer";

    JTextField statsTextBox;

    public CharacterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);
        if (statsTextBox != null) {
            Character c = (Character)object;
            String txt = "";
            txt += getStatText("C", c.getCommand(), c.getCommandTotal());
            txt += getStatText("A", c.getAgent(), c.getAgentTotal());
            txt += getStatText("E", c.getEmmisary(), c.getEmmisaryTotal());
            txt += getStatText("E", c.getEmmisary(), c.getEmmisaryTotal());
            txt += getStatText("M", c.getMage(), c.getMageTotal());
            txt += getStatText("S", c.getStealth(), c.getStealthTotal());
            txt += getStatText("Cr", c.getChallenge(), c.getChallenge());
            txt += getStatText("H", c.getHealth(), c.getHealth());
            statsTextBox.setText(txt);
        }


    }

    private String getStatText(String prefix, int skill, int skillTotal) {
        if (skillTotal == 0) return "";
        return prefix + skill + (skillTotal != skill ? "(" + skillTotal + ")" : "") + " ";
    }



    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        BindingFactory bf = getBindingFactory();
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        JComponent c;

        glb.append(c = new JTextField());
        c.setBorder(null);
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        bf.bindControl(c, "name");
        glb.append(c = new JTextField());
        c.setBorder(null);
        bf.bindControl(c, "nationNo");

        glb.nextLine();

        glb.append(statsTextBox = new JTextField(), 2, 1);
        statsTextBox.setBorder(null);
        glb.nextLine();
        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }
}
