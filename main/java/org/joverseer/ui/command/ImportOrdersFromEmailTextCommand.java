package org.joverseer.ui.command;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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


public class ImportOrdersFromEmailTextCommand extends ActionCommand {
    ParseOrdersForm form;
    
    public ImportOrdersFromEmailTextCommand() {
        super("importOrdersFromEmailTextCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        form = new ParseOrdersForm(FormModelHelper.createFormModel(new String()));
        FormBackedDialogPage pg = new FormBackedDialogPage(form);
        TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
            protected boolean onFinish() {
                loadOrders(form.getOrderText());
                return true;
            }

            protected Object[] getCommandGroupMembers() {
                return new Object[]{new ActionCommand("parseOrdersCommand") {
                    protected void doExecuteCommand() {
                        form.setParseResults(parseOrders(form.getOrderText()));
                    }
                }, getFinishCommand(), getCancelCommand()};
            }
            
        };
        dlg.setTitle("Import Orders from Text");
        dlg.showDialog();
    }
    
    private ArrayList<String> parseOrders(String text) {
        OrderTextReader orderTextReader = new OrderTextReader();
        orderTextReader.setGame(GameHolder.instance().getGame());
        orderTextReader.setOrderText(text);
        orderTextReader.readOrders(0);
        return orderTextReader.getLineResults();
    }
    
    private void loadOrders(String text) {
        OrderTextReader orderTextReader = new OrderTextReader();
        orderTextReader.setGame(GameHolder.instance().getGame());
        orderTextReader.setOrderText(text);
        orderTextReader.readOrders(1);
        MessageDialog dialog = new MessageDialog("Import Orders", "Orders for " + orderTextReader.getChars() + "  characters were imported.");
        dialog.showDialog();
        Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), GameHolder.instance().getGame(), this));

    }
    
    class ParseOrdersForm extends AbstractForm {
        JTextArea orderText;
        JTextArea parseResults;
        
        private ParseOrdersForm(FormModel arg0) {
            super(arg0, "parseOrdersForm");
        }

        protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            tlb.cell(new JLabel("Orders : "), "valign=top");
            tlb.gapCol();
            
            orderText = new JTextArea();
            JScrollPane scp = new JScrollPane(orderText);
            scp.setPreferredSize(new Dimension(300, 500));
            tlb.cell(scp);
            
            tlb.gapCol();
            parseResults = new JTextArea();
            parseResults.setEditable(false);
            scp = new JScrollPane(parseResults);
            scp.setPreferredSize(new Dimension(300, 500));
            tlb.cell(scp);
            
            return tlb.getPanel();
        }
        
        public void setParseResults(ArrayList<String> results) {
            String res = "";
            for (String r : results) {
                res += r + "\n";
            }
            parseResults.setText(res);
        }
        
        public String getOrderText() {
            return orderText.getText();
        }
        
    }
    
    

}