package org.joverseer.ui.support.dialogs;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.joverseer.ui.command.SaveGame;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.DefaultMessageAreaPane;

/**
 * 
 * An Exit dialog.
 * @author Dave
 *
 */
public abstract class ExitDialog extends ApplicationDialog {

	private ActionCommand saveAndFinishCommand;
    private DefaultMessageAreaPane messageAreaPane;

    private static final String CONFIRMATION_DIALOG_ICON = "confirmationDialog.icon";
    private String confirmationMessage;

    public ExitDialog(String title, String message) {
		super(title,null);
		this.saveAndFinishCommand = new SaveGame() {
			@Override
			protected void doExecuteCommand() {
				super.doExecuteCommand();
				ExitDialog.this.onFinish();
				ExitDialog.super.onCancel();
			}
		};
		this.confirmationMessage = message;
	}

	@Override
	protected Object[] getCommandGroupMembers() {
		return new AbstractCommand[] { getFinishCommand(), getCancelCommand(), getSaveAndFinishCommand() };
	}

    @Override
	protected JComponent createDialogContentPane() {
    this.messageAreaPane = new DefaultMessageAreaPane();
    Icon icon = getIconSource().getIcon(CONFIRMATION_DIALOG_ICON);
    if (icon == null) {
        icon = UIManager.getIcon("OptionPane.questionIcon");
    }
    this.messageAreaPane.setDefaultIcon(icon);
    this.messageAreaPane.setMessage(new DefaultMessage(this.confirmationMessage));
    return this.messageAreaPane.getControl();
}

@Override
protected void disposeDialogContentPane() {
	this.messageAreaPane = null;
}

	protected AbstractCommand getSaveAndFinishCommand() {
		return this.saveAndFinishCommand;
	}


	@Override
	protected void onAboutToShow() {
		registerCancelCommandAsDefault();
	}

	@Override
	protected boolean onFinish() {
		this.onConfirm();
		return true;
	}

	@Override
	protected void onCancel() {
		// TODO Auto-generated method stub
		super.onCancel();
	}

	abstract protected void onConfirm();
}
