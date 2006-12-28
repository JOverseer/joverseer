package org.joverseer.ui.viewers;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.FormBackedDialogPage;
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
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.EditPopulationCenterForm;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.NewGameForm;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.joverseer.domain.ProductEnum;

import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;


public class PopulationCenterViewer extends AbstractForm {
    public static final String FORM_PAGE = "PopulationCenterViewer";

    JTextField nation;
    JTextField sizeFort;
    HashMap production = new HashMap();
    HashMap stores = new HashMap();

    ActionCommand editPopulationCenterCommand = new EditPopulationCenterCommand();
    
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
        
        for (ProductEnum p : ProductEnum.values()) {
            JTextField tf = (JTextField)production.get(p);
            Integer amt = pc.getProduction(p);
            String amtStr;
            if (amt == null || amt == 0) {
                amtStr = "  -";
            } else {
                amtStr = String.valueOf(amt);
            }
            tf.setText(amtStr);

            tf = (JTextField)stores.get(p);
            amt = pc.getStores(p);
            if (amt == null || amt == 0) {
                amtStr = "  -";
            } else {
                amtStr = String.valueOf(amt);
            }
            tf.setText(amtStr);

        }
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

        glb.append(c = new JTextField());
        c.setBorder(null);
        bf.bindControl(c, "loyalty");
        glb.nextLine();
        
        Font f = GraphicUtils.getFont("Arial", Font.PLAIN, 9);
        
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
                new Object[] {editPopulationCenterCommand});
        return group.createPopupMenu();
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
    
}

