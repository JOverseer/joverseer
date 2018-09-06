/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.joverseer.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.springframework.richclient.application.ApplicationLauncher;

import com.jidesoft.plaf.LookAndFeelFactory;

/**
 * The main class for the JOverseer gui client
 */
public class JOverseerJIDEClient {
	private static final String CONTEXT_ROOT = "/ctx";
	// private static final String CONTEXT_ROOT =
	// "F:/Programming/Projects/JOverseerDevelopment/classes/ctx";
	private static final String APPLICATION_CONTEXT = CONTEXT_ROOT + "/jideApplicationContext.xml";
	private static final String PAGE_CONTEXT = CONTEXT_ROOT + "/richclient-page-application-context.xml";
	private static final String PREFERENCES_CONTEXT = CONTEXT_ROOT + "/preferences-context.xml";
	private static final String STARTUP_CONTEXT = CONTEXT_ROOT + "/richclient-startup-context.xml";

	private static boolean testApplicationIsLaunched = false;

	public void Test() {
	};

	public static String[] cmdLineArgs;

	private static final Log _logger = LogFactory.getLog(JOverseerJIDEClient.class);

	/**
	 * Main routine for the simple sample application.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		try {
			
			SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
			FileAppender fileAppender = new FileAppender(new SimpleLayout(), getLogFilename());
			fileAppender.setName("joverseerfileappender");
			fileAppender.activateOptions();
			
			Logger rootLogger = Logger.getRootLogger();

//			rootLogger.setLevel(Level.DEBUG);
			rootLogger.setLevel(Level.WARN);
			rootLogger.addAppender(fileAppender);
			
//			for (Enumeration loggers=LogManager.getCurrentLoggers(); loggers.hasMoreElements(); )  {
//			    Logger logger2 = (Logger) loggers.nextElement();
//			    _logger.warn("logger:" + logger2.getName());
//		    for (Enumeration appenders=logger2.getAllAppenders(); appenders.hasMoreElements(); )  {
//			        Appender appender = (Appender) appenders.nextElement();
//				    _logger.warn("appender-"+appender.getName());
//		    }}
//		    
			
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); // until sorting bug fixed.
			cmdLineArgs = args;
			_logger.info("JOverseer Client starting up");
			if (args.length >0) {
				if (args[0].equals("-L")) {
					Locale.setDefault(new Locale(args[1]));
				}
				if (args[0].equals("-U")) {
					System.clearProperty("org.joverseer.ui.lastVersionCheckDate");
				}
			}
			com.jidesoft.utils.Lm.verifyLicense("Marios Skounakis", "JOverseer", "L1R4Nx7vEp0nMbsoaHdH7nkRrx5F.dO");
			//LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
			
			
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

			// LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE);
			// In order to launch the platform, we have to construct an
			// application context that defines the beans (services) and
			// wiring. This is pretty much straight Spring.
			//
			// Part of this configuration will indicate the initial page to be
			// displayed.

			String rootContextDirectoryClassPath = "/ctx";

			// The startup context defines elements that should be available
			// quickly such as a splash screen image.

			String startupContextPath = rootContextDirectoryClassPath + "/richclient-startup-context.xml";

			String richclientApplicationContextPath = rootContextDirectoryClassPath + "/richclient-application-context.xml";

			// The ApplicationLauncher is responsible for loading the contexts,
			// presenting the splash screen, initializing the Application
			// singleton instance, creating the application window to display
			// the initial page.
			// Application.instance().

			new ApplicationLauncher(STARTUP_CONTEXT, new String[] { APPLICATION_CONTEXT, PAGE_CONTEXT, PREFERENCES_CONTEXT });

		} catch (Exception e) {
			System.out.println(e);
			throw e;
			// System.exit(1);
		}
	}
	// put the log somewhere writable
	public static String getLogFilename() {
		File log;
		String name;
		SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
		name = "joverseer_"+format.format(Calendar.getInstance().getTime());
		try {
			log = File.createTempFile(name, ".log");
			// this next line will get skipped on an exception
			name = log.getAbsolutePath();
		} catch (IOException e) {
			// fallback to somewhere that should be writable, but a bit messy for the user.
			name = System.getProperty("user.home") + File.separator + name + ".log"; 
		}
		return name;
	}
	public static void launchTestFramework() {
		if (!testApplicationIsLaunched) {
			try {
				com.jidesoft.utils.Lm.verifyLicense("Marios Skounakis", "JOverseer", "L1R4Nx7vEp0nMbsoaHdH7nkRrx5F.dO");
				// LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

				// The ApplicationLauncher is responsible for loading the
				// contexts,
				// presenting the splash screen, initializing the Application
				// singleton instance, creating the application window to
				// display
				// the initial page.
				// Application.instance().
				new ApplicationLauncher(STARTUP_CONTEXT, new String[] { APPLICATION_CONTEXT, PAGE_CONTEXT, PREFERENCES_CONTEXT });
				testApplicationIsLaunched = true;

			} catch (Exception e) {
				System.out.println(e + "\n-----Stack trace follows------\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * A class to handle uncaught exceptions on the AWT event thread. This is
	 * registered (wired in) in the application context as part of the
	 * definition of the application lifecycle advisor.
	 */
	public static class AWTExceptionHandler {

		public void handle(Throwable e) {
			// You should probably do something more clever than this.
			_logger.error("Exception on AWT Event Thread", e);
		}
	}
}
