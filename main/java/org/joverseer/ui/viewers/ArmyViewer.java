package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.*;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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

    ActionCommand showArmyMovementRangeAction = new ShowArmyMovementRangeAction();
    ActionCommand toggleFedAction = new ToggleFedAction();

    public ArmyViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        glb.append(commanderName = new JTextField());
        commanderName.setPreferredSize(new Dimension(160, 12));
        glb.append(nation = new JTextField());
        nation.setPreferredSize(new Dimension(30, 12));

        // button to show range of army on map
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        JButton btnMenu = new JButton();
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        btnMenu.setPreferredSize(new Dimension(16, 16));
        btnMenu.setIcon(ico);
        glb.append(btnMenu);
        btnMenu.addActionListener(new PopupMenuActionListener()
        {
            public JPopupMenu getPopupMenu() {
                return createArmyPopupContextMenu();
            }
        });

        glb.nextLine();
        glb.append(armySize = new JTextField());
        armySize.setPreferredSize(new Dimension(100, 12));
        glb.append(armyMorale = new JTextField());
        armyMorale.setPreferredSize(new Dimension(30, 12));
        glb.nextLine();
        glb.append(extraInfo = new JTextField());
        extraInfo.setPreferredSize(new Dimension(100, 12));
        glb.nextLine();
        glb.append(food = new JTextField());
        food.setPreferredSize(new Dimension(100, 12));

        commanderName.setBorder(null);
        commanderName.setFont(new Font(commanderName.getFont().getName(), Font.BOLD, commanderName.getFont().getSize()));
        nation.setBorder(null);
        armySize.setBorder(null);
        armyMorale.setBorder(null);
        extraInfo.setBorder(null);
        food.setBorder(null);

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);

        Army army = (Army) object;
        commanderName.setText(army.getCommanderTitle() + " " + army.getCommanderName());

        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) return;
        GameMetadata gm = game.getMetadata();
        nation.setText(gm.getNationByNum(army.getNationNo()).getShortName());

        armySize.setText("Size: " + army.getSize().toString());
        armyMorale.setText("M:");
        if (army.getElements().size() > 0) {
            extraInfo.setText("");
            extraInfo.setVisible(true);
            for (ArmyElement element : army.getElements()) {
                extraInfo.setText(extraInfo.getText() +
                        (extraInfo.getText().equals("") ? "" : " ") +
                        element.getDescription());
            }
        } else {
            extraInfo.setVisible(false);
        }
        String foodStr = "";
        if (army.getFood() != null) {
            foodStr = army.getFood().toString() + " ";
        }
        Boolean fed = army.computeFed();

        foodStr += (fed != null && fed == true ? "Fed" : "Unfed");
        food.setText(foodStr);
        //armyMorale.setText("M: 0");
    }

    private JPopupMenu createArmyPopupContextMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "armyCommandGroup",
                new Object[]{showArmyMovementRangeAction, toggleFedAction});
        return group.createPopupMenu();
    }

    private class ShowArmyMovementRangeAction extends ActionCommand {
        public ShowArmyMovementRangeAction() {
            super("showArmyMovementRangeAction");
        }

        protected void doExecuteCommand() {
            ArmyRangeMapItem armi = new ArmyRangeMapItem((org.joverseer.domain.Army) getFormObject());
            org.joverseer.support.Container mic = (org.joverseer.support.Container) Application.instance().getApplicationContext().getBean("mapItemContainer");
            mic.removeAll(mic.items);
            AbstractMapItem.add(armi);

            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
        }
    }

    private class ToggleFedAction extends ActionCommand {
        public ToggleFedAction() {
            super("toggleFedAction");
        }

        protected void doExecuteCommand() {
            Army a = (org.joverseer.domain.Army) getFormObject();
            Boolean fed = a.computeFed();
            a.setFed(fed == null || fed != true ? true : false);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
        }
    }
}
