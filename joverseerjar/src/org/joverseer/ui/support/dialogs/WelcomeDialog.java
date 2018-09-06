package org.joverseer.ui.support.dialogs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.preferences.PreferenceRegistry;
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
		JCheckBox checkBox = new JCheckBox(new AbstractAction(Messages.getString("Welcome.3")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e1) {
				if (e1.getSource() instanceof JCheckBox) {
					JCheckBox cBox = (JCheckBox) e1.getSource();
					PreferenceRegistry.instance().setPreferenceValue("general.showWelcome", cBox.isSelected() ? "yes" : "no");
				}
			}
		});
		checkBox.setSelected(PreferenceRegistry.instance().getPreferenceValue("general.showWelcome").equals("yes"));
		lb.unrelatedGapRow();
		lb.cell(checkBox);
		return lb.getPanel();
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

}
