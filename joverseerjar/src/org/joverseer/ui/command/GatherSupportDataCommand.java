package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.SubmitSupportData;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

import com.middleearthgames.updater.ThreepartVersion;

public class GatherSupportDataCommand extends ActionCommand {

	final String EOL="\r\n";
	public GatherSupportDataCommand() {
		super("GatherSupportDataCommand");
	}

	@Override
	protected void doExecuteCommand() {
		ApplicationDescriptor descriptor = joApplication.getApplicationDescriptor();
		ThreepartVersion latest = new ThreepartVersion(descriptor.getVersion());
		String report = "Version:" +descriptor.getVersion() + EOL
				+ SystemProperties();

        FormModel formModel = FormModelHelper.createFormModel(report);
        final SubmitSupportData form = new SubmitSupportData(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        dialog.setTitle(Messages.getString("changelogDialog.title"));
        dialog.showDialog();

	}
	private void reportProperty(StringBuilder sb,String prop)
	{
		sb.append(prop);
		sb.append(":");
		sb.append(System.getProperty(prop));
		sb.append(EOL);
	}
	public String SystemProperties()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("system properties"+EOL);
		reportProperty(sb,"java.home");
		reportProperty(sb,"java.vendor");
		reportProperty(sb,"java.vendor.url");
		reportProperty(sb,"java.version");
		reportProperty(sb,"os.arch");
		reportProperty(sb,"os.name");
		reportProperty(sb,"os.version");
		return sb.toString();
	}

}
