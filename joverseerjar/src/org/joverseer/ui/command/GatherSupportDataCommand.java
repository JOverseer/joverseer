package org.joverseer.ui.command;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.joverseer.JOApplication;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.BugReport;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.ExportDiploForm;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ApplicationDialog;

public class GatherSupportDataCommand extends ActionCommand {

	protected static final String OPEN_LOG_COMMAND_ID = "openLogCommand";
	protected static final String OPEN_BUG_REPORT_COMMAND_ID = "openBugReportCommand";
	protected static final String SEND_BUG_REPORT_COMMAND_ID = "sendBugReportCommand";
	final String EOL="\r\n";
	JTextArea textArea;
	GameHolder gameHolder;
	
	public GatherSupportDataCommand(GameHolder gameHolder) {
		super("GatherSupportDataCommand");
		this.gameHolder = gameHolder;
	}
	
	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		ApplicationDescriptor descriptor = JOApplication.getApplicationDescriptor();

		String report = "Version:" +descriptor.getVersion() + this.EOL
				+ SystemProperties();
		this.textArea = new JTextArea();
		this.textArea.append(report);
		this.textArea.append(this.ScreenInfo());
		Logger l = Logger.getRootLogger();
		Appender a = l.getAppender("joverseerfileappender");
		
		if (a != null) {
			if (a instanceof org.apache.log4j.FileAppender) {
				org.apache.log4j.FileAppender fileAppender = (org.apache.log4j.FileAppender)a;
				this.textArea.append(fileAppender.getFile());
			}
		}
        ApplicationDialog dialog = new ApplicationDialog() {
        	ActionCommand openLogCommand = new ActionCommand(OPEN_LOG_COMMAND_ID) {
    			@Override
				public void doExecuteCommand() {
    				try {
    					Logger l1 = Logger.getRootLogger();
    					Appender a1 = l1.getAppender("joverseerfileappender");
    					
    					if (a1 != null) {
    						if (a1 instanceof org.apache.log4j.FileAppender) {
    							org.apache.log4j.FileAppender fileAppender = (org.apache.log4j.FileAppender)a1;
    	    					Desktop.getDesktop().open(new File(fileAppender.getFile()));
    						}
    					}
    				} catch (Exception e) {
    					
    				}
    			}
        	};
        	ActionCommand openBugReportCommand = new ActionCommand(OPEN_BUG_REPORT_COMMAND_ID) {

				@Override
				protected void doExecuteCommand() {
					BugReportEmail emailDialog = new BugReportEmail();
					emailDialog.setTitle(Messages.getString("GatherSupportDataCommand.title"));
					emailDialog.showDialog();
				}
        		
        	};
        	
            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                	//getBugReportCommand(),getOpenLogCommand(),getFinishCommand()
                	getOpenLogCommand(),getFinishCommand()
                };
            }

			@Override
			protected JComponent createDialogContentPane() {
				return GatherSupportDataCommand.this.textArea;
			}
			ActionCommand getOpenLogCommand() {
				return this.openLogCommand;
			}
			ActionCommand getBugReportCommand() {
				return this.openBugReportCommand;
			}
        };
        dialog.setTitle(Messages.getString("GatherSupportDataCommand.title"));
        dialog.showDialog();

	}
	private void reportProperty(StringBuilder sb,String prop)
	{
		sb.append(prop);
		sb.append(":");
		sb.append(System.getProperty(prop));
		sb.append(this.EOL);
	}
	private void reportEnvironmentVariable(StringBuilder sb,String name)
	{
		sb.append(name);
		sb.append(":");
		wordWrap(sb,System.getenv(name),80);
		sb.append(this.EOL);
	}
	private void wordWrap(StringBuilder sb,String value,int limit)
	{
		int start,stop,remaining;
		start = 0;
		if (value == null) {
			sb.append("not defined");
			return;
		}
	
		remaining = value.length();
		while (remaining > 0) {
			stop = start + remaining;
			if (remaining > limit) {
				stop = start + limit -1; // -1 because we count from 0
			}
			sb.append(value.substring(start, stop));
			sb.append(this.EOL);
			start = stop; //no +1 as we start from 0
			// note the last time through, remaining goes -ve.
			remaining -= limit; 
		}
	}
