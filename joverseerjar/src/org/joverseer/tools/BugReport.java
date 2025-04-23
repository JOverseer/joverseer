package org.joverseer.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.joverseer.support.AppDataManager;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.BusyIndicator;

public class BugReport {
	GameHolder gameHolder;
	String emailAddress;
	List<File> addFiles;
	
	public BugReport() {
		this.gameHolder = null;
		this.addFiles = null;
	}
	
	public BugReport(GameHolder gh, List<File> additionalFiles, String email) {
		this.gameHolder = gh;
		this.emailAddress = email;
		this.addFiles = additionalFiles;
	}
	
	public String zipReport(String message) throws IOException {
		System.out.println(this.gameHolder.getFile());
		List<File> filesToZip = collectFiles(message);
		
		String path = AppDataManager.getPath() + File.separator + "BUGREPORT-" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".zip";
        final FileOutputStream fos = new FileOutputStream(path);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        
        boolean deleteFirstFile = true;

        for (File fileToZip : filesToZip) {
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

            fis.close();
            if(deleteFirstFile) {
            	fileToZip.delete();
            	deleteFirstFile = false;
            }
        }

        zipOut.close();
        fos.close();
        
        return path;
	}
	
	private List<File> collectFiles(String msgContent){

		String logFile = null;
		try {
			Logger l1 = Logger.getRootLogger();
			Appender a1 = l1.getAppender("joverseerfileappender");
			
			if (a1 != null) {
				if (a1 instanceof org.apache.log4j.FileAppender) {
					org.apache.log4j.FileAppender fileAppender = (org.apache.log4j.FileAppender)a1;
					logFile = (fileAppender.getFile());
				}
			}
		} catch (Exception e) {
			
		}
		
		File msgFile = new File(AppDataManager.getPath() + File.separator + "report.txt");
		try {
			FileWriter wr = new FileWriter(msgFile);
			wr.write(msgContent);
			wr.flush();
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			File f = new File(this.gameHolder.getFile());
			return Arrays.asList(msgFile, new File(logFile));
			
		}catch(NullPointerException e){
			return Arrays.asList(msgFile, new File(logFile));
		}
		
//		if (this.gameHolder == null) return Arrays.asList(msgFile, new File(logFile));
//		
//		String gameFile = this.gameHolder.getFile();
//		
//		return Arrays.asList(msgFile, new File(gameFile), new File(logFile));
	}
	
	public boolean sendBugReport(String email, String name, String acct, File file) throws HttpException, IOException {
		boolean success = false;
		String url = "http://www.meturn.com/cgi-bin/HUpload.exe";
		final PostMethod filePost = new PostMethod(url);
		Part[] parts = { new StringPart("emailaddr", email), new StringPart("name", name), new StringPart("account", acct), new FilePart(file.getName(), file) };
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK) {
			Scanner s = new Scanner(filePost.getResponseBodyAsStream()).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";

			if(result.indexOf("Submitting ...Success") == -1) {
				success = false;
			}
			else success = true;
			
			if (success) JOptionPane.showMessageDialog(null, "Email succesfully sent!", "Bug Report", JOptionPane.INFORMATION_MESSAGE);
			else JOptionPane.showMessageDialog(null, "Error occured, email not sent. Try again later", "Bug Report", JOptionPane.INFORMATION_MESSAGE);
			s.close();
//			final SubmitOrdersResultsForm frm = new SubmitOrdersResultsForm(FormModelHelper.createFormModel(new Object()));
//			FormBackedDialogPage page = new FormBackedDialogPage(frm);
//			CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
//
//				@Override
//				protected void onAboutToShow() {
//					try {
//						((HTMLDocument) frm.getJEditorPane().getDocument()).setBase(new URI("http://www.meturn.com/").toURL());
//						frm.getJEditorPane().getEditorKit().read(filePost.getResponseBodyAsStream(), frm.getJEditorPane().getDocument(), 0);
//						this.setDescription(this.getMessage("ExportDiploForm.DiploSentByMETURNSuccessMessage", new Object[] { file }));
//						this.setTitlePaneTitle("Hi 1!");
//						BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
//					} catch (Exception exc) {
//						//ExportDiploForm.this.sent = false;
//						this.logger.error(exc);
//					}
//				}
//
//				@Override
//				protected boolean onFinish() {
//					return true;
//				}
//				@Override
//				protected Object[] getCommandGroupMembers() {
//					return new AbstractCommand[] { getFinishCommand() };
//				}
//			};
//			dialog.setTitle(Messages.getString("submitDiploDialog.title"));
			BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
//			dialog.showDialog();
//			if (!this.sent) {
//				Game g = this.gameHolder.getGame();
//				int nationNo = this.gameHolder.getGame().getMetadata().getNationNo();
//				PlayerInfo pi = g.getTurn().getPlayerInfo(nationNo);
//				filePost.releaseConnection();
//				pi.setDiploSentOn(new Date());
//				this.sent = true;
//				pi.setDiploSent(true);
//				//autoSaveGameAccordingToPref();
//			}
		} else {
			//this.sent=false;
			filePost.releaseConnection();
		}
		
		return success;
	}
	
}
