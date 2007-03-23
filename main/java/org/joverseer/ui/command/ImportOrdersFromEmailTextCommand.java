package org.joverseer.ui.command;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderTextReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.InputApplicationDialog;
import org.springframework.richclient.dialog.MessageDialog;


public class ImportOrdersFromEmailTextCommand extends ActionCommand {
    public ImportOrdersFromEmailTextCommand() {
        super("importOrdersFromEmailTextCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final JTextArea orderText = new JTextArea();
        InputApplicationDialog dlg = new InputApplicationDialog() {
            protected boolean onFinish() {
                loadOrders(orderText.getText());
                return true;
            }
            
        };
        orderText.setPreferredSize(new Dimension(400, 500));
        JScrollPane scp = new JScrollPane(orderText);
        dlg.setInputField(scp);
        dlg.setInputLabelMessage("Order text :");
        dlg.setTitle("Import Orders from Text");
        
        dlg.showDialog();
        
    }
    
    private void loadOrders(String text) {
        OrderTextReader orderTextReader = new OrderTextReader();
        orderTextReader.setGame(GameHolder.instance().getGame());
        orderTextReader.setOrderText(text);
        orderTextReader.readOrders();
        MessageDialog dialog = new MessageDialog("Import Orders", "Orders for " + orderTextReader.getChars() + "  characters were imported.");
        dialog.showDialog();
        Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), GameHolder.instance().getGame(), this));

    }

}
