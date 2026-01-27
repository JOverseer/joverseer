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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.joverseer.tools.BugReport;
import org.joverseer.ui.views.Messages;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationLauncher;

import com.jidesoft.plaf.LookAndFeelFactory;
/**
 * The main class for the JOverseer gui client
 */
public class JOverseerJIDEClient {
	private static final String CONTEXT_ROOT = "/ctx";
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
			Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			    showErrorDialog(throwable);
			});
			
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
			//System.setProperty( "sun.java2d.uiScale", "1.0" );
			
			cmdLineArgs = args; // save as global
			// now do the command line switches needed before we do much more.
			
			_logger.info("JOverseer Client starting up" + args);
			for (int i=0;i<args.length;i++) {
				if (args[i].equals("-L")) {
					if (i++ <args.length) {
						// change locale before we start rendering windows.
						Locale.setDefault(new Locale(args[i])); 
					}
					continue;
				}
				if (args[i].equals("-U")) {
					// force a version check if set to automatic.
					System.clearProperty("org.joverseer.ui.lastVersionCheckDate");
					continue;
				}
				if (args[i].equals("-1")) {
					continue;
				}
				if (args[i].equals("--debug")) {
					rootLogger.setLevel(Level.DEBUG);
					continue;
				}
				if (args[i].equals("--info")) {
					rootLogger.setLevel(Level.INFO);
					continue;
				}
				if (args[i].equals("d")) {
					//developer mode
					continue;
				}
				// may also be a filename, but onPostStartup() deals with that. 
			}
			// see also com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor.onPreInitialize()
			// see also com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor.onPostStartup()
			
			
			com.jidesoft.utils.Lm.verifyLicense("Marios Skounakis", "JOverseer", "L1R4Nx7vEp0nMbsoaHdH7nkRrx5F.dO");

			if(!System.getProperty("os.name").contains("Mac")) {
				LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
				LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE);
			}
			

/*			try {
				UIManager.setLookAndFeel( new FlatLightLaf() );
			} catch( Exception ex ) {
			    System.err.println( "Failed to initialize LaF" );
			}			
*///	        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");


			new ApplicationLauncher(STARTUP_CONTEXT, new String[] { APPLICATION_CONTEXT, PAGE_CONTEXT, PREFERENCES_CONTEXT });

		} catch (Exception e) {
			_logger.error(e.getMessage());
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
	
    public static void showErrorDialog(Throwable t) {
    	t.printStackTrace();
    	JDialog dialog = new JDialog(null, Messages.getString("standardMessages.Error.title"), Dialog.ModalityType.APPLICATION_MODAL);
    	
    	JPanel panel = new JPanel(new BorderLayout(0, 16));
    	
		JTextArea area = new JTextArea(Messages.getString("jOverseerCrashOccurred.message"));
		area.setEditable(false);
		area.setWrapStyleWord(true);
		area.setCaretColor(UIManager.getColor("Panel.background"));
		panel.add(area, BorderLayout.CENTER);
		
		JPanel buttonPannel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(buttonPannel, BorderLayout.PAGE_END);
		
		JButton createFile = new JButton(Messages.getString("saveDiagnostics.label"));
		createFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BugReport br = new BugReport();
				try {
					String location = br.zipReport(getStackTrace(t), true);
					if(location != null) {
				        JOptionPane.showMessageDialog(
				            null,
				            new JLabel(Messages.getString("diagnosticFile.success")),
				            Messages.getString("diagnosticFile.success.title"),
				            JOptionPane.INFORMATION_MESSAGE
				        );
				        Application.instance().close();
					}
				} catch (IOException e2) {
			        JOptionPane.showMessageDialog(
			            null,
			            new JLabel(Messages.getString("diagnosticFile.fail")),
			            Messages.getString("jOverseerCrashOccurred.message"),
			            JOptionPane.ERROR_MESSAGE
			        );
				}
			}
		});
		
		buttonPannel.add(createFile);
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Application.instance().close();
			}
		});
		
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
    }
    
    private static String getStackTrace(Throwable t) {
    	StringWriter sw = new StringWriter();
    	t.printStackTrace(new PrintWriter(sw));
    	String sStackTrace = sw.toString();
        return sStackTrace;
    }
}
