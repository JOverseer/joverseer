package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.beanutils.BeanComparator;
import org.joverseer.domain.Character;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.export.OrderFileGenerator;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class ExportOrdersForm extends AbstractForm {
    public static int ORDERS_OK = 0;
    public static int ORDERS_NOT_OK = 1;
    
    JComboBox nation;
    JComboBox version;
    JTextArea orders;
    boolean ordersOk = false;
    boolean cancelExport = false;
    
    int orderCheckResult = 0;
    
    boolean uncheckedOrders = false;
    boolean ordersWithErrors = false;
    boolean missingOrders = false;
    
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
                    orderCheckResult = validateOrders();
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
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ordersOk) return;
                cancelExport = false;
                if (orderCheckResult != ORDERS_OK) {
                    if (missingOrders) {
                        MessageDialog dlg = new MessageDialog("Error", "Some characters are missing orders. Cannot export.");
                        dlg.showDialog();
                        return;
                    }
                    if (ordersWithErrors) {
                        cancelExport = false;
                        ConfirmationDialog dlg = new ConfirmationDialog("Warning", "Some orders have been checked with Orderchecker and have errors. Continue with export?") {
                            protected void onCancel() {
                                super.onCancel();
                                cancelExport = true;
                            }
                            
                            protected void onConfirm() {
                            }
                            
                        };
                        dlg.showDialog();
                        if (cancelExport) return;
                    }
                    if (uncheckedOrders) {
                        ConfirmationDialog dlg = new ConfirmationDialog("Warning", "Some orders have not been checked with Orderchecker. Continue with export?") {
                            protected void onCancel() {
                                super.onCancel();
                                cancelExport = true;
                            }

                            protected void onConfirm() {
                            }
                        };
                        dlg.showDialog();
                        if (cancelExport) return;
                    }
                }
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
    
    private int validateOrders() {
        Game g = GameHolder.instance().getGame();
        ArrayList<Character> chars = (ArrayList<Character>)g.getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", getSelectedNationNo());
        Collections.sort(chars, new BeanComparator("id"));
        ArrayList<Character> toRemove = new ArrayList<Character>();
        for (Character ch : chars) {
            if (ch.getHealth() == null || ch.getHealth() == 0) {
                toRemove.add(ch);
            }
        }
        chars.removeAll(toRemove);
        
        missingOrders = false;
        ordersWithErrors = false;
        uncheckedOrders = false;
        
        OrderResultContainer orc = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
        
        for (Character ch : chars) {
            for (int i=0; i<2; i++) {
                if (ch.getOrders()[i].isBlank()) {
                    missingOrders = true;
                } else {
                    if (orc.getResultsForOrder(ch.getOrders()[i]).size() == 0) {
                        uncheckedOrders = true;
                    } else {
                        if (orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Error ||
                                orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Warning) {
                            ordersWithErrors = true;
                        }
                    }
                }
            }
        }
        
        if (missingOrders || uncheckedOrders || ordersWithErrors) return ORDERS_NOT_OK;
        return ORDERS_OK;
    }
    
}
