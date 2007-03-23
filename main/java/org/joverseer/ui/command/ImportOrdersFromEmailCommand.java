package org.joverseer.ui.command;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderFileReader;
import org.joverseer.support.readers.orders.OrderTextReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class ImportOrdersFromEmailCommand extends ActionCommand {
	boolean dialogResultOK = false;
	
    public ImportOrdersFromEmailCommand() {
        super("importOrdersFromEmailCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        String orderText = "";
        final ImportOrdersFromEmailForm form = new ImportOrdersFromEmailForm(FormModelHelper.createFormModel(orderText));
        FormBackedDialogPage pg = new FormBackedDialogPage(form);
        TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
			protected boolean onFinish() {
				form.commit();
				dialogResultOK = true;
				return false;
			}
        };
        dlg.showDialog();
        if (!dialogResultOK) return;
        orderText = (String)form.getFormObject();
        loadOrders(orderText);
    }
    
    private void loadOrders(String orderText) {
    	OrderTextReader reader = new OrderTextReader();
    	reader.setOrderText(orderText);
    	reader.setGame(GameHolder.instance().getGame());
    	reader.readOrders();
    }
    
    private class ImportOrdersFromEmailForm extends AbstractForm {
    	JTextArea orderText;

		public ImportOrdersFromEmailForm(FormModel arg0) {
			super(arg0, "importOrdersFromEmailForm");
		}

		protected JComponent createFormControl() {
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			tlb.cell(new JLabel("Orders :"), "valign=top");
			orderText = new JTextArea();
			orderText.setPreferredSize(new Dimension(500, 500));
			JScrollPane scp;
			tlb.cell(scp = new JScrollPane(orderText));
			scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			return tlb.getPanel();
		}

		public void commit() {
			super.commit();
			setFormObject(orderText.getText());
		}
		
		
    	
    }


}
