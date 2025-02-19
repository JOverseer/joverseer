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

import org.joverseer.JOApplication;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.JOVersionCompatibility;
import org.joverseer.support.RecentGames;
import org.joverseer.support.RecentGames.RecentGameInfo;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.command.LoadGame;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ExitDialog;
import org.joverseer.ui.support.dialogs.WelcomeDialog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandGroupModelBuilder;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.exceptionhandling.DefaultRegisterableExceptionHandler;
import org.springframework.richclient.exceptionhandling.RegisterableExceptionHandler;

import com.middleearthgames.updater.UpdateChecker;

/**
 * Extends the default application lifecycle advisor to allow the injection of
 * any status bar command group implementation. It also changes the repaint
 * manager to use the technique of Scott Deplap to detect illegal UI updates
 * outside of the EDT
 *
 * @author Jonny Wray
 */
public class JideApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

	private RepaintManager repaintManager;
	boolean canCloseWindow = true;
	public static boolean devOption = false;
	static boolean menusEnabled = false;

	//injected dependencies
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

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
					//TODO:I18N
					JOptionPane.showMessageDialog(parentFrame, "Not enough memory. This is often caused by running joverseer.jar directly. You should always run joverseer.bat and not joverseer.jar.", "Error", JOptionPane.ERROR_MESSAGE);
					// clear game so that the program does not ask you if you
					// want to close the program
					JideApplicationLifecycleAdvisor.this.gameHolder.setGame(null);
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
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) event);
		}
		super.onApplicationEvent(event);
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case GameChangedEvent:
			if (!menusEnabled ) {
				CommandGroupEnabler traverser = new CommandGroupEnabler();
				traverser.buildModel(Application.instance().getLifecycleAdvisor().getToolBarCommandGroup());

				JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();
				for (int i = 0; i < menuBar.getMenuCount(); i++) {
					if (!menuBar.getMenu(i).isEnabled()) {
						menuBar.getMenu(i).setEnabled(true);
					}
					for (int j = 0; j < menuBar.getMenu(i).getItemCount(); j++) {
						JMenuItem menu = menuBar.getMenu(i).getItem(j);
						if (menu != null) {
							if (!menu.isEnabled()) {
								menu.setEnabled(true);
							}
						}
					}
				}
				menusEnabled = true;
			}
		case SaveGameEvent:
			JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();

			for (int i = 0; i < menuBar.getMenuCount(); i++) {
				// recent games
				//TODO: I18N
				if (menuBar.getMenu(i).getText().equals("Game")) {
					for (int j = 0; j < menuBar.getMenu(i).getItemCount(); j++) {
						if (menuBar.getMenu(i).getItem(j) != null && menuBar.getMenu(i).getItem(j).getText() != null && menuBar.getMenu(i).getItem(j).getText().equals("Recent Games")) {
							menuBar.getMenu(i).getItem(j).removeAll();
							addRecentGamesMenu((JMenu) menuBar.getMenu(i).getItem(j));
						}
					}
				}
			}
		}
	}

	@Override
	public void onPostStartup() {
		initializeRepaintManager();
		refreshClearMapItemsVisibility();

		JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();
		
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			// recent games
			//TODO: I18N
			if (menuBar.getMenu(i).getText().equals("Game")) {
				for (int j = 0; j < menuBar.getMenu(i).getItemCount(); j++) {
					if (menuBar.getMenu(i).getItem(j) != null && menuBar.getMenu(i).getItem(j).getText() != null && menuBar.getMenu(i).getItem(j).getText().equals("Recent Games")) {
						addRecentGamesMenu((JMenu) menuBar.getMenu(i).getItem(j));
					}
				}
			}

			// user guide
			//TODO:I18N
			if (menuBar.getMenu(i).getText().equals("Help")) {
				addUserGuide(menuBar.getMenu(i));
			}

			if (menuBar.getMenu(i).getText().equals("Admin")) {
				menuBar.getMenu(i).setVisible(devOption);
			}

		}

		
		// this is the in concert with org.joverseer.ui.JOverseerJIDEClient.main()
		// and onPreInitialize()
		String[] args = JOverseerJIDEClient.cmdLineArgs;
		boolean doInitOnFirstRunOfNewJO = false;
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-L")) {
				if (i++ <args.length) {
					//skip
				}
				continue;
			}
			if (args[i].equals("-U")) {
				continue;
			}
			if (args[i].equals("-1")) {
				doInitOnFirstRunOfNewJO = true;
				continue;
			}
			if (args[i].endsWith(".jov")) {
				String fname = args[i];
				File f = new File(fname);
				if (f.exists()) {
					LoadGame lg = new LoadGame(fname,this.gameHolder);
					lg.loadGame();
				}
			}
		}
		JOVersionCompatibility fixer = new JOVersionCompatibility(this.getApplication());
		if (!doInitOnFirstRunOfNewJO) {
			doInitOnFirstRunOfNewJO = fixer.firstTimeThisVersionRun();
		}
		if (doInitOnFirstRunOfNewJO) {			
			fixer.checkAndUpdateURLs();
			fixer.markAsFirstTimeThisVersonRun();
		}
		// some commands are disabled until game loaded...
	}

	@Override
	public void onWindowOpened(ApplicationWindow arg0) {
		super.onWindowOpened(arg0);
		
		if (System.getProperty("java.version").startsWith("1.8.")) {
			final MessageDialog dlg = new MessageDialog("Java Version Check", "Obsolete java detected.\nYou will experience problems.\nDownload the full setup from https://www.gamesystems.com/gamingsoftware ") {
				@Override
				protected Object[] getCommandGroupMembers() {
					return new Object[] { new ActionCommand("actionYes") {
						@Override
						protected void doExecuteCommand() {
								getDialog().dispose();
						}
					}
					};
				}
			};
			dlg.showDialog();
			
		}
		
		if (PreferenceRegistry.instance().getPreferenceValue("general.tipOfTheDay").equals("yes")) {
			GraphicUtils.showTipOfTheDay();
		}

		// automatic version checking
		// get preference
		String pval = PreferenceRegistry.instance().getPreferenceValue("updates.autoCheckForNewVersion");
		if (pval == null || pval.equals("")) {
			// if preference is null, ask user if they want to activate version
			// checking
			//TODO:I18N
			final MessageDialog dlg = new MessageDialog("Automatic version check", "As of version 1.0.4 JOverseer comes with a mechanism to automatically check for new versions on the web site.\r\n Note that if you choose yes, JOverseer will try to connect to the internet every time upon start-up to check for a new version.\n Do you wish to activate this check?") {
				@Override
				protected Object[] getCommandGroupMembers() {
					return new Object[] { new ActionCommand("actionYes") {
						@Override
						protected void doExecuteCommand() {
							PreferenceRegistry.instance().setPreferenceValue("updates.autoCheckForNewVersion", "yes");
												getDialog().dispose();
						}
					}, new ActionCommand("actionNo") {
						@Override
						protected void doExecuteCommand() {
							PreferenceRegistry.instance().setPreferenceValue("updates.autoCheckForNewVersion", "no");
							getDialog().dispose();
						}
					} };
				}
			};
			dlg.showDialog();
		}
		// get preference value and do version checking if needed
		pval = PreferenceRegistry.instance().getPreferenceValue("updates.autoCheckForNewVersion");
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

				ApplicationDescriptor descriptor = JOApplication.getApplicationDescriptor();
				String current = descriptor.getVersion();

   		        try {
   		        	// hack to switch to https moved to -1 commandline option.
   		        	String prefValue = PreferenceRegistry.instance().getPreferenceValue("updates.RSSFeed");
   		        	String latest = UpdateChecker.getFullLatestVersion(prefValue); 
		            if (UpdateChecker.compareTo(latest, current) > 0) {
		                new com.middleearthgames.updater.UpdateInfo(UpdateChecker.getWhatsNew(prefValue));
					}
					String str = new SimpleDateFormat().format(new Date());
					prefs.put("lastVersionCheckDate", str);
				} catch (Exception exc) {
					// do nothing
				}
			}
		}
		// if not no, so that default for systems without the setting, is for it to appear
		if (PreferenceRegistry.instance().getPreferenceValue("general.showWelcome").equals("yes")) {
			WelcomeDialog landing = new WelcomeDialog();
			landing.setTitle(Messages.getString("Welcome.Title"));
			landing.setDescription(Messages.getString("Welcome.Description"));
			landing.showDialog();
		}
		
		boolean homePage = true;
		for (String c : JOverseerJIDEClient.cmdLineArgs) {
			System.out.println(c);
			if(c.equals("-disableHome")) {
				homePage = false;
				break;
			}
		}
		
		if(PreferenceRegistry.instance().getPreferenceValue("general.homeView").equals("yes") && homePage) {
			GraphicUtils.showView("homeView");
		} else if(homePage == false){
			GraphicUtils.hideView("homeView");
		}
		
		GraphicUtils.showView("currentHexDataViewer");
	}

	public void setRepaintManager(RepaintManager repaintManager) {
		this.repaintManager = repaintManager;
	}

	private void initializeRepaintManager() {
		if (this.repaintManager != null) {
			RepaintManager.setCurrentManager(this.repaintManager);
		}
	}

	@Override
	public boolean onPreWindowClose(ApplicationWindow arg0) {
		this.canCloseWindow = true;
		if (GameHolder.hasInitializedGame()) {
			this.canCloseWindow = false;
			// show warning
			MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
			ExitDialog md = new ExitDialog(ms.getMessage("confirmCloseAppDialog.title", new String[] {}, Locale.getDefault()), ms.getMessage("confirmCloseAppDialog.message", new String[] {}, Locale.getDefault())) {

				@Override
				protected void onConfirm() {
					JideApplicationLifecycleAdvisor.this.canCloseWindow = true;
				}
			};
			md.showDialog();
		}

		return this.canCloseWindow;
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

	private void addUserGuide(JMenu menu) {
		final File f = new File("JOverseerUserGuide.pdf");
		JMenuItem mi = null;
		if (f.exists()) {
			//TODO:I18N
			mi = new JMenuItem("User's Guide");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + f.getAbsolutePath());
					} catch (Exception exc) {
						// do nothing
					}
				}
			});
		}
		if (mi != null) {
			menu.add(new JSeparator());
			menu.add(mi);
		}
	}
	private void addRecentGamesMenu(JMenu menu) {
		RecentGames rg = new RecentGames();
		ArrayList<RecentGames.RecentGameInfo> rgis = rg.getRecentGameInfo();
		for (RecentGameInfo rgi : rgis) {
			final RecentGameInfo frgi = rgi;
			JMenuItem mu = new JMenuItem();
			mu.setText("Game " + String.valueOf(rgi.getNumber()) + " (Due: " + rgi.getDate() + ")");
			mu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					LoadGame loadGame = new LoadGame(frgi.getFile(),JideApplicationLifecycleAdvisor.this.gameHolder);
					loadGame.execute();
				}
			});
			menu.add(mu);
		}
	}

	public void refreshClearMapItemsVisibility() {
		CommandGroup toolbar = Application.instance().getLifecycleAdvisor().getToolBarCommandGroup();
		AbstractCommand ac = toolbar.find("clearMapItemsCommand");
		if (ac != null) {
			ac.setVisible(PreferenceRegistry.instance().getPreferenceValue("map.clearMapItemsOnToolBar").equals("yes"));
		}
	}

	public class CommandGroupEnabler extends CommandGroupModelBuilder
	{

		@Override
		protected Object buildRootModel(CommandGroup commandGroup) {
			return null;
		}

		@Override
		protected Object buildGroupModel(Object parentModel, CommandGroup commandGroup, int level) {
			return null;
		}

		@Override
		protected Object buildChildModel(Object parentModel, AbstractCommand command, int level) {
			// check for a separator first.
			if (command != null) {
				if (!command.isEnabled()) {
					command.setEnabled(true);
				}
			}
			return null;
		}
	};

}