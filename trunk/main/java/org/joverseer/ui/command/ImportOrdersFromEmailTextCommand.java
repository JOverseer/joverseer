package org.joverseer.ui.command;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderTextReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Imports orders from text using the ParseOrdersForm form
 * 
 * @author Marios Skounakis
 */
public class ImportOrdersFromEmailTextCommand extends ActionCommand {
	ParseOrdersForm form;

	public ImportOrdersFromEmailTextCommand() {
		super("importOrdersFromEmailTextCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		form = new ParseOrdersForm(FormModelHelper.createFormModel(new String()));
		FormBackedDialogPage pg = new FormBackedDialogPage(form);
		final ClipboardOwner clipboardOwner = form;
		TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
			@Override
			protected boolean onFinish() {
				loadOrders(form.getOrderText(), form.getOrderTextType());
				return true;
			}

			@Override
			protected Object[] getCommandGroupMembers() {
				return new Object[] { new ActionCommand("copyFromClipboardCommand") {
					@Override
					protected void doExecuteCommand() {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						Transferable s = clipboard.getContents(clipboardOwner);
						try {
							form.setOrderText(s.getTransferData(DataFlavor.stringFlavor).toString());
						} catch (Exception e) {
							// do nothing
						}
					}
				}, new ActionCommand("parseOrdersCommand") {
					@Override
					protected void doExecuteCommand() {
						form.setParseResults(parseOrders(form.getOrderText(), form.getOrderTextType()));
					}
				}, getFinishCommand(), getCancelCommand() };
			}

		};
		dlg.setTitle("Import Orders from Text");
		dlg.showDialog();
	}

	private ArrayList<String> parseOrders(String text, String textType) {
		OrderTextReader orderTextReader = new OrderTextReader();
		if (textType.equals("Standard")) {
			orderTextReader.setTextType(OrderTextReader.STANDARD_ORDER_TEXT);
		} else if (textType.equals("Order Checker")) {
			orderTextReader.setTextType(OrderTextReader.ORDERCHECKER_ORDER_TEXT);
		}
		orderTextReader.setGame(GameHolder.instance().getGame());
		orderTextReader.setOrderText(text);
		orderTextReader.readOrders(0);
		return orderTextReader.getLineResults();
	}

	private void loadOrders(String text, String textType) {
		OrderTextReader orderTextReader = new OrderTextReader();
		if (textType.equals("Standard")) {
			orderTextReader.setTextType(OrderTextReader.STANDARD_ORDER_TEXT);
		} else if (textType.equals("Order Checker")) {
			orderTextReader.setTextType(OrderTextReader.ORDERCHECKER_ORDER_TEXT);
		}
		orderTextReader.setGame(GameHolder.instance().getGame());
		orderTextReader.setOrderText(text);
		orderTextReader.readOrders(1);
		MessageDialog dialog = new MessageDialog("Import Orders", orderTextReader.getOrders() + " orders were imported.");
		dialog.showDialog();
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), GameHolder.instance().getGame(), this));

	}

	class ParseOrdersForm extends AbstractForm implements ClipboardOwner {
		JTextArea orderText;
		JTextArea parseResults;
		JScrollPane orderScp;
		JScrollPane parseScp;
		JComboBox orderTextTypeCmb;

		private ParseOrdersForm(FormModel arg0) {
			super(arg0, "parseOrdersForm");
		}

		@Override
		protected JComponent createFormControl() {
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			tlb.cell(new JLabel("Orders : "), "colspec=left:60px valign=top");
			tlb.gapCol();

			orderText = new JTextArea();
			orderScp = new JScrollPane(orderText);
			orderScp.setPreferredSize(new Dimension(300, 500));
			orderScp.getViewport().addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					if (!parseResults.getText().equals("")) {
						parseScp.getViewport().setViewPosition(orderScp.getViewport().getViewPosition());
					}
				}

			});
			tlb.cell(orderScp);

			tlb.gapCol();
			parseResults = new JTextArea();
			parseResults.setEditable(false);
			parseScp = new JScrollPane(parseResults);
			parseScp.setPreferredSize(new Dimension(300, 500));
			parseScp.getViewport().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					orderScp.getViewport().setViewPosition(parseScp.getViewport().getViewPosition());
				}
			});
			tlb.cell(parseScp);
			tlb.relatedGapRow();

			orderTextTypeCmb = new JComboBox();
			orderTextTypeCmb.setPreferredSize(new Dimension(200, 20));
			orderTextTypeCmb.addItem("Standard");
			orderTextTypeCmb.addItem("Order Checker");
			tlb.cell(new JLabel("Type :"));
			tlb.gapCol();
			TableLayoutBuilder tlb2 = new TableLayoutBuilder();
			tlb2.cell(orderTextTypeCmb, "colspan=1 colspec=left:100px");
			tlb2.relatedGapRow();
			tlb.cell(tlb2.getPanel(), "colspan=1");
			tlb.row();

			return tlb.getPanel();
		}

		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

		public void setParseResults(ArrayList<String> results) {
			String res = "";
			for (String r : results) {
				res += r + "\n";
			}
			parseResults.setText(res);
			orderText.setCaretPosition(0);
			parseResults.setCaretPosition(0);
		}

		public String getOrderText() {
			return orderText.getText();
		}

		public void setOrderText(String text) {
			orderText.setText(text);
		}

		public String getOrderTextType() {
			return orderTextTypeCmb.getSelectedItem().toString();
		}
	}

}
