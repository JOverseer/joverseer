package org.joverseer.ui.command;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.joverseer.JOApplication;
import org.joverseer.ui.support.Messages;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ApplicationDialog;

public class GatherSupportDataCommand extends ActionCommand {

	protected static final String OPEN_LOG_COMMAND_ID = "openLogCommand";
	final String EOL="\r\n";
	JTextArea textArea;
	public GatherSupportDataCommand() {
		super("GatherSupportDataCommand");
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
        	
            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
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

}
