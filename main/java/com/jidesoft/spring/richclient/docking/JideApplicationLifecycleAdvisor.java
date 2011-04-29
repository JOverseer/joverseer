/*
 * Copyright 2005 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.jidesoft.spring.richclient.docking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.RepaintManager;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.support.RecentGames.RecentGameInfo;
import org.joverseer.support.versionCheck.VersionChecker;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.command.LoadGame;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.exceptionhandling.DefaultRegisterableExceptionHandler;
import org.springframework.richclient.exceptionhandling.RegisterableExceptionHandler;

/**
 * Extends the default application lifecycle advisor to allow the injection of
 * any status bar command group implementation. It also changes the repaint
 * manager to use the technique of Scott Deplap to detetect illegal UI updates
 * outside of the EDT
 * 
 * @author Jonny Wray
 */
public class JideApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

	private RepaintManager repaintManager;
	boolean canCloseWindow = true;
	public static boolean devOption = false;

	public CommandGroup getSpecificCommandGroup(String name) {
		return getCommandGroup(name);
	}

	@Override
	public RegisterableExceptionHandler getRegisterableExceptionHandler() {
		RegisterableExceptionHandler handler = new DefaultRegisterableExceptionHandler() {
			// customized exception handler for handling out of memory errors
			// and giving
			// a specialized message
			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				if (OutOfMemoryError.class.isInstance(arg1)) {
					JFrame parentFrame = (getApplication().getActiveWindow() == null) ? null : getApplication().getActiveWindow().getControl();
					JOptionPane.showMessageDialog(parentFrame, "Not enough memory. This is often caused by running joverseer.jar directly. You should always run joverseer.bat and not joverseer.jar.", "Error", JOptionPane.ERROR_MESSAGE);
					// clear game so that the program does not ask you if you
					// want to close the program
					GameHolder.instance().setGame(null);
					// close program
					Application.instance().close();
				} else {
					super.uncaughtException(arg0, arg1);
				}
			}

		};

		return handler;
	}

	@Override
	public void onPostStartup() {
		initializeRepaintManager();
		if (devOption == false) {
			JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();
			for (int i = 0; i < menuBar.getMenuCount(); i++) {
				if (menuBar.getMenu(i).getText().equals("Admin")) {
					menuBar.getMenu(i).setVisible(false);
				}
			}
		}
		JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			// recent games
			if (menuBar.getMenu(i).getText().equals("Game")) {
				for (int j = 0; j < menuBar.getMenu(i).getItemCount(); j++) {
					if (menuBar.getMenu(i).getItem(j) != null && menuBar.getMenu(i).getItem(j).getText() != null && menuBar.getMenu(i).getItem(j).getText().equals("Recent Games")) {
						JMenu menu = (JMenu) menuBar.getMenu(i).getItem(j);
						RecentGames rg = new RecentGames();
						ArrayList<RecentGames.RecentGameInfo> rgis = rg.getRecentGameInfo();
						for (RecentGameInfo rgi : rgis) {
							final RecentGameInfo frgi = rgi;
							JMenuItem mu = new JMenuItem();
							mu.setText("Game " + String.valueOf(rgi.getNumber()));
							mu.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									LoadGame loadGame = new LoadGame(frgi.getFile());
									loadGame.execute();
								}
							});
							menu.add(mu);
						}
					}
				}
			}

			// user guide
			if (menuBar.getMenu(i).getText().equals("Help")) {
				try {
					final File f = new File("JOverseerUserGuide.pdf");
					if (f.exists()) {
						JMenuItem mi = new JMenuItem("User's Guide");
						mi.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + f.getAbsolutePath());
								} catch (Exception exc) {
									// do nothing
								}
							}
						});
						menuBar.getMenu(i).add(new JSeparator());
						menuBar.getMenu(i).add(mi);
					}
				} catch (Exception exc) {
					// do nothing
				}
			}

		}

		if (JOverseerJIDEClient.cmdLineArgs != null && JOverseerJIDEClient.cmdLineArgs.length == 1 && JOverseerJIDEClient.cmdLineArgs[0].endsWith(".jov")) {
			String fname = JOverseerJIDEClient.cmdLineArgs[0];
			File f = new File(fname);
			if (f.exists()) {
				LoadGame lg = new LoadGame(fname);
				lg.loadGame();
			}
		}
	}

	@Override
	public void onWindowOpened(ApplicationWindow arg0) {
		super.onWindowOpened(arg0);
		if (PreferenceRegistry.instance().getPreferenceValue("general.tipOfTheDay").equals("yes")) {
			GraphicUtils.showTipOfTheDay();
		}

		// automatic version checking
		// get preference
		String pval = PreferenceRegistry.instance().getPreferenceValue("general.autoCheckForNewVersion");
		if (pval == null || pval.equals("")) {
			// if preference is null, ask user if they want to activate version
			// checking
			final MessageDialog dlg = new MessageDialog("Automatic version check", "As of version 1.0.4 JOverseer comes with a mechanism to automatically check for new versions on the web site.\r\n Note that if you choose yes, JOverseer will try to connect to the internet every time upon start-up to check for a new version.\n Do you wish to activate this check?") {
				@Override
				protected Object[] getCommandGroupMembers() {
					return new Object[] { new ActionCommand("actionYes") {
						@Override
						protected void doExecuteCommand() {
							PreferenceRegistry.instance().setPreferenceValue("general.autoCheckForNewVersion", "yes");
							getDialog().dispose();
						}
					}, new ActionCommand("actionNo") {
						@Override
						protected void doExecuteCommand() {
							PreferenceRegistry.instance().setPreferenceValue("general.autoCheckForNewVersion", "no");
							getDialog().dispose();
						}
					} };
				}
			};
			dlg.showDialog();
		}
		// get preference value and do version checking if needed
		pval = PreferenceRegistry.instance().getPreferenceValue("general.autoCheckForNewVersion");
		if (pval.equals("yes")) {
			// check once every week
			Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);

			pval = prefs.get("lastVersionCheckDate", null);
			Date dt = null;
			try {
				dt = new SimpleDateFormat().parse(pval);
			} catch (Exception exc) {
				// do nothing
			}
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, -7);
			Date dateMinusOneWeek = c.getTime();
			if (dt == null || dateMinusOneWeek.after(dt)) {

				VersionChecker versionChecker = new VersionChecker();
				try {
					boolean newVersionExists = versionChecker.newVersionExists();
					if (newVersionExists) {
						MessageDialog md = new MessageDialog("A new version is available!", "A new version of JOverseer is available.\n If you wish to download it, visit the downloads page.\n<a href='http://code.google.com/p/joverseer/downloads/list'>http://code.google.com/p/joverseer/downloads/list</a>") {

						};
						md.showDialog();
					}
					String str = new SimpleDateFormat().format(new Date());
					prefs.put("lastVersionCheckDate", str);
				} catch (Exception exc) {
					// do nothing
					int a = 1;
				}
			}
		}
	}

	public void setRepaintManager(RepaintManager repaintManager) {
		this.repaintManager = repaintManager;
	}

	private void initializeRepaintManager() {
		if (repaintManager != null) {
			RepaintManager.setCurrentManager(repaintManager);
		}
	}

	@Override
	public boolean onPreWindowClose(ApplicationWindow arg0) {
		canCloseWindow = true;
		if (GameHolder.hasInitializedGame()) {
			canCloseWindow = false;
			// show warning
			MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
			ConfirmationDialog md = new ConfirmationDialog(ms.getMessage("confirmCloseAppDialog.title", new String[] {}, Locale.getDefault()), ms.getMessage("confirmCloseAppDialog.message", new String[] {}, Locale.getDefault())) {

				@Override
				protected void onConfirm() {
					canCloseWindow = true;
				}
			};
			md.showDialog();
		}

		return canCloseWindow;
	}

	@Override
	public void onPreInitialize(Application arg0) {
		super.onPreInitialize(arg0);

		devOption = true;
		if (JOverseerJIDEClient.cmdLineArgs == null || JOverseerJIDEClient.cmdLineArgs.length == 0)
			devOption = false;
		if (devOption) {
			devOption = false;
			for (String c : JOverseerJIDEClient.cmdLineArgs) {
				if (c.equals("d"))
					devOption = true;
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			super.afterPropertiesSet();
		} catch (Exception exc) {
			// workaround
		}
	}

}
