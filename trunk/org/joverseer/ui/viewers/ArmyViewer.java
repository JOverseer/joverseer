package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.Army;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 6 Οκτ 2006
 * Time: 10:41:27 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArmyViewer extends AbstractForm {
    public static final String FORM_PAGE = "ArmyViewer";

    JTextField commanderName;
    JTextField nation;
    JTextField armySize;
    JTextField armyMorale;
    JTextField extraInfo;
    JTextField food;

    public ArmyViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        glb.append(commanderName = new JTextField());
        glb.append(nation = new JTextField());
        glb.nextLine();
        glb.append(armyMorale = new JTextField());
        glb.nextLine();
        glb.append(extraInfo = new JTextField());
        glb.nextLine();
        glb.append(food = new JTextField());

        commanderName.setBorder(null);
        commanderName.setFont(new Font(commanderName.getFont().getName(), Font.BOLD, commanderName.getFont().getSize()));
        nation.setBorder(null);
        armyMorale.setBorder(null);
        extraInfo.setBorder(null);
        food.setBorder(null);

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);

        Army army = (Army)object;
        commanderName.setText(army.getCommanderTitle() + " " + army.getCommanderName());
        nation.setText(String.valueOf(army.getNationNo()));
        //todo fix these
        armyMorale.setVisible(false);
        extraInfo.setVisible(false);
        food.setVisible(false);
        //armyMorale.setText("M: 0");
        //extraInfo.setText(army.get);
    }
}
