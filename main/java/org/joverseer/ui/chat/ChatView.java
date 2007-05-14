package org.joverseer.ui.chat;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.joverseer.chat.domain.Message;
import org.joverseer.chat.domain.MessageTypeEnum;
import org.joverseer.chat.domain.User;
import org.joverseer.chat.support.ChatClient;
import org.joverseer.chat.support.ChatServer;
import org.joverseer.chat.support.MessageReceiver;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class ChatView extends AbstractView implements MessageReceiver, ApplicationListener {
    //JTextArea text;
    JTextPane text;
    JTextField message;
    ChatClient client;
    StyledDocument doc;
    
    protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        
        MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");

//        text = new JTextArea();
//        text.setWrapStyleWord(true);
//        text.setLineWrap(true);
        text = new JTextPane();
        doc = text.getStyledDocument();
        
        Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        Style s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

        JScrollPane scp = new JScrollPane(text);
        scp.setPreferredSize(new Dimension(400, 100));
        tlb.cell(scp, "align=left rowSpec=fill:default:grow");
        tlb.gapCol();
        JButton connect = new JButton("C");
        tlb.cell(connect,"align=left");
        tlb.gapCol();
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ChatConnection conn = new ChatConnection();
                conn.setPort(9600);
                final ConnectToChatServerForm frm = new ConnectToChatServerForm(FormModelHelper.createFormModel(conn));
                
                FormBackedDialogPage page = new FormBackedDialogPage(frm);

                TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                    protected void onAboutToShow() {
                    }

                    protected boolean onFinish() {
                        frm.commit();
                        try {
                            messageReceived("Connecting to server...");
                            ChatClient cc = ChatClient.connect(conn.getServer(), conn.getPort(), new User(conn.getUsername()));
                            setClient(cc);
                            messageReceived("Connected!");
                        }
                        catch (Exception exc) {
                            ErrorDialog d = new ErrorDialog(exc);
                            d.showDialog();
                        }
                        return true;
                    }
                };
                MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
                dialog.setTitle(ms.getMessage("editNoteDialog.title", new Object[]{""}, Locale.getDefault()));
                dialog.setModal(false);
                dialog.showDialog();
                
            }
        });
        
        JButton startServer = new JButton("S");
        tlb.cell(startServer,"align=left");
        tlb.relatedGapRow();
        startServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ChatServer cs = new ChatServer();
                    Thread t = new Thread(cs);
                    t.start();
                    messageReceived("Server started!");
                }
                catch (Exception exc) {
                    ErrorDialog d = new ErrorDialog(exc);
                    d.showDialog();
                }
            }
        });
        
        tlb.relatedGapRow();
        
        message = new JTextField();
        message.setPreferredSize(new Dimension(400, 20));
        message.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(message.getText());
                message.setText("");
            }
        });
        tlb.cell(message, "align=left");
//        tlb.gapCol();
//        JButton send = new JButton("Send");
//        tlb.cell(send);
//        send.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                client.sendMessage(message.getText());
//                message.setText("");
//            }
//        });
        tlb.relatedGapRow();
        
        
        
        return tlb.getPanel();
    }

    public void messageReceived(Message msg) {
        if (msg == null) return;
        String msgStr = "";
        if (msg.getType() == MessageTypeEnum.Order) {
            String orderText = msg.getContents();
            String[] parts = orderText.split("!");
            String charId = parts[0];
            final int orderIdx = Integer.parseInt(parts[1]);
            final int orderNo = Integer.parseInt(parts[2]);
            String orderParams = "";
            if (parts.length == 4) {
                orderParams = parts[3]; 
            }
            final Character c = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charId);
            if (c != null) {
                final Order no = new Order(c);
                no.setOrderNo(orderNo);
                no.setParameters(orderParams);
                msgStr = msg.getUser().getUsername() + ": Order for '" + charId + "' " + no.getNoAndCode() + " " + no.getParameters().replace(Order.DELIM, " ") + ".";
                addMsg(msgStr);
                
                Style s= doc.getStyle("regular"); 
                s = doc.getStyle("button");
                final JButton button = new JButton();
                
                ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
                Icon ico = new ImageIcon(imgSource.getImage("acceptOrder.icon"));
                button.setIcon(ico);
                button.setPreferredSize(new Dimension(16, 16));
                button.setToolTipText("Accept order");
                button.setCursor(Cursor.getDefaultCursor());
                button.setMargin(new Insets(0,0,0,0));
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Order o = c.getOrders()[orderIdx];
                        o.setOrderNo(no.getOrderNo());
                        o.setParameters(no.getParameters());
                        Application.instance().getApplicationContext().publishEvent(
                              new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));
                    }
                });
                StyleConstants.setComponent(s, button);
                try {
                    doc.insertString(doc.getLength(), " ", s);
                }
                catch (Exception exc) {};
                
            } else {
                msgStr = msg.getUser().getUsername() + ": order for character " + charId + " but character was not found.";
                addMsg(msgStr);
            }
        } else {
            msgStr = msg.getUser().getUsername() + ": " + msg.getContents();
            addMsg(msgStr);
        }
        //text.setCaretPosition(text.getText().length());
    }

    public void messageReceived(String msg) {
        addMsg( msg);
        //text.setCaretPosition(text.getText().length());
    }

    public ChatClient getClient() {
        return client;
    }

    private void addMsg(String msg) {
        try {
            doc.insertString(doc.getLength(), "\n" + msg, doc.getStyle("regular"));
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
    public void setClient(ChatClient client) {
        this.client = client;
        this.client.addMessageReceiver(this);
    }

    public void sendOrder(Order o) {
        String msg = o.getCharacter().getId() + "!" + 
                        (o.getCharacter().getOrders()[0] == o ? "0" : "1") + "!" +
                                o.getOrderNo() + "!" +
                                o.getParameters();
        client.sendMessage(msg, MessageTypeEnum.Order);                        
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SendOrderByChat.toString())) {
                sendOrder((Order)e.getObject());
            } 
        }
    }
    

}
