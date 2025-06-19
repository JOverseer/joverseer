package org.joverseer.ui.command;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.joverseer.JOApplication;
import org.joverseer.ui.views.Messages;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ApplicationDialog;

public class AboutCommand extends ActionCommand {

	final String EOL="\r\n";
	final String messageCode = "applicationDescriptor.";
	private JTextArea textArea;

	@Override
	protected void doExecuteCommand() {
		// TODO Auto-generated method stub
		ApplicationDescriptor descriptor = JOApplication.getApplicationDescriptor();
		String aboutText = Messages.getString(this.messageCode + "caption") + this.EOL + this.EOL + Messages.getString(this.messageCode + "description") + this.EOL + this.EOL;
		String report = "Version: " +descriptor.getVersion() + this.EOL
				+ "Build ID: " + descriptor.getBuildId();
		this.textArea = new JTextArea();
		this.textArea.append(aboutText);
		this.textArea.append(report);
		this.textArea.setWrapStyleWord(true);
		this.textArea.setLineWrap(true);
		this.textArea.setEditable(false);	
		Color c = UIManager.getColor("JPanel.background");
		this.textArea.setCaretColor(c);
		this.textArea.setBackground(c);
		
		ApplicationDialog dialog = new ApplicationDialog() {

			@Override
			protected JComponent createDialogContentPane() {
				// TODO Auto-generated method stub
				return AboutCommand.this.textArea;
			}
			
            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                	//getBugReportCommand(),getOpenLogCommand(),getFinishCommand()
                	getFinishCommand()
                };
            }

			@Override
			protected boolean onFinish() {
				// TODO Auto-generated method stub
				return true;
			}
			
		};
        dialog.setTitle(Messages.getString("applicationDescriptor.title"));
        dialog.setPreferredSize(new Dimension(300, 250));
        dialog.showDialog();
	}

}
