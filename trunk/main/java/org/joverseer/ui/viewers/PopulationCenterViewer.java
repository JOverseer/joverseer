package org.joverseer.ui.viewers;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ColorPicker;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.joverseer.ui.views.EditPopulationCenterForm;
import org.joverseer.ui.views.NewGameForm;
import org.joverseer.domain.ProductEnum;

import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;


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
    HashMap production = new HashMap();
    HashMap stores = new HashMap();

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

        PopulationCenter pc = (PopulationCenter)object;
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) return;
        
        name.setText(GraphicUtils.parseName(pc.getName()));
        
        GameMetadata gm = game.getMetadata();

        int nationNo = 0;
        int loyaltyNo = 0;
        if (pc.getSize() != PopulationCenterSizeEnum.ruins) {
            nationNo = pc.getNationNo();
            loyaltyNo = pc.getLoyalty();
        }

        nation.setText(gm.getNationByNum(nationNo).getShortName());
        loyalty.setText(String.valueOf(loyaltyNo));
        
        
        sizeFort.setText(pc.getSize().toString() + " - " + pc.getFortification().toString());
        
        if (pc.getHarbor() != HarborSizeEnum.none) {
        	sizeFort.setText(sizeFort.getText() + " - " + pc.getHarbor().toString());
        } 
        
        // show production
        // if the pop center is a ruin, search in past turns and find the first
        // turn where the pop center was not a ruin and report that production
        PopulationCenter pcForProduction = pc;
        Turn productionTurn = null;
        if (pcForProduction.getSize() == PopulationCenterSizeEnum.ruins) {
            for (int i=game.getMaxTurn()-1; i>=0; i--) {
                productionTurn = game.getTurn(i);
                if (productionTurn == null) continue;
                PopulationCenter pop = (PopulationCenter)productionTurn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
                if (pop == null) continue;
                if (pop.getSize() != PopulationCenterSizeEnum.ruins) {
                    boolean hasProduction = false;
                    for (ProductEnum p : ProductEnum.values()) {
                        if (pop.getProduction(p) != null && pop.getProduction(p) > 0) {
                            hasProduction = true;
                        }
                    }
                    if (hasProduction) {
                        pcForProduction = pop;
                        break;
                    }
                }
            }
        }
        if (pcForProduction != pc) {
            productionDescription.setText(String.format("Production from turn %s (%s).", productionTurn.getTurnNo(), pcForProduction.getSize()));
            productionDescription.setVisible(true);
        } else {
            productionDescription.setVisible(false);
        }
        
        for (ProductEnum p : ProductEnum.values()) {
            JTextField tf = (JTextField)production.get(p);
            Integer amt = pcForProduction.getProduction(p);
            String amtStr;
            if (amt == null || amt == 0) {
                amtStr = "  -";
            } else {
                amtStr = String.valueOf(amt);
            }
            tf.setText(amtStr);

            tf = (JTextField)stores.get(p);
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
            };
            if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource())) {
    	        PopCenterXmlInfoSource is = (PopCenterXmlInfoSource)pc.getInfoSource();
    	        if (is.getTurnNo() != is.getPreviousTurnNo()) {
    	        	turnInfoStr += (turnInfoStr.equals("") ? "" : " - ") + "Owner from t" + is.getPreviousTurnNo();
    	        }
            }
        }
        turnInfo.setText(turnInfoStr);
        turnInfo.setVisible(!turnInfoStr.equals(""));
//        else if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource()) &&
//                ((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo() < game.getCurrentTurn()) {
//            turnInfo.setVisible(true);
//            turnInfo.setText("Info from turn " + Math.max(((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo(), 0));
//                
//        }
       
        
        if (getShowColor()) {
            Game g = GameHolder.instance().getGame();
            Turn t = g.getTurn();
            
            NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
            Color col;
            if (nr == null) {
                col = ColorPicker.getInstance().getColor(NationAllegianceEnum.Neutral.toString());
            } else {
                col = ColorPicker.getInstance().getColor(nr.getAllegiance().toString());
            }
            name.setForeground(col);
        }
    }

    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        BindingFactory bf = getBindingFactory();
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
        c.setPreferredSize(new Dimension(100, 12));
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
        Font f = GraphicUtils.getFont(productionDescription.getFont().getName(), Font.ITALIC, productionDescription.getFont().getSize());
        productionDescription.setFont(f);
        glb.nextLine();
        f = GraphicUtils.getFont("Arial", Font.PLAIN, 9);
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        for (ProductEnum p : ProductEnum.values()) {
            JLabel label = new JLabel(" " + p.getCode());
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
        lostThisTurn.setFont(GraphicUtils.getFont(lostThisTurn.getFont().getName(), Font.ITALIC, lostThisTurn.getFont().getSize()));
        lostThisTurn.setPreferredSize(new Dimension(100, 12));
        
        tlb.row();
        tlb.cell(turnInfo = new JTextField());
        turnInfo.setBorder(null);
        turnInfo.setFont(GraphicUtils.getFont(lostThisTurn.getFont().getName(), Font.ITALIC, lostThisTurn.getFont().getSize()));
        turnInfo.setPreferredSize(new Dimension(100, 12));
        
        
        JPanel pnl = tlb.getPanel();
        pnl.setBackground(Color.white);
        glb.append(pnl, 2, 1);
        
        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }
    
    private JPopupMenu createPopulationCenterPopupContextMenu() {
        PopulationCenter pc = (PopulationCenter) getFormObject();
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "populationCenterCommandGroup",
                new Object[] {toggleLostThisTurnCommand, "separator", editPopulationCenter, "separator", deletePopulationCenterCommand});
        return group.createPopupMenu();
    }
    
    private class DeletePopulationCenterCommand extends ActionCommand {
        protected void doExecuteCommand() {
            PopulationCenter pc = (PopulationCenter)getFormObject();
            Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Turn t = g.getTurn();
            Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
            pcs.removeItem(pc);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }
    
    private class ToggleLostThisTurnCommand extends ActionCommand {
        protected void doExecuteCommand() {
            PopulationCenter pc = (PopulationCenter)getFormObject();
            pc.setLostThisTurn(!pc.getLostThisTurn());
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }
    
    private class EditPopulationCenterCommand extends ActionCommand {

        protected void doExecuteCommand() {
            final PopulationCenter pc = (PopulationCenter)getFormObject();
            FormModel formModel = FormModelHelper.createFormModel(pc);
            final EditPopulationCenterForm form = new EditPopulationCenterForm(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(form);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                }

                protected boolean onFinish() {
                    form.commit();
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
                    return true;
                }
            };
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("editPopulationCenterDialog.title", new Object[]{String.valueOf(pc.getHexNo())}, Locale.getDefault()));
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

