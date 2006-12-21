package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Combat;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.CombatNarrationForm;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.junit.runner.Description;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

import sun.security.action.GetLongAction;


public class CombatViewer extends AbstractForm {

    public static final String FORM_PAGE = "CombatViewer";
    
    JTextField description;
    
    ActionCommand showCombatAction;
    
    public CombatViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        glb.append(description = new JTextField());
        description.setPreferredSize(new Dimension(200, 12));
        description.setBorder(null);
        
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        JButton btnMenu = new JButton();
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        btnMenu.setPreferredSize(new Dimension(16, 16));
        btnMenu.setIcon(ico);
        glb.append(btnMenu);
        btnMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                return createCombatPopupContextMenu();
            }
        });

        glb.nextLine();
        
        JPanel p = glb.getPanel();
        p.setBackground(Color.white);
        return p;
    }
    
    private JPopupMenu createCombatPopupContextMenu() {
        Combat c = (Combat)getFormObject();
        Object[] narrationActions =  new Object[c.getNarrations().size()];
        int i = 0;
        for (Integer nationNo : c.getNarrations().keySet()) {
            narrationActions[i] = new ShowCombatAction(nationNo);
        }
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "combatCommandGroup", narrationActions);
        return group.createPopupMenu();
    }
    
    public void setFormObject(Object obj) {
        super.setFormObject(obj);
        Combat c = (Combat)obj;
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();

        String d = "";
        for (Integer nationNo : c.getNarrations().keySet()) {
            Nation n = game.getMetadata().getNationByNum(nationNo);
            d += (!d.equals("") ? ", " : "") + n.getName();
        }
        description.setText(d);
    }
    
    private class ShowCombatAction extends ActionCommand {
        int nationNo;
        public ShowCombatAction(int nationNo) {
            super("showCombatAction");
            this.nationNo = nationNo;
            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Nation n = game.getMetadata().getNationByNum(nationNo);
            setLabel(n.getName() + " Narration");
        }

        protected void doExecuteCommand() {
            Combat c = (org.joverseer.domain.Combat) getFormObject();
            final String descr = c.getNarrationForNation(nationNo);
            FormModel formModel = FormModelHelper.createFormModel(descr);
            final CombatNarrationForm form = new CombatNarrationForm(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(form);
            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    form.setFormObject(descr);
                }

                protected boolean onFinish() {
                    return true;
                }

            };
            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Nation n = game.getMetadata().getNationByNum(nationNo);
            
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("combatNarrationDialog.title", new Object[]{String.valueOf(c.getHexNo()), String.valueOf(n.getName())}, Locale.getDefault()));
            dialog.showDialog();
        }
        
    }
}
