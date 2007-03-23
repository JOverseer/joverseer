package org.joverseer.ui.command;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.export.OrderFileGenerator;
import org.joverseer.orders.export.OrderTextGenerator;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class ExportOrderTextCommand extends ActionCommand {
    public ExportOrderTextCommand() {
        super("exportOrderTextCommand");
    }

    protected void doExecuteCommand() {
        final ExportOrderTextForm form = new ExportOrderTextForm(FormModelHelper.createFormModel(new String()));
        FormBackedDialogPage pg = new FormBackedDialogPage(form);
        TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
            protected Object[] getCommandGroupMembers() {
                return new Object[]{getFinishCommand()};
            }

            protected boolean onFinish() {
                return true;
            }
        };
        
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dlg.setTitle(ms.getMessage("exportOrderTextDialog.title", new Object[]{}, Locale.getDefault()));
        dlg.showDialog();
        
    }
    
    class ExportOrderTextForm extends AbstractForm {
        JComboBox nation;
        JTextArea orders;

        private ExportOrderTextForm(FormModel arg0) {
            super(arg0, "exportOrderTextForm");
        }
        
        private ArrayList getNationItems() {
            Game g = GameHolder.instance().getGame();
            ArrayList<PlayerInfo> pis = (ArrayList<PlayerInfo>)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).getItems();
            ArrayList ret = new ArrayList();
            for (PlayerInfo pi : pis) {
                ret.add(g.getMetadata().getNationByNum(pi.getNationNo()).getName());
            }
            return ret;
        }
        
        private int getSelectedNationNo() {
            String nationName = nation.getSelectedItem().toString();
            Game g = GameHolder.instance().getGame();
            return g.getMetadata().getNationByName(nationName).getNumber();
        }
        
        protected JComponent createFormControl() {
            GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
            glb.append(new JLabel("Nation :"));
            glb.append(nation = new JComboBox(getNationItems().toArray()));
            nation.setPreferredSize(new Dimension(100, 24));
            glb.nextLine();
            
            orders = new JTextArea();
            orders.setWrapStyleWord(false);
            orders.setLineWrap(false);
            JScrollPane scp = new JScrollPane(orders);
            scp.setPreferredSize(new Dimension(500, 400));
            glb.append(scp, 2, 1);
            
            glb.nextLine();
            JButton generate = new JButton("Generate");
            glb.append(generate, 1, 1);
            glb.nextLine();
            generate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    OrderTextGenerator gen = new OrderTextGenerator();
                    Game g = GameHolder.instance().getGame();
                    try {
                        orders.setText(gen.generateOrderFile(g, g.getTurn(), getSelectedNationNo()));
                        orders.setCaretPosition(0);
                    }
                    catch (Exception exc) {
                        orders.setText(exc.getMessage());
                    }
                }
            });
            nation.setSelectedIndex(0);
            
            return glb.getPanel();
        }
        
    }

}
