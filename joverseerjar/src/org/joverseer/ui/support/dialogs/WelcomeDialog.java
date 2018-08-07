package org.joverseer.ui.support.dialogs;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.ui.support.Messages;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class WelcomeDialog extends TitledApplicationDialog {

	public WelcomeDialog() {
		super();
		this.setTitlePaneTitle(Messages.getString("Welcome.TitlePaneTitle"));
	}

	@Override
	protected JComponent createTitledDialogContentPane() {
		TableLayoutBuilder lb = new TableLayoutBuilder();
		JLabel lbl = new JLabel(Messages.getString("Welcome.1"));
		lb.cell(lbl);
		lb.unrelatedGapRow();
		lbl = new JLabel(Messages.getString("Welcome.2"));
		lb.cell(lbl);
		return lb.getPanel();
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

}
