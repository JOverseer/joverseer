package org.joverseer.ui.command;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.orders.export.OrderTextGenerator;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Opens the ExportOrderTextForm form
 *
 * @author Marios Skounakis
 */
public class ExportOrderTextCommand extends ActionCommand {
	//dependencies
	GameHolder gameHolder;
	public ExportOrderTextCommand(GameHolder gameHolder) {
		super("exportOrderTextCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		final ExportOrderTextForm form = new ExportOrderTextForm(FormModelHelper.createFormModel(new String()));
		FormBackedDialogPage pg = new FormBackedDialogPage(form);
		CustomTitledPageApplicationDialog dlg = new CustomTitledPageApplicationDialog(pg) {
			@Override
			protected Object[] getCommandGroupMembers() {
				return new Object[] { getFinishCommand() };
			}

			@Override
			protected boolean onFinish() {
				return true;
			}
		};

		dlg.setTitle(Messages.getString("exportOrderTextDialog.title"));
		dlg.showDialog();

	}

	class ExportOrderTextForm extends AbstractForm implements ClipboardOwner {
		JComboBox nation;
		JTextArea orders;

		private ExportOrderTextForm(FormModel arg0) {
			super(arg0, "exportOrderTextForm");
		}

		private ArrayList<String> getNationItems() {
			Game g = ExportOrderTextCommand.this.gameHolder.getGame();
			ArrayList<String> ret = new ArrayList<String>();
			for (PlayerInfo pi : g.getTurn().getPlayerInfo()) {
				ret.add(g.getMetadata().getNationByNum(pi.getNationNo()).getName());
			}
			return ret;
		}

		private int getSelectedNationNo() {
			String nationName = this.nation.getSelectedItem().toString();
			Game g = ExportOrderTextCommand.this.gameHolder.getGame();
			return g.getMetadata().getNationByName(nationName).getNumber();
		}

		@Override
		protected JComponent createFormControl() {
			Game g = ExportOrderTextCommand.this.gameHolder.getGame();

			GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
			glb.append(new JLabel(Messages.getString("standardFields.Nation")));
			glb.append(this.nation = new JComboBox(getNationItems().toArray()));

			this.nation.setSelectedIndex(0);
			this.nation.setSelectedItem(g.getMetadata().getNationByNum(g.getMetadata().getNationNo()).getName());

			this.nation.setPreferredSize(new Dimension(100, 24));
			glb.nextLine();

			this.orders = new JTextArea();
			this.orders.setWrapStyleWord(false);
			this.orders.setLineWrap(false);
			JScrollPane scp = new JScrollPane(this.orders);
			scp.setPreferredSize(new Dimension(500, 400));
			glb.append(scp, 2, 1);

			glb.nextLine();
			JButton generate = new JButton(Messages.getString("exportOrderTextForm.BtnGenerate"));
			glb.append(generate, 1, 1);
			glb.nextLine();
			generate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OrderTextGenerator gen = new OrderTextGenerator();
					Game g1 = ExportOrderTextCommand.this.gameHolder.getGame();
					try {
						ExportOrderTextForm.this.orders.setText(gen.generateOrderFile(g1, g1.getTurn(), getSelectedNationNo()));
						ExportOrderTextForm.this.orders.setCaretPosition(0);
					} catch (Exception exc) {
						ExportOrderTextForm.this.orders.setText(exc.getMessage());
					}
				}
			});

			JButton ctc = new JButton(Messages.getString("standardActions.CopyToClipboard"));
			glb.append(ctc, 1, 1);
			glb.nextLine();
			final ClipboardOwner clipboardOwner = this;
			ctc.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					StringSelection stringSelection = new StringSelection(ExportOrderTextForm.this.orders.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, clipboardOwner);
				}
			});

			return glb.getPanel();
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

	}

}
