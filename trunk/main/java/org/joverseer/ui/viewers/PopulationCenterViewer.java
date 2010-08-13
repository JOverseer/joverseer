package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromInfluenceOtherInfoSource;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.PopulationCenterLoyaltyEstimator;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.views.EditPopulationCenterForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Shows pcs in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class PopulationCenterViewer extends ObjectViewer {

    public static final String FORM_PAGE = "PopulationCenterViewer";

    boolean showColor = true;

    JTextField loyalty;
    JTextField name;
    JTextField nation;
    JTextField sizeFort;
    JTextField lostThisTurn;
    JTextField productionDescription;
    JTextField turnInfo;
    HashMap<ProductEnum, JTextField> production = new HashMap<ProductEnum, JTextField>();
    HashMap<ProductEnum, JTextField> stores = new HashMap<ProductEnum, JTextField>();
    ArrayList<JLabel> productionLabels = new ArrayList<JLabel>();

    ActionCommand editPopulationCenter = new EditPopulationCenterCommand();
    ActionCommand toggleLostThisTurnCommand = new ToggleLostThisTurnCommand();
    ActionCommand deletePopulationCenterCommand = new DeletePopulationCenterCommand();
    
    public PopulationCenterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    public boolean appliesTo(Object obj) {
        return PopulationCenter.class.isInstance(obj);
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);

        PopulationCenter pc = (PopulationCenter) object;
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null)
            return;

        name.setText(GraphicUtils.parseName(pc.getName()));
        name.setCaretPosition(0);
        GameMetadata gm = game.getMetadata();

        int nationNo = 0;
        int loyaltyNo = 0;
        if (pc.getSize() != PopulationCenterSizeEnum.ruins) {
            nationNo = pc.getNationNo();
            loyaltyNo = pc.getLoyalty();
        }
        if (loyaltyNo == 0) {
            InfoSourceValue isv = PopulationCenterLoyaltyEstimator.getLoyaltyEstimateForPopCenter(pc);
            if (isv != null) {
                String txt = isv.getValue() + " (t"
                        + ((DerivedFromInfluenceOtherInfoSource) isv.getInfoSource()).getTurnNo() + ")";
                loyalty.setText(txt);
            } else {
                loyalty.setText(String.valueOf(loyaltyNo));
            }
        } else {
            loyalty.setText(String.valueOf(loyaltyNo));
        }

        nation.setText(gm.getNationByNum(nationNo).getShortName());
        nation.setCaretPosition(0);
        sizeFort.setText(UIUtils.enumToString(pc.getSize()));
        if (!pc.getFortification().equals(FortificationSizeEnum.none)) {
        	sizeFort.setText(sizeFort.getText() + " - " + UIUtils.enumToString(pc.getFortification()));
        }
        if (pc.getHarbor() != HarborSizeEnum.none) {
            sizeFort.setText(sizeFort.getText() + " - " + UIUtils.enumToString(pc.getHarbor()));
        }
        sizeFort.setCaretPosition(0);

        // show production
        // if the pop center is a ruin, search in past turns and find the first
        // turn where the pop center was not a ruin and report that production
        PopulationCenter pcForProduction = pc;
        Turn productionTurn = null;
        if (!hasProduction(pcForProduction)) {
            for (int i = game.getMaxTurn() - 1; i >= 0; i--) {
                productionTurn = game.getTurn(i);
                if (productionTurn == null)
                    continue;
                PopulationCenter pop = (PopulationCenter) productionTurn
                        .getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
                if (pop == null)
                    continue;
                if (pop.getSize() != PopulationCenterSizeEnum.ruins) {
                    if (hasProduction(pop)) {
                        pcForProduction = pop;
                        break;
                    }
                }
            }
        }
        if (pcForProduction != pc) {
            productionDescription.setText(String.format("Production from turn %s (%s).", productionTurn.getTurnNo(),
                    pcForProduction.getSize()));
            productionDescription.setVisible(true);
            setStoresVisible(false);
            setProductionLabelsVisible(true);
            setProductionVisible(true);
        } else {
            productionDescription.setVisible(false);
            // see if we have any production
            boolean hasProduction = false;
            for (ProductEnum pe : ProductEnum.values()) {
                if (pc.getProduction(pe) != null) {
                    hasProduction = true;
                    break;
                }
            }
            setAllProductionVisible(hasProduction);
        }

        for (ProductEnum p : ProductEnum.values()) {
            JTextField tf = (JTextField) production.get(p);
            Integer amt = pcForProduction.getProduction(p);
            String amtStr;
            if (amt == null || amt == 0) {
                amtStr = "  -";
            } else {
                amtStr = String.valueOf(amt);
            }
            tf.setText(amtStr);

            tf = (JTextField) stores.get(p);
            amt = pcForProduction.getStores(p);
            if (amt == null || amt == 0) {
                amtStr = "  -";
            } else {
                amtStr = String.valueOf(amt);
            }
            tf.setText(amtStr);

        }

        if (pc.getLostThisTurn()) {
            lostThisTurn.setText("Exp. to be lost this turn.");
            lostThisTurn.setVisible(true);
        } else {
            lostThisTurn.setVisible(false);
        }
        String turnInfoStr = "";
        if (pc.getSize() != PopulationCenterSizeEnum.ruins) {
            if (pc.getInfoSource().getTurnNo() < game.getCurrentTurn()) {
                turnInfoStr = "Info from t" + Math.max(pc.getInfoSource().getTurnNo(), 0);
            }
            ;
            if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource())) {
                PopCenterXmlInfoSource is = (PopCenterXmlInfoSource) pc.getInfoSource();
                if (is.getTurnNo() != is.getPreviousTurnNo()) {
                    turnInfoStr += (turnInfoStr.equals("") ? "" : " - ") + "Owner from t"
                            + Math.max(is.getPreviousTurnNo(), 0);
                }
            }
        }
        turnInfo.setText(turnInfoStr);
        turnInfo.setVisible(!turnInfoStr.equals(""));
        // else if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource()) &&
        // ((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo() < game.getCurrentTurn()) {
        // turnInfo.setVisible(true);
        // turnInfo.setText("Info from turn " +
        // Math.max(((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo(), 0));
        //                
        // }


        if (getShowColor()) {
            Game g = GameHolder.instance().getGame();
            Turn t = g.getTurn();

            NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty(
                    "nationNo", nationNo);
            Color col;
            if (nr == null) {
                col = ColorPicker.getInstance().getColor(NationAllegianceEnum.Neutral.toString());
            } else {
                col = ColorPicker.getInstance().getColor(nr.getAllegiance().toString());
            }
            name.setForeground(col);
        }
    }
    
    protected boolean hasProduction(PopulationCenter pop) {
        for (ProductEnum p : ProductEnum.values()) {
            if (pop.getProduction(p) != null && pop.getProduction(p) > 0) {
                return true;
            }
        }
        return false;
    }

    protected void setStoresVisible(boolean visible) {
        for (JTextField f : stores.values()) {
            f.setVisible(visible);
        }
    }

    protected void setProductionVisible(boolean visible) {
        for (JTextField f : production.values()) {
            f.setVisible(visible);
        }
    }

    protected void setProductionLabelsVisible(boolean visible) {
        for (JLabel lbl : productionLabels) {
            lbl.setVisible(visible);
        }
    }

    protected void setAllProductionVisible(boolean visible) {
        setStoresVisible(visible);
        setProductionVisible(visible);
        setProductionLabelsVisible(visible);
    }

    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        JComponent c;

        glb.append(name = new JTextField());
        c = name;
        c.setPreferredSize(new Dimension(100, 12));
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        c.setBorder(null);

        glb.append(sizeFort = new JTextField());
        c = sizeFort;
        c.setPreferredSize(new Dimension(120, 12));
        c.setBorder(null);

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");

        JButton btnMenu = new JButton();
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        btnMenu.setIcon(ico);
        btnMenu.setPreferredSize(new Dimension(16, 16));
        glb.append(btnMenu);
        btnMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                return createPopulationCenterPopupContextMenu();
            }
        });

        glb.nextLine();

        glb.append(nation = new JTextField());
        c = nation;
        c.setBorder(null);

        glb.append(loyalty = new JTextField());
        loyalty.setBorder(null);
        glb.nextLine();

        glb.append(productionDescription = new JTextField(), 2, 1);
        productionDescription.setBorder(null);
        Font f = GraphicUtils.getFont(productionDescription.getFont().getName(), Font.ITALIC, productionDescription
                .getFont().getSize());
        productionDescription.setFont(f);
        glb.nextLine();
        f = GraphicUtils.getFont("Arial", Font.PLAIN, 9);

        TableLayoutBuilder tlb = new TableLayoutBuilder();
        for (ProductEnum p : ProductEnum.values()) {
            JLabel label = new JLabel(" " + p.getCode());
            productionLabels.add(label);
            label.setPreferredSize(new Dimension(28, 12));
            label.setFont(f);
            tlb.cell(label);
        }
        tlb.row();
        for (ProductEnum p : ProductEnum.values()) {
            JTextField tf = new JTextField();
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(28, 16));
            tf.setFont(f);
            tlb.cell(tf);
            production.put(p, tf);
        }
        tlb.row();
        for (ProductEnum p : ProductEnum.values()) {
            JTextField tf = new JTextField();
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(28, 16));
            tf.setFont(f);
            tlb.cell(tf);
            stores.put(p, tf);
        }
        tlb.row();

        tlb.cell(lostThisTurn = new JTextField());
        lostThisTurn.setBorder(null);
        lostThisTurn.setFont(GraphicUtils.getFont(lostThisTurn.getFont().getName(), Font.ITALIC, lostThisTurn.getFont()
                .getSize()));
        lostThisTurn.setPreferredSize(new Dimension(100, 12));

        tlb.row();
        tlb.cell(turnInfo = new JTextField());
        turnInfo.setBorder(null);
        turnInfo.setFont(GraphicUtils.getFont(lostThisTurn.getFont().getName(), Font.ITALIC, lostThisTurn.getFont()
                .getSize()));
        turnInfo.setPreferredSize(new Dimension(100, 12));


        JPanel pnl = tlb.getPanel();
        pnl.setBackground(Color.white);
        glb.append(pnl, 2, 1);

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }

    private JPopupMenu createPopulationCenterPopupContextMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "populationCenterCommandGroup",
                new Object[] {toggleLostThisTurnCommand, "separator", editPopulationCenter,
                        deletePopulationCenterCommand, "separator", new ShowCaptureInformationCommand(), "separator", new ShowInfoSourcePopupCommand(((PopulationCenter)getFormObject()).getInfoSource())});
        return group.createPopupMenu();
    }

    private class DeletePopulationCenterCommand extends ActionCommand {
    	boolean cancelAction;
        protected void doExecuteCommand() {
        	cancelAction = true;
            PopulationCenter pc = (PopulationCenter) getFormObject();
            ConfirmationDialog cdlg = new ConfirmationDialog("Warning", "Are you sure you want to delete population center '" + pc.getName() + "'?") {
                protected void onCancel() {
                    super.onCancel();
                }
                
                protected void onConfirm() {
                	cancelAction = false;
                }
                
            };
            cdlg.showDialog();
            if (cancelAction) return;
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Turn t = g.getTurn();
            Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
            pcs.removeItem(pc);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }
    
    private class ShowCaptureInformationCommand extends ActionCommand {

        protected void doExecuteCommand() {
            PopulationCenter pc = (PopulationCenter) getFormObject();
            if (pc.getSize().equals(PopulationCenterSizeEnum.ruins)) return;
            int loyalty = pc.getLoyalty();
            boolean estimateLoyalty = false;
            if (loyalty == 0) {
            	estimateLoyalty = true;
            	if (pc.getSize().equals(PopulationCenterSizeEnum.camp)) {
            		loyalty = 20;
            	} else if (pc.getSize().equals(PopulationCenterSizeEnum.village)) {
            		loyalty = 35;
            	} else if (pc.getSize().equals(PopulationCenterSizeEnum.town)) {
            		loyalty = 50;
            	} else if (pc.getSize().equals(PopulationCenterSizeEnum.majorTown)) {
            		loyalty = 70;
            	} else if (pc.getSize().equals(PopulationCenterSizeEnum.city)) {
            		loyalty = 90;
            	}
            }
            
            CombatPopCenter combatPc = new CombatPopCenter(pc);
            combatPc.setLoyalty(loyalty);
            
            Combat combat = new Combat();
            combat.setSide2Pc(combatPc);
            int strength = combat.computePopCenterStrength(combatPc);
            
            Game game = GameHolder.instance().getGame();
            Hex hex = game.getMetadata().getHex(pc.getHexNo());
            HexInfo hi = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", pc.getHexNo());
            
            boolean estimateClimate = false;
            ClimateEnum climate = ClimateEnum.Mild;
            if (hi == null || hi.getClimate() == null) {
            	estimateClimate = true;
            	climate = ClimateEnum.Mild;
            }
            
            CombatArmy ca = CombatUtils.findCombatArmyRequiredToCapturePopCenter(pc.getSize(), pc.getFortification(), loyalty, climate, hex.getTerrain());
            if (ca == null) return;
            int captureEHI = ca.getHI().getNumber();
            int lostEHI = (int)Math.round(captureEHI * (100d - ca.getLosses())/100);
            
            String msg = "Population Center combat statistics estimates:";
            msg += "\n" + pc.getName() + " has " + strength + " strength. If it is attacked by an enemy army:";
            msg += "\n- " + captureEHI + " enHI are required to capture this pop center.";
            msg += "\n- " + lostEHI + " enHI will be lost by the attacking army.";
            if (estimateLoyalty) {
            	msg += "\n * Because the loyalty of this pop center is unknown, an estimate of " + loyalty + " was used.";
            }
            if (estimateClimate) {
            	msg += "\n * Because the climate is unknown, Mild climate was used.";
            }
            msg += "\n * Hated relations are assumed for both the attacker and the defender.";
            msg += "\nNote: The numbers are rough estimates. To get accurate numbers it is advised to do the calculations by hand.";
            
            MessageDialog dlg = new MessageDialog("Population Center combat", msg);
        	dlg.showDialog();
        }
    }

    private class ToggleLostThisTurnCommand extends ActionCommand {

        protected void doExecuteCommand() {
            PopulationCenter pc = (PopulationCenter) getFormObject();
            pc.setLostThisTurn(!pc.getLostThisTurn());
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }

    private class EditPopulationCenterCommand extends ActionCommand {

        protected void doExecuteCommand() {
            final PopulationCenter pc = (PopulationCenter) getFormObject();
            FormModel formModel = FormModelHelper.createFormModel(pc);
            final EditPopulationCenterForm form = new EditPopulationCenterForm(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(form);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {

                protected void onAboutToShow() {
                }

                protected boolean onFinish() {
                    form.commit();
                    GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).refreshItem(pc);
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
                    return true;
                }
            };
            MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("editPopulationCenterDialog.title", new Object[] {String.valueOf(pc
                    .getHexNo())}, Locale.getDefault()));
            dialog.showDialog();
        }
    }


    public boolean getShowColor() {
        return showColor;
    }


    public void setShowColor(boolean showColor) {
        this.showColor = showColor;
    }

}
