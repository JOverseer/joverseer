package org.joverseer.ui.chat;

import java.awt.AWTPermission;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.joverseer.chat.domain.Message;
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
import org.litesoft.p2pchat.ActivePeerManager;
import org.litesoft.p2pchat.MyInfo;
import org.litesoft.p2pchat.P2PChatAWT;
import org.litesoft.p2pchat.PeerInfo;
import org.litesoft.p2pchat.PendingPeerManager;
import org.litesoft.p2pchat.UserDialog;
import org.scopemvc.view.swing.SwingUtil;
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


public class ChatView extends AbstractView implements MessageReceiver, ApplicationListener, UserDialog {
    //JTextArea text;
    private MyInfo zMyInfo;
    private ActivePeerManager zActivePeerManager = null;
    private PendingPeerManager zPendingPeerManager = null;
    private List zPeersList;
    
    JTextPane text;
    JTextField message;
    StyledDocument doc;
    
    ChatClient client;
    ChatServer server;
    
    UserDialog chatDialog;
    
    protected void setMessageEnabled(boolean v) {
        message.setEnabled(v);
    }
    
    protected JComponent createControl() {
        chatDialog = this;
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
                if (client != null) {
                    ErrorDialog dlg = new ErrorDialog("Already connected. You must disconnect first.");
                    dlg.showDialog();
                    return;
                }
                final ChatConnection conn = new ChatConnection();
                conn.setMyIP("127.0.0.1");
                conn.setMyPort(9600);
                final ConnectToChatServerForm frm = new ConnectToChatServerForm(FormModelHelper.createFormModel(conn));
                
                FormBackedDialogPage page = new FormBackedDialogPage(frm);

                TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                    protected void onAboutToShow() {
                    }

                    protected boolean onFinish() {
                        frm.commit();
                        try {
                            messageReceived("Connecting to server " + conn.getMyIP() + ":" + conn.getMyPort() + " as " + conn.getUsername());
                            client = ChatClient.connect(conn.getMyIP(), conn.getMyPort(), new User(conn.getUsername()));
                            setClient(client);
                            messageReceived("Connected!"); 
                            setMessageEnabled(true);
                        }
                        catch (Exception exc) {
                            setMessageEnabled(false);
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
                if (client != null) {
                    ErrorDialog dlg = new ErrorDialog("Already connected as a client. You must disconnect first.");
                    dlg.showDialog();
                    return;
                }
                if (server != null) {
                    ErrorDialog dlg = new ErrorDialog("Already running a server. You must close the server first.");
                    dlg.showDialog();
                    return;
                }
                final ChatConnection conn = new ChatConnection();
                conn.setMyIP("127.0.0.1");
                conn.setMyPort(9600);
                conn.setPeerPort(9600);
                final ConnectToChatServerForm frm = new ConnectToChatServerForm(FormModelHelper.createFormModel(conn));
                
                FormBackedDialogPage page = new FormBackedDialogPage(frm);

                TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                    protected void onAboutToShow() {
                    }

                    protected boolean onFinish() {
                        frm.commit();
                        try {
//                            server = new ChatServer(conn.getPort());
//                            Thread t = new Thread(server);
//                            t.start();
//                            messageReceived("Server started - listening to port " + server.getPort() + ".");
//                            client = ChatClient.connect("localhost", conn.getPort(), new User(conn.getUsername()));
//                            setClient(client);
//                            String server = conn.getServer();
//                            messageReceived("Connected to " + conn.getServer() + ":" + conn.getPort() + " as " + conn.getUsername()); 
//                            setMessageEnabled(true);
                            final P2PChatAWT chat = new P2PChatAWT() {
                                protected UserDialog getUserDialog(MyInfo pMyInfo) {
                                    zMyInfo = pMyInfo;
                                    return chatDialog;
                                }
                            };
                            Runnable r = new Runnable() {
                                public void run() {
                                    String[] args = new String[]{
                                            conn.getUsername(),
                                            conn.getPeerIP() + ":" + conn.getPeerPort(),
                                            "[" + conn.getMyIP() + ":" + conn.getMyPort() + "]"
                                            };
                                    chat.init(args);
                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                            setMessageEnabled(true);
                        }
                        catch (Exception exc) {
                            setMessageEnabled(false);
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
//                try {
//                    ChatServer cs = new ChatServer();
//                    Thread t = new Thread(cs);
//                    t.start();
//                    messageReceived("Server started!");
//                }
//                catch (Exception exc) {
//                    ErrorDialog d = new ErrorDialog(exc);
//                    d.showDialog();
//                }
            }
        });
        
        tlb.relatedGapRow();
        
        message = new JTextField();
        message.setPreferredSize(new Dimension(400, 20));
        message.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                if (client == null) {
//                    ErrorDialog dlg = new ErrorDialog("Not connected.");
//                    dlg.showDialog();
//                    setMessageEnabled(false);
//                    return;
//                }
//                if (!client.sendMessage(message.getText())) {
//                    ErrorDialog dlg = new ErrorDialog("Unexpected error sending message. Disconnecting...");
//                    dlg.showDialog();
//                    disconnect();
//                    return;
//                } else {
//                    message.setText("");
//                }
                zActivePeerManager.sendToAllCHAT( message.getText() );
                showCHAT(zMyInfo, message.getText());
                message.setText("");
            }
        });
        setMessageEnabled(false);
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
    
    public JButton getAcceptButtonForOrderWrapper(OrderWrapper orderWrapper) {
        final OrderWrapper ow = orderWrapper;
        final JButton button = new JButton();
        final Character c = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c == null) return null;
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("acceptOrder.icon"));
        button.setIcon(ico);
        button.setPreferredSize(new Dimension(16, 16));
        button.setToolTipText("Accept order");
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Order o = c.getOrders()[ow.getOrderIdx()];
                o.setOrderNo(ow.getOrderNo());
                o.setParameters(ow.getParameters());
                Application.instance().getApplicationContext().publishEvent(
                      new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));
            }
        });
        
        return button;
    }
    
    public JButton getSelectCharButtonForOrderWrapper(OrderWrapper orderWrapper) {
        final OrderWrapper ow = orderWrapper;
        final JButton button = new JButton();
        final Character c = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c == null) return null;
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("selectHexCommand.icon"));
        button.setIcon(ico);
        button.setPreferredSize(new Dimension(16, 16));
        button.setToolTipText("Find char on map");
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Application.instance().getApplicationContext().publishEvent(
                      new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), new Point(c.getX(), c.getY()), this));
            }
        });
        
        return button;
    }
    
    public void orderWrapperReceived(OrderWrapper orderWrapper, String username) {
        String msgStr;
        final OrderWrapper ow = orderWrapper;
        final Character c = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c != null) {
            if (c.getHexNo() != ow.getHexNo()) {
                msgStr = username + ": Order for '" + ow.getCharId() + "' but character was found at a different location.";
                addMsg(msgStr);
                return;
            }
            msgStr = username + "> Order for '" + ow.getCharId() + "' " + ow.getOrderNo() + " " + ow.getParameters().replace(Order.DELIM, " ") + ".";
            addMsg(msgStr);
            
            JButton btn = getAcceptButtonForOrderWrapper(ow);
            Style s= doc.getStyle("button");
            StyleConstants.setComponent(s, btn);
            final Style ns = s;
            try {
                doc.insertString(doc.getLength(), " ", ns);
                text.setCaretPosition(doc.getLength()-1);
            }
            catch (Exception exc) {};
            btn = getSelectCharButtonForOrderWrapper(ow);
            StyleConstants.setComponent(s, btn);
            try {
                doc.insertString(doc.getLength(), " ", doc.getStyle("regular"));
                doc.insertString(doc.getLength(), " ", ns);
                text.setCaretPosition(doc.getLength()-1);
            }
            catch (Exception exc) {};
        } else {
            msgStr = username + "> order for character " + ow.getCharId() + " but character was not found.";
            addMsg(msgStr);
        }
    }

    public void messageReceived(Message msg) {
        if (msg == null) return;
        String msgStr = "";
    }
    
    public void showObject(PeerInfo peerInfo, Object obj) {
        String msgStr = "";
        if (OrderWrapper.class.isInstance(obj)) {
            orderWrapperReceived((OrderWrapper)obj, peerInfo.getChatName());
        } else if (MultiOrderWrapper.class.isInstance(obj)) {
            final MultiOrderWrapper mow = (MultiOrderWrapper)obj;
            addMsg(peerInfo.getChatName() + "> " + "sent group of orders.");
            for (OrderWrapper ow : mow.getOrderWrappers()) {
                orderWrapperReceived(ow, peerInfo.getChatName());
            }
            addMsg("");
            //addMsg(msg.getUser().getUsername() + ": " + "Accept all orders.");
            JButton btn = new JButton("Accept all orders in this group");
            btn.setCursor(Cursor.getDefaultCursor());
            btn.setMargin(new Insets(0,0,0,0));
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (OrderWrapper ow : mow.getOrderWrappers()) {
                        JButton b = getAcceptButtonForOrderWrapper(ow);  
                        if (b == null) continue;
                        b.doClick();
                    }
                }
            });
            Style s= doc.getStyle("button");
            StyleConstants.setComponent(s, btn);
            final Style ns = s;
            try {
                doc.insertString(doc.getLength(), " ", ns);
                text.setCaretPosition(doc.getLength()-1);
            }
            catch (Exception exc) {};
            addMsg("");

        }
        else {
            msgStr = peerInfo.getChatName() + "> " + obj.toString();
            addMsg(msgStr);
        }
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
            final String m = msg;
            try {
                doc.insertString(doc.getLength(), "\n" + m, doc.getStyle("regular"));
                text.setCaretPosition(doc.getLength()-1);
            }
            catch (Exception exc) {
                exc.printStackTrace();
            }
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
        //client.sendMessage(new OrderWrapper(o));
        OrderWrapper ow = new OrderWrapper(o);
        zActivePeerManager.sendToAllObject( ow );
        
        showObject(zMyInfo, ow);
        message.setText("");
    }
    
    public void sendOrders(ArrayList<Order> os) {
        MultiOrderWrapper mow = new MultiOrderWrapper();
        for (Order o : os) {
            mow.getOrderWrappers().add(new OrderWrapper(o));
        }        
        //client.sendMessage(mow);
        zActivePeerManager.sendToAllObject( mow );
        
        showObject(zMyInfo, mow);
        message.setText("");
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SendOrdersByChat.toString())) {
                if (Order.class.isInstance(e.getObject())) {
                    sendOrder((Order)e.getObject());
                } else if (ArrayList.class.isInstance(e.getObject())) {
                    sendOrders((ArrayList<Order>)e.getObject());
                }
                
            } 
        }
    }
    
    protected void disconnect() {
        client = null;
        setMessageEnabled(false);
        messageReceived("Disconnected from server.");
    }

    public void setActivePeerManager( ActivePeerManager pActivePeerManager )
    {
        if ( pActivePeerManager != null )
            zActivePeerManager = pActivePeerManager;
    }

    public void setPendingPeerManager( PendingPeerManager pPendingPeerManager )
    {
        if ( pPendingPeerManager != null )
            zPendingPeerManager = pPendingPeerManager;
    }

    public void showCHAT(PeerInfo pPeerInfo, String pMessage) {
        String msg = pPeerInfo.getChatName() + "> " + pMessage;
        addMsg(msg);
    }

    public void showConnect(PeerInfo pPeerInfo) {
        String msg = pPeerInfo.getChatName() + " (" + pPeerInfo.getAddresses() + ") connected.";
        addMsg(msg);
    }

    public void showConnectFailed(PeerInfo pPeerInfo) {
        // TODO Auto-generated method stub
        
    }

    public void showDisconnect(PeerInfo pPeerInfo) {
        String msg = pPeerInfo.getChatName() + " (" + pPeerInfo.getAddresses() + ") disconnected.";
        addMsg(msg);
    }

    public void showHELO(PeerInfo pPeerInfo) {
        String msg = "HELO from " + pPeerInfo.getChatName() + " (" + pPeerInfo.getAddresses() + ").";
        addMsg(msg);
    }

    public void showNAME(PeerInfo pPeerInfo) {
        String msg = "Name from " + pPeerInfo.getChatName() + " (" + pPeerInfo.getAddresses() + ").";
        addMsg(msg);
    }

    public void showPMSG(PeerInfo pPeerInfo, String pMessage) {
        // TODO Auto-generated method stub
        
    }

    public void showStreamsFailed(PeerInfo pPeerInfo) {
        // TODO Auto-generated method stub
        
    }

    public void showUnrecognized(PeerInfo pPeerInfo, String pBadMessage) {
        // TODO Auto-generated method stub
        
    }
    
    
}
