package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Logger;
import org.joverseer.domain.Character;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.export.OrderFileGenerator;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.GamePreference;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.command.OpenGameDirTree;
import org.joverseer.ui.command.SaveGame;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.support.dialogs.InputDialog;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Export/Submit orders form
 * 
 * @author Marios Skounakis
 */
//TODO document better

public class ExportOrdersForm extends AbstractForm {
    static Logger logger = Logger.getLogger(ExportOrdersForm.class);
    public static int ORDERS_OK = 0;
    public static int ORDERS_NOT_OK = 1;
    
    JComboBox nation;
    JComboBox version;
    JTextArea orders;
    boolean ordersOk = false;
    boolean cancelExport = false;
    Integer oldSelectedNation = null;
    int orderCheckResult = 0;
    
    boolean uncheckedOrders = false;
    boolean ordersWithErrors = false;
    boolean ordersWithWarnings = false;
    boolean missingOrders = false;
    boolean duplicateSkillOrders = false;
    
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
    	Game g = GameHolder.instance().getGame();
    	
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
                if (oldSelectedNation == null || oldSelectedNation != nationNo) {
                    orders.setText("");
                    ordersOk = false;
                    oldSelectedNation = nationNo;
                }
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
        glb.append(scp, 3, 1);
        
        glb.nextLine();
        JButton generate = new JButton("Generate");
        generate.setPreferredSize(new Dimension(100, 20));
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
                	orders.setText("Unexpected error generating order file.");
                    ordersOk = false;
                    logger.error(exc);
                }
            }
        });
        JButton save = new JButton("Save");
        save.setPreferredSize(new Dimension(100, 20));
        glb.append(save, 1, 1);
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAndSendOrders(false);
            }
        });
        
        JButton send = new JButton("Send");
        send.setPreferredSize(new Dimension(100, 20));
        glb.append(send, 1, 1);
        glb.append(new JLabel(), 1, 1);
        
        send.setVisible(true);
        
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	saveAndSendOrders(true);
            }
        });
                
        nation.setSelectedIndex(0);
    	nation.setSelectedItem(g.getMetadata().getNationByNum(g.getMetadata().getNationNo()).getName());
        
        return glb.getPanel();
    }
    
    private void increaseVersionNumber(PlayerInfo pi) {
    	pi.setTurnVersion(pi.getTurnVersion() + 1);
    	nation.setSelectedIndex(nation.getSelectedIndex());
    }
    
    private void saveAndSendOrders(boolean send) {
    	if (!ordersOk) return;
        if (!checkOrderValidity()) return;
        Game g = GameHolder.instance().getGame();
        int nationNo = getSelectedNationNo();
        PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
        pi.setTurnVersion(Integer.parseInt(version.getSelectedItem().toString()));
        String fname = String.format("me%02dv%s.%03d", getSelectedNationNo(), version.getSelectedItem(), g.getMetadata().getGameNo());
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Save");
        fileChooser.setSelectedFile(new File(fname));
        
        String orderPathPref = PreferenceRegistry.instance().getPreferenceValue("submitOrders.defaultFolder");
        String lastDir = "";
        if ("importDir".equals(orderPathPref)) {
        	lastDir = GamePreference.getValueForPreference("importDir", OpenGameDirTree.class);
        } else {
        	lastDir = GamePreference.getValueForPreference("orderDir", ExportOrdersForm.class);
        }
        if (lastDir == null) {
            lastDir = GamePreference.getValueForPreference("importDir", OpenGameDirTree.class);
        }
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if ("importDir".equals(orderPathPref)) {
                	GamePreference.setValueForPreference("orderDir", file.getParent(), ExportOrdersForm.class);
                }
                FileWriter f = new FileWriter(file);
                String txt = orders.getText();
                txt = txt.replace("\n", System.getProperty("line.separator"));
                f.write(txt);
                f.close();
                pi.setLastOrderFile(file.getAbsolutePath());
                if (!send) {
                	increaseVersionNumber(pi);
                }
                if (send) {
                    String prefMethod = PreferenceRegistry.instance().getPreferenceValue("submitOrders.method");
                    if (prefMethod.equals("email")) {
                        // send by email
                        String recipientEmail = PreferenceRegistry.instance().getPreferenceValue("submitOrders.recipientEmail");
                        String cmd = "bin\\mailSender\\MailSender.exe " + recipientEmail + " " + fname + " " + file.getCanonicalPath();
                        logger.debug("Starting mail client with command " + cmd);
                        Runtime.getRuntime().exec(cmd);
                        increaseVersionNumber(pi);

                        String msg = "Orders sent to Middle Earth Games (" + recipientEmail + ") by email and saved to file " + fileChooser.getSelectedFile() + ".\nYou should save your game now (to update the order version counter).";
                        MessageDialog md = new MessageDialog("Turn Submitted", msg);
                        md.showDialog();
                        pi.setOrdersSentOn(new Date());
                        autoSaveGameAccordingToPref();
                    } else {
                        // submit to meturn.com
                        Preferences prefs = Preferences.userNodeForPackage(ExportOrdersForm.class);
                        String email = prefs.get("useremail", "");
                        String emailRegex = "^(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alnum}@(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alpha}$";
                        InputDialog idlg = new InputDialog();
                        idlg.setTitle("Send turn");
                        idlg.init("Enter the email address where you want the confirmation email to be sent.");
                        JTextField emailText = new JTextField();
                        idlg.addComponent("Email address :", emailText);
                        idlg.setPreferredSize(new Dimension(400, 80));
                        emailText.setText(email);
                        do {
                            idlg.showDialog();
                            if (!idlg.getResult()) {
                                ErrorDialog md = new ErrorDialog("Send aborted.");
                                md.showDialog();
                                return;
                            }
                            email = emailText.getText();
                        } while (!Pattern.matches(emailRegex, email));
                        prefs.put("useremail", email);
                        
                        String name = pi.getPlayerName();
                        String acct = pi.getAccountNo();
                        
                        String url = "http://www.meturn.com/cgi-bin/HUpload.exe";
                        final PostMethod filePost = new PostMethod(url);
                        Part[] parts = {
                                new StringPart("emailaddr", email),
                                new StringPart("name", name),
                                new StringPart("account", acct),
                                new FilePart(file.getName(), file)
                            };
                        filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
                        //final GetMethod filePost = new GetMethod("http://www.meturn.com/");
                        HttpClient client = new HttpClient();
                        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                        int status = client.executeMethod(filePost);
                        String msg = "";
                        if (status == HttpStatus.SC_OK) {
                            final SubmitOrdersResultsForm frm = new SubmitOrdersResultsForm(FormModelHelper.createFormModel(new Object()));
                            FormBackedDialogPage page = new FormBackedDialogPage(frm);
    
                            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                                protected void onAboutToShow() {
                                	try {
                                		((HTMLDocument)frm.getJEditorPane().getDocument()).setBase(new URL("http://www.meturn.com/"));
                                		frm.getJEditorPane().getEditorKit().read(filePost.getResponseBodyAsStream(), frm.getJEditorPane().getDocument(), 0);
                                	}
                                	catch (Exception exc) {
                                            logger.error(exc);
                                	}
                                }
    
                                protected boolean onFinish() {
                                    return true;
                                }
                                
                                protected Object[] getCommandGroupMembers() {
                                    return new AbstractCommand[] {
                                            getFinishCommand()
                                    };
                                }
                            };
                            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                            dialog.setTitle(ms.getMessage("submitOrdersDialog.title", new Object[]{}, Locale.getDefault()));
                            dialog.showDialog();
                            
                            increaseVersionNumber(pi);
                            filePost.releaseConnection();
                            
                            msg = "Orders sent to Middle Earth Games and saved to file " + fileChooser.getSelectedFile() + ".\nYou should save your game now (to update the order version counter).";
                        	MessageDialog md = new MessageDialog("Turn Submitted", msg);
                        	md.showDialog();
                            pi.setOrdersSentOn(new Date());
                            autoSaveGameAccordingToPref();
                        } else {
                        	send = false;
                        }
                    }
                } else {
                	MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                	MessageDialog md = new MessageDialog("Turn Exported", "The turn was succesfully exported to file " + fileChooser.getSelectedFile() + ".");
                	md.showDialog();
                	autoSaveGameAccordingToPref();
                } 
            }
            catch (Exception exc) {
                logger.error(exc);
                MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                MessageDialog md = new MessageDialog(
                        ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                        exc.getMessage());
                md.showDialog();
            }
        }
    }
    
    private int validateOrders() {
        Game g = GameHolder.instance().getGame();
        ArrayList<Character> chars = (ArrayList<Character>)g.getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", getSelectedNationNo());
        Collections.sort(chars, new BeanComparator("id"));
        ArrayList<Character> toRemove = new ArrayList<Character>();
        for (Character ch : chars) {
            if (ch.getHealth() == null || ch.getHealth() == 0 || ch.getHexNo() <= 0) {
                toRemove.add(ch);
            }
        }
        chars.removeAll(toRemove);
        
        missingOrders = false;
        ordersWithErrors = false;
        uncheckedOrders = false;
        duplicateSkillOrders = false;
        ordersWithWarnings = false;
        
        OrderResultContainer orc = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
        OrderParameterValidator validator = new OrderParameterValidator();
        for (Character ch : chars) {
            for (int i=0; i<ch.getNumberOfOrders(); i++) {
                if (ch.getOrders()[i].isBlank()) {
                    missingOrders = true;
                } else {
                    if (orc.getResultsForOrder(ch.getOrders()[i]).size() == 0) {
                        uncheckedOrders = true;
                    } else {
                        if (orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Error){
                            ordersWithErrors = true;
                        } else if (orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Warning) {
                        	ordersWithWarnings = true;
                        }
                    }
                    OrderValidationResult ovr = validator.checkForDuplicateSkillOrder(ch.getOrders()[i]);
                    if (ovr != null) {
                    	duplicateSkillOrders = true;
                    }
                    ovr = validator.checkOrder(ch.getOrders()[i]);
                    if (ovr != null && ovr.getLevel() == OrderValidationResult.ERROR) {
                    	ordersWithErrors = true;
                    }
                    for (int j=0; j<=ch.getOrders()[i].getLastParamIndex(); j++) {
                    	ovr = validator.checkParam(ch.getOrders()[i], j);
                        if (ovr != null && ovr.getLevel() == OrderValidationResult.ERROR) {
                        	ordersWithErrors = true;
                        }	
                    }
                }
            }
        }
        
        if (missingOrders || uncheckedOrders || ordersWithErrors || duplicateSkillOrders || ordersWithWarnings) return ORDERS_NOT_OK;
        return ORDERS_OK;
    }
    
    private boolean checkOrderValidity() {
        cancelExport = false;
        if (orderCheckResult != ORDERS_OK) {
            if (missingOrders) {
                MessageDialog dlg = new MessageDialog("Error", "Some characters are missing orders. Cannot export.");
                dlg.showDialog();
                return false;
            }
            if (duplicateSkillOrders) {
            	MessageDialog dlg = new MessageDialog("Error", "Some characters are trying to issue duplicate skill orders. Cannot export.");
                dlg.showDialog();
                return false;
            }
            if (ordersWithErrors) {
            	String strictErrorHandling = PreferenceRegistry.instance().getPreferenceValue("submitOrders.strictErrorHandling");
            	if ("yes".equals(strictErrorHandling)) {
            		MessageDialog dlg = new MessageDialog("Error", "Some orders have errors. Cannot export.");
            		dlg.showDialog();
            		return false;
            	}
                cancelExport = false;
                ConfirmationDialog dlg = new ConfirmationDialog("Warning", "Some orders have errors ! There is a good chance your orders won't be processed correctly! Are you absolutely sure you want to export your orders?") {
                    protected void onCancel() {
                        super.onCancel();
                        cancelExport = true;
                    }
                    
                    protected void onConfirm() {
                    }
                    
                };
                dlg.showDialog();
                if (cancelExport) return false;
            } else if (ordersWithWarnings) {
            	cancelExport = false;
                ConfirmationDialog dlg = new ConfirmationDialog("Warning", "Some orders have warnings! Are you sure you want to export your orders?") {
                    protected void onCancel() {
                        super.onCancel();
                        cancelExport = true;
                    }
                    
                    protected void onConfirm() {
                    }
                    
                };
                dlg.showDialog();
                if (cancelExport) return false;
            	
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
                if (cancelExport) return false;
            }
        }
        return true;

    }
    
    private void autoSaveGameAccordingToPref() {
    	String pval = PreferenceRegistry.instance().getPreferenceValue("submitOrders.autoSave");
    	if (pval.equals("yes")) {
    		new SaveGame().execute();
    	}
    }
    
}
