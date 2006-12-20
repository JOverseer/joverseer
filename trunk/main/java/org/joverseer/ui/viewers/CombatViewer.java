package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

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
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.junit.runner.Description;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class CombatViewer extends AbstractForm {

    public static final String FORM_PAGE = "CombatViewer";
    
    JTextField description;
    
    ActionCommand showCombatAction = new ShowCombatAction();
    
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
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "combatCommandGroup", new Object[] {showCombatAction});
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
        public ShowCombatAction() {
            super("showCombatAction");
        }

        protected void doExecuteCommand() {
            Combat c = (org.joverseer.domain.Combat) getFormObject();
            String title = "Combat at " + c.getHexNo();
            String descr = (String)c.getNarrations().values().toArray()[0];
            descr = descr.replaceAll("\r\n\r\n", "\n");
            descr = descr.replaceAll("\r\n", "\n");
            MessageDialog dlg = new MessageDialog(title, descr);
            dlg.showDialog();
        }
    }
}
