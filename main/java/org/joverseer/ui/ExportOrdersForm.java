package org.joverseer.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.export.OrderFileGenerator;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class ExportOrdersForm extends AbstractForm {
    JComboBox nation;
    JComboBox version;
    JTextArea orders;
    boolean ordersOk = false;
    
    public ExportOrdersForm(FormModel model) {
        super(model, "ExportOrdersForm");
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
        nation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                int nationNo = getSelectedNationNo();
                PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
                version.setSelectedItem(String.valueOf(pi.getTurnVersion()));
            }
            
        });
        
        glb.nextLine();
        
        glb.append(new JLabel("Version :"));
        glb.append(version = new JComboBox(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        version.setPreferredSize(new Dimension(20, 24));
        glb.nextLine();
        
        orders = new JTextArea();
        orders.setWrapStyleWord(false);
        orders.setLineWrap(false);
        orders.setPreferredSize(new Dimension(450, 700));
        JScrollPane scp = new JScrollPane(orders);
        scp.setPreferredSize(new Dimension(500, 400));
        glb.append(scp, 2, 1);
        
        glb.nextLine();
        JButton generate = new JButton("Generate");
        glb.append(generate, 1, 1);
        glb.nextLine();
        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrderFileGenerator gen = new OrderFileGenerator();
                Game g = GameHolder.instance().getGame();
                try {
                    orders.setText(gen.generateOrderFile(g, g.getTurn(), getSelectedNationNo()));
                    orders.setCaretPosition(0);
                    ordersOk = true;
                }
                catch (Exception exc) {
                    orders.setText(exc.getMessage());
                    ordersOk = false;
                }
            }
        });
        JButton save = new JButton("Save");
        glb.append(save, 1, 1);
        final AbstractForm form = this;
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ordersOk) return;
                Game g = GameHolder.instance().getGame();
                int nationNo = getSelectedNationNo();
                PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
                String fname = String.format("me%02dv%s.%03d", getSelectedNationNo(), version.getSelectedItem(), g.getMetadata().getGameNo());
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                fileChooser.setApproveButtonText("Save");
                fileChooser.setSelectedFile(new File(fname));
                if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        pi.setTurnVersion(Integer.parseInt(version.getSelectedItem().toString()) + 1);
                        FileWriter f = new FileWriter(fileChooser.getSelectedFile());
                        f.write(orders.getText());
                        f.close();
                        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                        MessageDialog md = new MessageDialog("Turn Exported", "The turn was succesfully exported to file " + fileChooser.getSelectedFile() + ".");
                        md.showDialog();
                    }
                    catch (Exception exc) {
                        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                        MessageDialog md = new MessageDialog(
                                ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                                exc.getMessage());
                        md.showDialog();
                    }
                }
            }
        });
        
        nation.setSelectedIndex(0);
        
        return glb.getPanel();
    }
    
    
}
