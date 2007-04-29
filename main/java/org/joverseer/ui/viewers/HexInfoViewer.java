package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.joverseer.domain.HexInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.ShowCharacterFastStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterLongStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterMovementRangeCommand;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class HexInfoViewer extends ObjectViewer {

    public static final String FORM_PAGE = "HexInfoViewer";

    HashMap<MovementDirection, JTextField> infCosts = new HashMap<MovementDirection, JTextField>();
    HashMap<MovementDirection, JTextField> cavCosts = new HashMap<MovementDirection, JTextField>();

    JTextField hexNo;
    JTextField climate;
    
    RemoveBridgeCommand removeBridgeNE = new RemoveBridgeNE();
    RemoveBridgeCommand removeBridgeE = new RemoveBridgeE();
    RemoveBridgeCommand removeBridgeSE = new RemoveBridgeSE();
    RemoveBridgeCommand removeBridgeSW = new RemoveBridgeSW();
    RemoveBridgeCommand removeBridgeW = new RemoveBridgeW();
    RemoveBridgeCommand removeBridgeNW = new RemoveBridgeNW();

    AddBridgeCommand addBridgeNE = new AddBridgeNE();
    AddBridgeCommand addBridgeE = new AddBridgeE();
    AddBridgeCommand addBridgeSE = new AddBridgeSE();
    AddBridgeCommand addBridgeSW = new AddBridgeSW();
    AddBridgeCommand addBridgeW = new AddBridgeW();
    AddBridgeCommand addBridgeNW = new AddBridgeNW();
    
    ShowFedInfantryArmyRangeCommand showFedInfantryArmyRangeCommand = new ShowFedInfantryArmyRangeCommand();
    ShowUnfedInfantryArmyRangeCommand showUnfedInfantryArmyRangeCommand = new ShowUnfedInfantryArmyRangeCommand();
    ShowFedCavalryArmyRangeCommand showFedCavalryArmyRangeCommand = new ShowFedCavalryArmyRangeCommand();
    ShowUnfedCavalryArmyRangeCommand showUnfedCavalryArmyRangeCommand = new ShowUnfedCavalryArmyRangeCommand();
    
    

    public HexInfoViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    public boolean appliesTo(Object obj) {
        return HexInfo.class.isInstance(obj);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        JLabel l;
        lb.append(l = new JLabel("Hex No :"), 2, 1);
        lb.append(hexNo = new JTextField(), 2, 1);
        hexNo.setBorder(null);
        lb.append(climate = new JTextField(), 2, 1);
        climate.setPreferredSize(new Dimension(50, 12));
        climate.setBorder(null);
        
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        JButton btnMenu = new JButton();
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        btnMenu.setIcon(ico);
        btnMenu.setPreferredSize(new Dimension(16, 16));
        lb.append(btnMenu);
        btnMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                return createHexInfoPopupContextMenu();
            }
        });
        
        lb.nextLine();
        lb.append(new JSeparator(), 5, 1);
        lb.nextLine();

        lb.append(l = new JLabel("Inf"), 2, 1);
        Font f = new Font(l.getFont().getName(), Font.BOLD, l.getFont().getSize());
        l.setFont(f);
        lb.append(l = new JLabel(" "));
        l.setPreferredSize(new Dimension(20, 12));
        lb.append(l = new JLabel("Cav"), 2, 1);
        l.setFont(f);
        lb.nextLine();
        for (MovementDirection md : MovementDirection.values()) {
            lb.append(new JLabel(md.getDir().toUpperCase() + " :"));
            JTextField tf = new JTextField();
            tf.setHorizontalAlignment(JLabel.RIGHT);
            infCosts.put(md, tf);
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(20, 12));
            lb.append(tf);

            lb.append(l = new JLabel(" "));

            lb.append(new JLabel(md.getDir().toUpperCase() + " :"));
            tf = new JTextField();
            tf.setHorizontalAlignment(JLabel.RIGHT);
            cavCosts.put(md, tf);
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(20, 12));
            lb.append(tf);
            lb.nextLine();
        }
        JPanel panel = lb.getPanel();
        panel.setBackground(Color.WHITE);
        return panel;
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);
        if (object != null) {
            Hex h = (Hex) object;
            String hexNoStr = String.valueOf(h.getColumn());
            if (h.getColumn() < 10) {
                hexNoStr = "0" + hexNoStr;
            }
            if (h.getRow() < 10) {
                hexNoStr = hexNoStr + "0";
            }
            hexNoStr += String.valueOf(h.getRow());
            hexNo.setText(hexNoStr);
            
            Game g = GameHolder.instance().getGame();
            HexInfo hi = (HexInfo)g.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", h.getHexNo());
            climate.setText(hi.getClimate() != null ? hi.getClimate().toString() : "");

            int startHexNo = h.getColumn() * 100 + h.getRow();
            if (startHexNo > 0) {
                for (MovementDirection md : MovementDirection.values()) {
                    int cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), false, true, true, null, startHexNo);
                    JTextField tf = (JTextField) infCosts.get(md);
                    String costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));

                    cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), true, true, true, null, startHexNo);
                    tf = (JTextField) cavCosts.get(md);
                    costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));
                }
                return;
            }
            for (JTextField tf : (Collection<JTextField>) infCosts.values()) {
                tf.setText("");
            }
            for (JTextField tf : (Collection<JTextField>) cavCosts.values()) {
                tf.setText("");
            }
        }
    }
    
    protected JPopupMenu createHexInfoPopupContextMenu() {
    	Hex hex = (Hex)getFormObject();
        ActionCommand showCharacterLongStrideRangeCommand = new ShowCharacterLongStrideRangeCommand(hex.getHexNo());
        ActionCommand showCharacterFastStrideRangeCommand = new ShowCharacterFastStrideRangeCommand(hex.getHexNo());
        ActionCommand showCharacterRangeOnMapCommand = new ShowCharacterMovementRangeCommand(hex.getHexNo(), 12);

        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "hexInfoCommandGroup",
                new Object[] {addBridgeNE, addBridgeE, addBridgeSE, addBridgeSW, addBridgeW, addBridgeNW, 
                        "separator",
                        removeBridgeNE, removeBridgeE, removeBridgeSE, removeBridgeSW, removeBridgeW, removeBridgeNW,
                        "separator",
                        showCharacterRangeOnMapCommand,
                        showCharacterLongStrideRangeCommand,
                        showCharacterFastStrideRangeCommand,
                        "separator",
                        showFedInfantryArmyRangeCommand,
                        showUnfedInfantryArmyRangeCommand,
                        showFedCavalryArmyRangeCommand,
                        showUnfedCavalryArmyRangeCommand,
                        });
        return group.createPopupMenu();
    }
    
    public class RemoveBridgeSE extends RemoveBridgeCommand {
        public RemoveBridgeSE() {
            super(HexSideEnum.BottomRight);
        }
    }

    public class RemoveBridgeE extends RemoveBridgeCommand {
        public RemoveBridgeE() {
            super(HexSideEnum.Right);
        }
    }

    public class RemoveBridgeSW extends RemoveBridgeCommand {
        public RemoveBridgeSW() {
            super(HexSideEnum.BottomLeft);
        }
    }

    public class RemoveBridgeNE extends RemoveBridgeCommand {
        public RemoveBridgeNE() {
            super(HexSideEnum.TopRight);
        }
    }

    public class RemoveBridgeW extends RemoveBridgeCommand {
        public RemoveBridgeW() {
            super(HexSideEnum.Left);
        }
    }

    public class RemoveBridgeNW extends RemoveBridgeCommand {
        public RemoveBridgeNW() {
            super(HexSideEnum.TopLeft);
        }
    }

    public class RemoveBridgeCommand extends ActionCommand {
        HexSideEnum side;
        
        public RemoveBridgeCommand(HexSideEnum side) {
            super();
            this.side = side;
        }

        protected void doExecuteCommand() {
            Hex hex = (Hex)getFormObject();
            HexSideElementEnum hse = null;
            for (HexSideElementEnum hsei : (ArrayList<HexSideElementEnum>)hex.getHexSideElements(side)) {
                if (hsei == HexSideElementEnum.Bridge) {
                    hse = hsei;
                }
            }
            if (hse != null) {
                hex.getHexSideElements(side).remove(hse);
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));

            }
        }
    }
    
    public class AddBridgeSE extends AddBridgeCommand {
        public AddBridgeSE() {
            super(HexSideEnum.BottomRight);
        }
    }

    public class AddBridgeE extends AddBridgeCommand {
        public AddBridgeE() {
            super(HexSideEnum.Right);
        }
    }

    public class AddBridgeSW extends AddBridgeCommand {
        public AddBridgeSW() {
            super(HexSideEnum.BottomLeft);
        }
    }

    public class AddBridgeNE extends AddBridgeCommand {
        public AddBridgeNE() {
            super(HexSideEnum.TopRight);
        }
    }

    public class AddBridgeW extends AddBridgeCommand {
        public AddBridgeW() {
            super(HexSideEnum.Left);
        }
    }

    public class AddBridgeNW extends AddBridgeCommand {
        public AddBridgeNW() {
            super(HexSideEnum.TopLeft);
        }
    }
    
    public class AddBridgeCommand extends ActionCommand {
        HexSideEnum side;
        
        public AddBridgeCommand(HexSideEnum side) {
            super();
            this.side = side;
        }

        protected void doExecuteCommand() {
            Hex hex = (Hex)getFormObject();
            boolean hasRoad = false;
            boolean hasMinorRiver = false;
            boolean hasMajorRiver = false;
            for (HexSideElementEnum hsei : (ArrayList<HexSideElementEnum>)hex.getHexSideElements(side)) {
                if (hsei == HexSideElementEnum.Bridge) {
                    return;
                }
                if (hsei == HexSideElementEnum.MinorRiver) {
                    hasMinorRiver = true;
                }
                if (hsei == HexSideElementEnum.Road) {
                    hasRoad = true;
                    if (hsei == HexSideElementEnum.MajorRiver) {
                        hasMajorRiver = true;
                    }
                }
            }
            if (!hasMinorRiver && !(hasMajorRiver && hasRoad)) return;
            hex.addHexSideElement(side, HexSideElementEnum.Bridge);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
        }
    }
    
    public class ShowArmyRangeCommand extends ActionCommand {
        boolean cav;
        boolean fed;
        
        public ShowArmyRangeCommand(String arg0, boolean cav, boolean fed) {
            super(arg0);
            this.cav = cav;
            this.fed = fed;
        }

        protected void doExecuteCommand() {
            Hex hex = (Hex)getFormObject();
            ArmyRangeMapItem armi = new ArmyRangeMapItem(hex.getHexNo(), cav, fed);
            AbstractMapItem.add(armi);

            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }
    
    public class ShowFedInfantryArmyRangeCommand extends ShowArmyRangeCommand {
        public ShowFedInfantryArmyRangeCommand() {
            super("showFedInfantryArmyRangeCommand", false, true);
        }
    }

    public class ShowUnfedInfantryArmyRangeCommand extends ShowArmyRangeCommand {
        public ShowUnfedInfantryArmyRangeCommand() {
            super("showUnfedInfantryArmyRangeCommand", false, false);
        }
    }

    public class ShowFedCavalryArmyRangeCommand extends ShowArmyRangeCommand {
        public ShowFedCavalryArmyRangeCommand() {
            super("showFedCavalryArmyRangeCommand", true, true);
        }
    }

    public class ShowUnfedCavalryArmyRangeCommand extends ShowArmyRangeCommand {
        public ShowUnfedCavalryArmyRangeCommand() {
            super("showUnfedCavalryArmyRangeCommand", true, false);
        }
    }

}
