/**
 * 
 */
package org.joverseer.ui.views;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.GamePreference;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.command.OpenGameDirTree;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.support.dialogs.InputDialog;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.progress.BusyIndicator;

/**
 * Export/Submit Diplo message
 * 
 * Mainly the same code as ExportOrderForm with modifications
 * @author Sam Terrett
 */
public class ExportDiploForm extends ScalableAbstractForm {
	JTextArea diploText;
	
	JLabel lbVersionV = new JLabel("");
	JLabel lbFileV = new JLabel("");
	JLabel lbSentV = new JLabel("");
	JLabel lbVersion = new JLabel();
	JLabel lbFile = new JLabel();
	JLabel lbSent = new JLabel();
	
	boolean sent;
	GameHolder gameHolder;

	public ExportDiploForm(FormModel arg0, GameHolder gameHolder) {
		super(arg0, "ExportDiploForm");
		this.gameHolder = gameHolder;
	}

	@Override
	protected JComponent createFormControl() {
		Game g = this.gameHolder.getGame();

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		this.diploText = new JTextArea();
		this.diploText.setWrapStyleWord(false);
		this.diploText.setLineWrap(false);
		this.diploText.setEditable(false);
		JScrollPane scp = new JScrollPane(this.diploText);
		scp.setPreferredSize(new Dimension(400, 350));
		panel.add(scp, BorderLayout.CENTER);
		
		
		try {
			this.diploText.setText((g.getTurn().getNationDiplo(g.getMetadata().getNationNo())).getMessage());
		} catch (NullPointerException e) {
			this.diploText.setText("NO DIPLO TEXT SAVED");
		}
		
		JPanel topPanel = new JPanel();
		
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel pnlVersion = new JPanel();
		topPanel.add(pnlVersion);
		pnlVersion.setLayout(new BoxLayout(pnlVersion, BoxLayout.Y_AXIS));

		this.lbVersion = new JLabel(Messages.getString("playerInfo.turnDiploVersion")); //$NON-NLS-1$
		pnlVersion.add(this.lbVersion);
		this.lbVersion.setBackground(Color.WHITE);
		pnlVersion.add(this.lbVersionV);
		this.lbVersionV.setHorizontalAlignment(SwingConstants.CENTER);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		topPanel.add(horizontalStrut);

		JPanel pnlSent = new JPanel();
		topPanel.add(pnlSent);
		pnlSent.setLayout(new BoxLayout(pnlSent, BoxLayout.Y_AXIS));

		this.lbSent = new JLabel(Messages.getString("playerInfo.ordersSentOn")); //$NON-NLS-1$
		pnlSent.add(this.lbSent);
		this.lbSent.setBackground(Color.WHITE);
		pnlSent.add(this.lbSentV);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		topPanel.add(horizontalStrut_1);

		JPanel pnlFile = new JPanel();
		topPanel.add(pnlFile);
		pnlFile.setLayout(new BoxLayout(pnlFile, BoxLayout.Y_AXIS));

		this.lbFile = new JLabel(Messages.getString("playerInfo.lastOrderFile")); //$NON-NLS-1$
		pnlFile.add(this.lbFile);
		this.lbFile.setBackground(Color.WHITE);
		pnlFile.add(this.lbFileV);

		setDiploPlayerInfo();
		panel.add(topPanel, BorderLayout.NORTH);
		
		return panel;
	}
	