//TODO: maybe use https://github.com/oshi/oshi for diagnostics.
	public String SystemProperties()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Environment variables"+this.EOL);
		reportEnvironmentVariable(sb, "PATH");
		reportEnvironmentVariable(sb, "JREHOMEDIR");
		reportEnvironmentVariable(sb, "JAVA_HOME");
		sb.append("system properties"+this.EOL);
		reportProperty(sb,"java.home");
		reportProperty(sb,"java.vendor");
		reportProperty(sb,"java.vendor.url");
		reportProperty(sb,"java.version");
		reportProperty(sb,"os.arch");
		reportProperty(sb,"os.name");
		
		sb.append("Note: some versions of java incorrectly report Windows 11 as 10."+this.EOL);
		reportProperty(sb,"os.version");
		reportProperty(sb,"sun.java2d.uiScale");
		reportProperty(sb,"sun.java2d.dpiaware");
		reportProperty(sb,"sun.java2d.ddoffscreen");
		reportProperty(sb,"sun.java2d.d3d");
		reportProperty(sb,"sun.java2d.noddraw");

		return sb.toString();
	}
        
	public String ScreenInfo()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		return String.format("reported size = %.0f by %.0f\r\n",width,height);
	}
	
	class BugReportEmail extends ApplicationDialog{
		JTextArea emailContent;
		JTextField emailAddress;
		List<File> attatchments;

		@Override
		protected JComponent createDialogContentPane() {
			JPanel p = new JPanel(new BorderLayout(5, 5));
			
			JPanel subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
			JLabel lb = new JLabel("Email: ");
			subPanel.add(lb);
			this.emailAddress = new JTextField();
			
			Preferences prefs = Preferences.userNodeForPackage(ExportDiploForm.class);
			String email = prefs.get("useremail", "");
			System.out.println(email + " jsjaodk");
			this.emailAddress.setText(email);
			
			subPanel.add(this.emailAddress);
			
			p.add(subPanel, BorderLayout.PAGE_START);
			
			this.emailContent = new JTextArea(20,50);
			this.emailContent.setLineWrap(true);
			this.emailContent.setWrapStyleWord(true);
			JScrollPane scp = new JScrollPane(this.emailContent);
			p.add(scp);
			
			// TODO Auto-generated method stub
			return p;
		}

		@Override
		protected boolean onFinish() {
			// TODO Auto-generated method stub
			this.dispose();
			return true;
		}
		
        @Override
		protected Object[] getCommandGroupMembers() {
            return new AbstractCommand[] {
            	getSendEmailCommand(),getFinishCommand()
            };
        }
        
        ActionCommand getSendEmailCommand() {
        	return this.sendEmail;
        }
        
        ActionCommand sendEmail = new ActionCommand(SEND_BUG_REPORT_COMMAND_ID) {

			@Override
			protected void doExecuteCommand() {
				// TODO Auto-generated method stub
				GameHolder gh = GatherSupportDataCommand.this.gameHolder;
				
				BugReport br = new BugReport(gh, BugReportEmail.this.attatchments, BugReportEmail.this.emailAddress.getText());
				
				try {
					String zipLocation = br.zipReport(BugReportEmail.this.emailContent.getText());
					
					String name = gh.getGame().getTurn().getPlayerInfo(gh.getGame().getMetadata().getNationNo()).getPlayerName();
					if (name == null)
						name = "null";
					String acct = gh.getGame().getTurn().getPlayerInfo(gh.getGame().getMetadata().getNationNo()).getAccountNo();
					if (acct == null)
						acct = "null";
					
					br.sendBugReport(BugReportEmail.this.emailAddress.getText(), name, acct, new File(zipLocation));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
        	
        };
		
	}

}
