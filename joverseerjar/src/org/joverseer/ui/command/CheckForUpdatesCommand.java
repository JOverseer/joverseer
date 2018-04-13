package org.joverseer.ui.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.JOverseerJIDEClient;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.DefaultApplicationDescriptor;
import org.springframework.richclient.command.ActionCommand;

import com.middleearthgames.updater.ThreepartVersion;
import com.middleearthgames.updater.UpdateChecker;

public class CheckForUpdatesCommand extends ActionCommand{
	public CheckForUpdatesCommand() {
		super("CheckForUpdatesCommand");
	}

	@Override
	protected void doExecuteCommand() {
		DefaultApplicationDescriptor descriptor = (DefaultApplicationDescriptor)Application.instance().getApplicationContext().getBean("applicationDescriptor");
		ThreepartVersion latest,current = new ThreepartVersion(descriptor.getVersion());
		String title;
        
	    try {
            latest = UpdateChecker.getLatestVersion(PreferenceRegistry.instance().getPreferenceValue("updates.RSSFeed"));
            if (latest.isLaterThan(current)) {
            	title = "Later version available";
            } else if (latest.equals(current)) {
            	title = "You have the latest version already. Update to reinstall.";
            } else {
            	title ="You have a later version. Update to install earlier official version.";
            }
	       	new com.middleearthgames.updater.UpdateInfo(UpdateChecker.getWhatsNew(PreferenceRegistry.instance().getPreferenceValue("updates.RSSFeed")),title);
			String str = new SimpleDateFormat().format(new Date());
			Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
			prefs.put("lastVersionCheckDate", str);
		} catch (Exception exc) {
			// do nothing
		}
		
	}

}
