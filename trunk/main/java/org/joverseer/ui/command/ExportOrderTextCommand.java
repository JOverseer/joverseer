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
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.orders.export.OrderTextGenerator;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.controls.ResourceButton;
import org.joverseer.ui.support.controls.ResourceLabel;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Opens the ExportOrderTextForm form
 * 
 * @author Marios Skounakis
 */
public class ExportOrderTextCommand extends ActionCommand {
	public ExportOrderTextCommand() {
		super("exportOrderTextCommand");
	}

	@Override
	protected void doExecuteCommand() {
		final ExportOrderTextForm form = new ExportOrderTextForm(FormModelHelper.createFormModel(new String()));
		FormBackedDialogPage pg = new FormBackedDialogPage(form);
		TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
			@Override
			protected Object[] getCommandGroupMembers() {
				return new Object[] { getFinishCommand() };
			}

			@Override
			protected boolean onFinish() {
				return true;
			}
		};

		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		dlg.setTitle(ms.getMessage("exportOrderTextDialog.title", new Object[] {}, Locale.getDefault()));
		dlg.showDialog();

	}

	class ExportOrderTextForm extends AbstractForm implements ClipboardOwner {
		JComboBox nation;
		JTextArea orders;

		private ExportOrderTextForm(FormModel arg0) {
			super(arg0, "exportOrderTextForm");
		}

		private ArrayList<String> getNationItems() {
			Game g = GameHolder.instance().getGame();
			ArrayList<String> ret = new ArrayList<String>();
			for (PlayerInfo pi : g.getTurn().getPlayerInfo()) {
				ret.add(g.getMetadata().getNationByNum(pi.getNationNo()).getName());
			}
			return ret;
		}

		private int getSelectedNationNo() {
			String nationName = nation.getSelectedItem().toString();
			Game g = GameHolder.instance().getGame();
			return g.getMetadata().getNationByName(nationName).getNumber();
		}

		@Override
		protected JComponent createFormControl() {
			Game g = GameHolder.instance().getGame();

			GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
			glb.append(new ResourceLabel("standardFields.Nation"));
			glb.append(nation = new JComboBox(getNationItems().toArray()));

			nation.setSelectedIndex(0);
			nation.setSelectedItem(g.getMetadata().getNationByNum(g.getMetadata().getNationNo()).getName());

			nation.setPreferredSize(new Dimension(100, 24));
			glb.nextLine();

			orders = new JTextArea();
			orders.setWrapStyleWord(false);
			orders.setLineWrap(false);
			JScrollPane scp = new JScrollPane(orders);
			scp.setPreferredSize(new Dimension(500, 400));
			glb.append(scp, 2, 1);

			glb.nextLine();
			JButton generate = new ResourceButton("exportOrderTextForm.BtnGenerate");
			glb.append(generate, 1, 1);
			glb.nextLine();
			generate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OrderTextGenerator gen = new OrderTextGenerator();
					Game g = GameHolder.instance().getGame();
					try {
						orders.setText(gen.generateOrderFile(g, g.getTurn(), getSelectedNationNo()));
						orders.setCaretPosition(0);
					} catch (Exception exc) {
						orders.setText(exc.getMessage());
					}
				}
			});

			JButton ctc = new ResourceButton("standardActions.CopyToClipboard");
			glb.append(ctc, 1, 1);
			glb.nextLine();
			final ClipboardOwner clipboardOwner = this;
			ctc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StringSelection stringSelection = new StringSelection(orders.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, clipboardOwner);
				}
			});

			return glb.getPanel();
		}

		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

	}

}