	@Override
	public void commit() {
		Game g = this.gameHolder.getGame();
		int nationNo = g.getMetadata().getNationNo();
		PlayerInfo pi = g.getTurn().getPlayerInfo(nationNo);
		
		File file = saveDiploFile(g, nationNo, pi);
		if (file == null) return;
		
		String email = getEmailAddress();
		if (email == null) return;
		
		String name = pi.getPlayerName();
		if (name == null)
			name = "null";
		String acct = pi.getAccountNo();
		if (acct == null)
			acct = "null";
		
		try {
			sendDiploFile(email, name, acct, file);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	private String getEmailAddress() {
		Preferences prefs = Preferences.userNodeForPackage(ExportDiploForm.class);
		String email = prefs.get("useremail", "");
		String emailRegex = "^(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alnum}@(\\p{Alnum}+(\\.|\\_|\\-)?)*\\p{Alpha}$";
		InputDialog idlg = new InputDialog("ExportDiploForm.SendTurnInputDialogTitle");
		idlg.init(getMessage("ExportOrdersForm.SendTurnInputDialogMessage"));
		idlg.setTitlePaneTitle(getMessage("ExportOrdersForm.SendTurnInputDialogPaneTitle"));
		JTextField emailText = new JTextField();
		idlg.addComponent(getMessage("ExportOrdersForm.SendTurnInputDialog.EmailAddress"), emailText);
		idlg.setPreferredSize(new Dimension(400, 80));
		emailText.setText(email);
		
		do {
			idlg.showDialog();
			if (!idlg.getResult()) {
				ErrorDialog.showErrorDialog("ExportDiploForm.SendAbortedMessage");
				this.sent = false;
				return null;
			}
			email = emailText.getText();
		} while (!Pattern.matches(emailRegex, email));
		prefs.put("useremail", email);
		
		
		return email;
	}
	
	private File saveDiploFile(Game g, int nationNo, PlayerInfo pi) {
		String fname = String.format("me%02dv%ddiplo-turn%02d.%03dd", nationNo, pi.getDiploVersion(), g.getCurrentTurn() + 1, g.getMetadata().getGameNo());
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Game " + Integer.toString(g.getMetadata().getGameNo()) + "d", Integer.toString(g.getMetadata().getGameNo()) + "d"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setApproveButtonText(getMessage("standardActions.Save"));
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
		if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) != JFileChooser.APPROVE_OPTION) {
			this.sent = false;
			return null;
		}
		try {
			File file = fileChooser.getSelectedFile();
			if ("importDir".equals(orderPathPref)) {
				GamePreference.setValueForPreference("orderDir", file.getParent(), ExportOrdersForm.class);
			}
			FileWriter f = new FileWriter(file);
			String diploTxt = ((g.getTurn()).getNationDiplo(g.getMetadata().getNationNo())).getMessage();
			
			String nationsList[] = g.getTurn().getNationDiplo(nationNo).getNations();
			String nationsNoList[] = new String[nationsList.length];
			for (int i = 0; i < nationsList.length; i++) {
				nationsNoList[i] = Integer.toString(g.getMetadata().getNationByName(nationsList[i]).getNumber());
			}
			
			String txt = "Begindiplo;\n" + fname + ";\n" + pi.getSecret()  + ";\n\nFrom nations: " + String.join(", ", nationsNoList) + "\n\n" + diploTxt + "\n\n" + "Enddiplo";
			txt = txt.replace("\n", System.getProperty("line.separator"));
			f.write(txt);
			f.close();
			
			pi.setLastDiploFile(file.getAbsolutePath());
			pi.setDiploVersion(pi.getDiploVersion() + 1);
			return file;
		} catch (Exception exc) {
			ErrorDialog.showErrorDialog(exc);
			return null;
		}
	}
	
	private void sendDiploFile(String email, String name, String acct, File file) throws HttpException, IOException {
		String url = "http://www.meturn.com/cgi-bin/HUpload.exe";
		final PostMethod filePost = new PostMethod(url);
		Part[] parts = { new StringPart("emailaddr", email), new StringPart("name", name), new StringPart("account", acct), new FilePart(file.getName(), file) };
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK) {
			final SubmitOrdersResultsForm frm = new SubmitOrdersResultsForm(FormModelHelper.createFormModel(new Object()));
			FormBackedDialogPage page = new FormBackedDialogPage(frm);
			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {

				@Override
				protected void onAboutToShow() {
					try {
						((HTMLDocument) frm.getJEditorPane().getDocument()).setBase(new URI("http://www.meturn.com/").toURL());
						frm.getJEditorPane().getEditorKit().read(filePost.getResponseBodyAsStream(), frm.getJEditorPane().getDocument(), 0);
						this.setDescription(this.getMessage("ExportDiploForm.DiploSentByMETURNSuccessMessage", new Object[] { file }));
						this.setTitlePaneTitle(Messages.getString("submitDiploResultsForm.title"));
						BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
					} catch (Exception exc) {
						ExportDiploForm.this.sent = false;
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
			dialog.setTitle(Messages.getString("submitDiploDialog.title"));
			BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
			dialog.showDialog();
			if (!this.sent) {
				Game g = this.gameHolder.getGame();
				int nationNo = this.gameHolder.getGame().getMetadata().getNationNo();
				PlayerInfo pi = g.getTurn().getPlayerInfo(nationNo);
				filePost.releaseConnection();
				pi.setDiploSentOn(new Date());
				this.sent = true;
				pi.setDiploSent(true);
				//autoSaveGameAccordingToPref();
			}
		} else {
			this.sent=false;
			filePost.releaseConnection();
		}
	}

	private void setDiploPlayerInfo() {
		Game g = this.gameHolder.getGame();
		if (g!=null) {
			int nationNo = g.getMetadata().getNationNo();
			PlayerInfo pi = g.getTurn().getPlayerInfo(nationNo);
            Date d = pi.getDiploSentOn();
            if (d == null) {
    			this.lbVersionV.setText(String.valueOf(pi.getDiploVersion()));
            	this.lbSentV.setText("");
            	this.lbFileV.setText("");
        		this.lbFileV.setVisible(false);
                this.lbSentV.setVisible(false);
        		this.lbFile.setVisible(false);
                this.lbSent.setVisible(false);
            } else {
            	this.lbSentV.setText(new SimpleDateFormat().format(d));
    			this.lbVersionV.setText(String.valueOf(pi.getDiploVersion()));
    			String file = String.valueOf(pi.getLastDiploFile());
    			final int truncate=40;
    			if (file.length() > truncate) {
    				file = "..." + file.substring(file.length() -truncate -1);
    			}
    			this.lbFileV.setText(file);
        		this.lbFileV.setVisible(true);
                this.lbSentV.setVisible(true);
        		this.lbFile.setVisible(true);
                this.lbSent.setVisible(true);
            }
		}
	}
	
	public boolean getReadyToClose() {
		return this.sent;
	}

}
