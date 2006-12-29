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

import org.joverseer.domain.Encounter;
import org.joverseer.ui.NarrationForm;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class EncounterViewer extends AbstractForm {

    public static final String FORM_PAGE = "encounterViewer";
    
    JTextField description;
    
    ActionCommand showDescriptionCommand = new ShowDescriptionCommand();
    
    public EncounterViewer(FormModel formModel) {
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
                return createEncounterPopupContextMenu();
            }
        });

        glb.nextLine();
        
        JPanel p = glb.getPanel();
        p.setBackground(Color.white);
        return p;
    }
    
    private JPopupMenu createEncounterPopupContextMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "encounterCommandGroup", new Object[]{showDescriptionCommand});
        return group.createPopupMenu();
    }
    
    public void setFormObject(Object obj) {
        super.setFormObject(obj);
        Encounter e = (Encounter)obj;
        String d = "Encounter: " + e.getCharacter();
        description.setText(d);
    }
    
    private class ShowDescriptionCommand extends ActionCommand {
        protected void doExecuteCommand() {
            Encounter e = (Encounter)getFormObject();
            final String descr = e.getDescription();
            FormModel formModel = FormModelHelper.createFormModel(descr);
            final NarrationForm form = new NarrationForm(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(form);
            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    form.setFormObject(descr);
                }

                protected boolean onFinish() {
                    return true;
                }

            };
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("encounterDialog.title", new Object[]{e.getCharacter(), String.valueOf(e.getHexNo())}, Locale.getDefault()));
            dialog.showDialog();
        }
        
    }
}
