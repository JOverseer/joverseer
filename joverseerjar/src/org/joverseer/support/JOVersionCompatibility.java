package org.joverseer.support;

import java.net.MalformedURLException;

import org.joverseer.preferences.PreferenceRegistry;
import org.springframework.richclient.application.Application;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;

import com.middleearthgames.updater.UpdateInfo;

/**
 * 
 * @author Dave
 * A class to encapsulate the runtime hacks needed to ensure compatibility between versions of JO.
 *
 * So the idea/algorythm is:
 * we keep track of the version of the last JO run.
 * if the last run version is the same, then do nothing.
 * if the last run version is older, then work out what upgrades to attempt and record this version number as the last run.
 * 
 * Note: running more than one version of JO on a machine is asking for trouble.
 * Note: you can't assume the current user has write access to the filesystem and directory where the JO application lives.
 * Note: the update.jar process runs as elevated privileges which means java preferences are tied to a different user for the lifetime of that thread.
 * 
 * known compatibility issues:
 * 1. the website to hosting the update, changed from http to https:
 * 2. the website to hosting the update, changed from middleearthgames.com to gamessystem.com
 * 3. the horrible mailsender.exe was deprecated as a method to submit orders.
 * 4. the protocol for using HUpload at meturns.com has changed.
 * 5. How to migrate to later versions of 3rd party libraries?
 */
public class JOVersionCompatibility {
	private Application theApp;
	// contains the version number of JO when InitOnce was run.
	private static final String FIRST_TIME_RUN_PROPERTY="updates.LastVersionRunAfterUpdate";
	
	public JOVersionCompatibility(Application theApp) {
		this.theApp = theApp;
	}
	public boolean firstTimeThisVersionRun() {
		
		String lastVersion = PreferenceRegistry.instance().getPreferenceValue(FIRST_TIME_RUN_PROPERTY);
		if (lastVersion == null) {
			lastVersion = "";
		}
		//((JideApplicationWindow)Application.instance().getActiveWindow()).getDockingManager().showFrame("notePad");
		((JideApplicationWindow)Application.instance().getActiveWindow()).getDockingManager().showFrame("homeView");
		//PreferenceRegistry.instance().setPreferenceValue("currentHexView.disableEditOrderButton", "No");
		//PreferenceRegistry.instance().setPreferenceValue("general.homeView", "Yes");
		return !lastVersion.equals(thisVersion());
	}
	public void markAsFirstTimeThisVersonRun(String value) {
		PreferenceRegistry.instance().setPreferenceValue(FIRST_TIME_RUN_PROPERTY, value);
	}
	public void markAsFirstTimeThisVersonRun() {
		this.markAsFirstTimeThisVersonRun(thisVersion());				
	}
	public String thisVersion() {
		return this.theApp.getDescriptor().getVersion();
	}
	private void checkAndUpdate(String preference,String DEFAULT_URL,String search,String replacement)
	{
		boolean setToDefault = true;
		try {
			String oldValue = PreferenceRegistry.instance().getPreferenceValue(preference);
			if (oldValue.startsWith("http:")) {
				String prefValue = UpdateInfo.enforceHttps(oldValue, replacement);
				PreferenceRegistry.instance().setPreferenceValue(preference, prefValue);
			}
			oldValue = PreferenceRegistry.instance().getPreferenceValue(preference);
			if (oldValue.matches("(.*)"+search+"(.*)")) {
				String prefValue = oldValue.replace(search, replacement);
				PreferenceRegistry.instance().setPreferenceValue(preference, prefValue);
				setToDefault = false;
			} else {
				// Normal exit.
				setToDefault = false;
			}
		} catch (MalformedURLException e) {
			// default is to overwrite.
		} finally {
			//implied catch!
			if (setToDefault) {
				PreferenceRegistry.instance().setPreferenceValue(preference, DEFAULT_URL);
			}
		}
		
	}
	public void checkAndUpdateURLs()
	{
		checkAndUpdate("updates.RSSFeed", "https://gamesystems.com/software/joverseer/feed.xml", "middleearthgames.com", "gamesystems.com");
		checkAndUpdate("updates.DownloadPointer", "https://www.gamesystems.com/software/joverseer/url.html", "middleearthgames.com", "gamesystems.com");
		checkAndUpdateSubmit();
	}
	private void checkAndUpdateSubmit() {
		String oldValue = PreferenceRegistry.instance().getPreferenceValue("submitOrders.recipientEmail");
		if (oldValue.equals("me@middleearthgames.com")) {
			PreferenceRegistry.instance().setPreferenceValue("submitOrders.recipientEmail", "gsi@gamesystems.com");
		}
	}
}
