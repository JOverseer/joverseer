package org.joverseer.ui.command;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import org.joverseer.JOApplication;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.views.ExportOrdersForm;
import org.joverseer.ui.views.Messages;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

import com.middleearthgames.updater.ThreepartVersion;
import com.middleearthgames.updater.UpdateChecker;
import com.middleearthgames.updater.UpdateInfo;

public class CheckForUpdatesCommand extends ActionCommand{
	public boolean cancel = false;

	public CheckForUpdatesCommand() {
		super("CheckForUpdatesCommand");
	}

	@Override
	protected void doExecuteCommand() {
		ApplicationDescriptor descriptor = JOApplication.getApplicationDescriptor();
		ThreepartVersion latest,current = new ThreepartVersion(descriptor.getVersion());
		String title;

	    try {
	    	// hack to switch to https
	    	String oldValue = PreferenceRegistry.instance().getPreferenceValue("updates.RSSFeed");
	        String prefValue = UpdateInfo.enforceHttps(oldValue, "middleearthgames.com");
	        if (oldValue.length() != prefValue.length()) {
	        	PreferenceRegistry.instance().setPreferenceValue("updates.RSSFeed", prefValue);
	        }
            latest = UpdateChecker.getLatestVersion(prefValue);
            if (latest.isLaterThan(current)) {
            	title = "Later version available";
            } else if (latest.equals(current)) {
            	this.cancel = false;
				ConfirmationDialog dlg = new ConfirmationDialog(Messages.getString("standardMessages.Message"),
						Messages.getString("CheckForUpdatesCommand.upToDateMessage")) {
					@Override
					protected void onCancel() {
						super.onCancel();
						CheckForUpdatesCommand.this.cancel = true;
					}

					@Override
					protected void onConfirm() {
					}

				};
				dlg.setPreferredSize(new Dimension(450,50));
				dlg.showDialog();
            	title = "You have the latest version already. Update to reinstall.";
            } else {
            	title ="You have a later version. Update to install earlier official version.";
            }
            if(this.cancel) return;
	       	new com.middleearthgames.updater.UpdateInfo(UpdateChecker.getWhatsNew(prefValue),title);
			String str = new SimpleDateFormat().format(new Date());
			Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
			prefs.put("lastVersionCheckDate", str);
	    } catch (javax.net.ssl.SSLHandshakeException e) {
			e.printStackTrace();
			System.out.print("try restarting with -Djavax.net.debug=all and check the logs");
			//javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
	    	
		} catch (Exception exc) {
			// do nothing
			System.out.print("exception in updater");
			exc.printStackTrace();
		}

	}

}
