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
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.orders.export.OrderFileGenerator;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.GamePreference;
import org.joverseer.support.PropertyComparator;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.command.OpenGameDirTree;
import org.joverseer.ui.command.SaveGame;
import org.joverseer.ui.support.controls.ResourceButton;
import org.joverseer.ui.support.controls.ResourceLabel;
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
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Export/Submit orders form
 * 
 * @author Marios Skounakis
 */
// TODO document better
public class ExportOrdersForm extends ScalableAbstractForm {
	@SuppressWarnings("hiding")
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

	private ArrayList<String> getNationItems() {
		Game g = GameHolder.instance().getGame();
		ArrayList<String> ret = new ArrayList<String>();
		for (PlayerInfo pi : g.getTurn().getPlayerInfo()) {
			ret.add(g.getMetadata().getNationByNum(pi.getNationNo()).getName());
		}
		return ret;
	}

	private int getSelectedNationNo() {
		String nationName = this.nation.getSelectedItem().toString();
		Game g = GameHolder.instance().getGame();
		return g.getMetadata().getNationByName(nationName).getNumber();
	}

	@Override
	protected JComponent createFormControl() {
		Game g = GameHolder.instance().getGame();

		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.append(new ResourceLabel("standardFields.Nation"));
		glb.append(this.nation = new JComboBox(getNationItems().toArray()));

		this.nation.setPreferredSize(this.uiSizes.newDimension(100/24, this.uiSizes.getHeight6()));
		this.nation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Game g1 = GameHolder.instance().getGame();
				int nationNo = getSelectedNationNo();
				PlayerInfo pi = (PlayerInfo) g1.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
				ExportOrdersForm.this.version.setSelectedItem(String.valueOf(pi.getTurnVersion()));
				if (ExportOrdersForm.this.oldSelectedNation == null || ExportOrdersForm.this.oldSelectedNation != nationNo) {
					ExportOrdersForm.this.orders.setText("");
					ExportOrdersForm.this.ordersOk = false;
					ExportOrdersForm.this.oldSelectedNation = nationNo;
				}
			}

		});

		glb.nextLine();

		glb.append(new ResourceLabel("ExportOrdersForm.Version"));
		glb.append(this.version = new JComboBox(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
		this.version.setPreferredSize(this.uiSizes.newDimension(20/24, this.uiSizes.getHeight6()));
		glb.nextLine();

		this.orders = new JTextArea();
		this.orders.setWrapStyleWord(false);
		this.orders.setLineWrap(false);
		this.orders.setEditable(false);
		JScrollPane scp = new JScrollPane(this.orders);
		scp.setPreferredSize(new Dimension(500, 400));
		glb.append(scp, 3, 1);

		glb.nextLine();
		JButton generate = new ResourceButton("ExportOrdersForm.BtnGenerate");
		generate.setPreferredSize(this.uiSizes.newDimension(100/20, this.uiSizes.getHeight5()));
		glb.append(generate, 1, 1);
		glb.nextLine();
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OrderFileGenerator gen = new OrderFileGenerator();
				Game g1 = GameHolder.instance().getGame();
				try {
					ExportOrdersForm.this.orders.setText(gen.generateOrderFile(g1, g1.getTurn(), getSelectedNationNo()));
					ExportOrdersForm.this.orders.setCaretPosition(0);
					ExportOrdersForm.this.orderCheckResult = validateOrders();
					ExportOrdersForm.this.ordersOk = true;
				} catch (Exception exc) {
					ExportOrdersForm.this.orders.setText(Application.instance().getApplicationContext().getMessage("ExportOrdersForm.error.UnexpectedError", null, null));
					ExportOrdersForm.this.ordersOk = false;
					logger.error(exc);
				}
			}
		});
		JButton save = new ResourceButton("standardActions.Save");
		save.setPreferredSize(this.uiSizes.newDimension(100/20, this.uiSizes.getHeight5()));
		glb.append(save, 1, 1);

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndSendOrders(false);
			}
		});

		JButton send = new ResourceButton("ExportOrdersForm.BtnSend");
		send.setPreferredSize(this.uiSizes.newDimension(100/20, this.uiSizes.getHeight5()));
		glb.append(send, 1, 1);
		glb.append(new JLabel(), 1, 1);

		send.setVisible(true);

		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndSendOrders(true);
			}
		});

		this.nation.setSelectedIndex(0);
		this.nation.setSelectedItem(g.getMetadata().getNationByNum(g.getMetadata().getNationNo()).getName());

		return glb.getPanel();
	}

	private void increaseVersionNumber(PlayerInfo pi) {
		pi.setTurnVersion(pi.getTurnVersion() + 1);
		this.nation.setSelectedIndex(this.nation.getSelectedIndex());
	}

	private void saveAndSendOrders(boolean send) {
		if (!this.ordersOk)
			return;
		if (!checkOrderValidity())
			return;
		Game g = GameHolder.instance().getGame();
		int nationNo = getSelectedNationNo();
		PlayerInfo pi = (PlayerInfo) g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
		pi.setTurnVersion(Integer.parseInt(this.version.getSelectedItem().toString()));
		String fname = String.format("me%02dv%s.%03d", getSelectedNationNo(), this.version.getSelectedItem(), g.getMetadata().getGameNo());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setApproveButtonText(Application.instance().getApplicationContext().getMessage("standardActions.Save", null, null));
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
				String txt = this.orders.getText();
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

						String msg = Application.instance().getApplicationContext().getMessage("ExportOrdersForm.OrdersSentByMailSuccessMessage", new Object[] { recipientEmail, fileChooser.getSelectedFile() }, null);
						MessageDialog md = new MessageDialog(Application.instance().getApplicationContext().getMessage("ExportOrdersForm.TurnSubmittedDialogTitle", null, null), msg);
						md.showDialog();
						pi.setOrdersSentOn(new Date());
						autoSaveGameAccordingToPref();
					} else {
						// submit to meturn.com
						Preferences prefs = Preferences.userNodeForPackage(ExportOrdersForm.class);
						String email = prefs.get("useremail", "");
						String emailRegex = "^(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alnum}@(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alpha}$";
						InputDialog idlg = new InputDialog();
						idlg.setTitle(Application.instance().getApplicationContext().getMessage("ExportOrdersForm.SendTurnInputDialogTitle", null, null));
						idlg.init(Application.instance().getApplicationContext().getMessage("ExportOrdersForm.SendTurnInputDialogMessage", null, null));
						JTextField emailText = new JTextField();
						idlg.addComponent(Application.instance().getApplicationContext().getMessage("ExportOrdersForm.SendTurnInputDialog.EmailAddress", null, null), emailText);
						idlg.setPreferredSize(new Dimension(400, 80));
						emailText.setText(email);
						do {
							idlg.showDialog();
							if (!idlg.getResult()) {
								ErrorDialog md = new ErrorDialog("ExportOrdersForm.SendAbortedMessage");
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
						Part[] parts = { new StringPart("emailaddr", email), new StringPart("name", name), new StringPart("account", acct), new FilePart(file.getName(), file) };
						filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
						// final GetMethod filePost = new
						// GetMethod("http://www.meturn.com/");
						HttpClient client = new HttpClient();
						client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
						int status = client.executeMethod(filePost);
						String msg = "";
						if (status == HttpStatus.SC_OK) {
							final SubmitOrdersResultsForm frm = new SubmitOrdersResultsForm(FormModelHelper.createFormModel(new Object()));
							FormBackedDialogPage page = new FormBackedDialogPage(frm);

							TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
								@Override
								protected void onAboutToShow() {
									try {
										((HTMLDocument) frm.getJEditorPane().getDocument()).setBase(new URL("http://www.meturn.com/"));
										frm.getJEditorPane().getEditorKit().read(filePost.getResponseBodyAsStream(), frm.getJEditorPane().getDocument(), 0);
									} catch (Exception exc) {
										this.logger.error(exc);
									}
								}

								@Override
								protected boolean onFinish() {
									return true;
								}

								@Override
								protected Object[] getCommandGroupMembers() {
									return new AbstractCommand[] { getFinishCommand() };
								}
							};
							MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
							dialog.setTitle(ms.getMessage("submitOrdersDialog.title", new Object[] {}, Locale.getDefault()));
							dialog.showDialog();

							increaseVersionNumber(pi);
							filePost.releaseConnection();

							msg = Application.instance().getApplicationContext().getMessage("ExportOrdersForm.OrdersSentByMETURNSuccessMessage", new Object[] { fileChooser.getSelectedFile() }, null);
							MessageDialog md = new MessageDialog(ms.getMessage("ExportOrdersForm.TurnSubmittedDialogTitle", null, null), msg);
							md.showDialog();
							pi.setOrdersSentOn(new Date());
							autoSaveGameAccordingToPref();
						} else {
							send = false;
						}
					}
				} else {
					MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
					MessageDialog md = new MessageDialog(ms.getMessage("ExportOrdersForm.TurnExportedDialogTitle", null, null), ms.getMessage("ExportOrdersForm.TurnExportedDialogMessage", new Object[] { fileChooser.getSelectedFile() }, null));
					md.showDialog();
					autoSaveGameAccordingToPref();
				}
			} catch (Exception exc) {
				logger.error(exc);
				ErrorDialog md = new ErrorDialog(exc.getMessage());
				md.showDialog();
			}
		}
	}

	private int validateOrders() {
		Game g = GameHolder.instance().getGame();
		ArrayList<Character> chars = g.getTurn().getCharacters().findAllByProperty("nationNo", getSelectedNationNo());
		Collections.sort(chars, new PropertyComparator<Character>("id"));
		ArrayList<Character> toRemove = new ArrayList<Character>();
		for (Character ch : chars) {
			if (ch.getHealth() == null || ch.getHealth() == 0 || ch.getHexNo() <= 0) {
				toRemove.add(ch);
			}
		}
		chars.removeAll(toRemove);

		this.missingOrders = false;
		this.ordersWithErrors = false;
		this.uncheckedOrders = false;
		this.duplicateSkillOrders = false;
		this.ordersWithWarnings = false;

		OrderResultContainer orc = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");
		OrderParameterValidator validator = new OrderParameterValidator();
		for (Character ch : chars) {
			for (int i = 0; i < ch.getNumberOfOrders(); i++) {
				if (ch.getOrders()[i].isBlank()) {
					this.missingOrders = true;
				} else {
					if (orc.getResultsForOrder(ch.getOrders()[i]).size() == 0) {
						if (!g.getMetadata().getGameType().equals(GameTypeEnum.gameKS)) {
							this.uncheckedOrders = true;
						}
					} else {
						if (orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Error) {
							this.ordersWithErrors = true;
						} else if (orc.getResultTypeForOrder(ch.getOrders()[i]) == OrderResultTypeEnum.Warning) {
							this.ordersWithWarnings = true;
						}
					}
					OrderValidationResult ovr = validator.checkForDuplicateSkillOrder(ch.getOrders()[i]);
					if (ovr != null) {
						this.duplicateSkillOrders = true;
					}
					ovr = validator.checkOrder(ch.getOrders()[i]);
					if (ovr != null && ovr.getLevel() == OrderValidationResult.ERROR) {
						this.ordersWithErrors = true;
					}
					for (int j = 0; j <= ch.getOrders()[i].getLastParamIndex(); j++) {
						ovr = validator.checkParam(ch.getOrders()[i], j);
						if (ovr != null && ovr.getLevel() == OrderValidationResult.ERROR) {
							this.ordersWithErrors = true;
						}
					}
				}
			}
		}

		if (this.missingOrders || this.uncheckedOrders || this.ordersWithErrors || this.duplicateSkillOrders || this.ordersWithWarnings)
			return ORDERS_NOT_OK;
		return ORDERS_OK;
	}

	private boolean checkOrderValidity() {
		this.cancelExport = false;
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		if (this.orderCheckResult != ORDERS_OK) {
			if (this.missingOrders) {
				MessageDialog dlg = new MessageDialog(ms.getMessage("standardMessages.Error", null, null), ms.getMessage("ExportOrdersForm.error.CharactersMissingOrders", null, null));
				dlg.showDialog();
				return false;
			}
			if (this.duplicateSkillOrders) {
				MessageDialog dlg = new MessageDialog(ms.getMessage("standardMessages.Error", null, null), ms.getMessage("ExportOrdersForm.error.CharactersIssuingDuplicateSkillOrders", null, null));
				dlg.showDialog();
				return false;
			}
			if (this.ordersWithErrors) {

				this.cancelExport = false;
				ConfirmationDialog dlg = new ConfirmationDialog(ms.getMessage("standardMessages.Warning", null, null), ms.getMessage("ExportOrdersForm.warning.OrdersWithErrors", null, null)) {
					@Override
					protected void onCancel() {
						super.onCancel();
						ExportOrdersForm.this.cancelExport = true;
					}

					@Override
					protected void onConfirm() {
					}

				};
				dlg.showDialog();
				if (this.cancelExport)
					return false;
			} else if (this.ordersWithWarnings) {
				this.cancelExport = false;
				ConfirmationDialog dlg = new ConfirmationDialog(ms.getMessage("standardMessages.Warning", null, null), ms.getMessage("ExportOrdersForm.warning.OrdersWithWarnings", null, null)) {
					@Override
					protected void onCancel() {
						super.onCancel();
						ExportOrdersForm.this.cancelExport = true;
					}

					@Override
					protected void onConfirm() {
					}

				};
				dlg.showDialog();
				if (this.cancelExport)
					return false;

			}
			if (this.uncheckedOrders) {
				ConfirmationDialog dlg = new ConfirmationDialog(ms.getMessage("standardMessages.Warning", null, null), ms.getMessage("ExportOrdersForm.warning.OrdersNotCheckedWithOC", null, null)) {
					@Override
					protected void onCancel() {
						super.onCancel();
						ExportOrdersForm.this.cancelExport = true;
					}

					@Override
					protected void onConfirm() {
					}
				};
				dlg.showDialog();
				if (this.cancelExport)
					return false;
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
