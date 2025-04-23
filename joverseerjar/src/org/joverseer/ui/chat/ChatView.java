package org.joverseer.ui.chat;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;
import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
//import net.sf.jml.MsnContact;
//import net.sf.jml.MsnMessenger;
//import net.sf.jml.MsnObject;
//import net.sf.jml.MsnSwitchboard;
//import net.sf.jml.MsnUserStatus;
//import net.sf.jml.event.MsnAdapter;
//import net.sf.jml.event.MsnMessageListener;
//import net.sf.jml.impl.MsnMessengerFactory;
//import net.sf.jml.message.MsnControlMessage;
//import net.sf.jml.message.MsnDatacastMessage;
//import net.sf.jml.message.MsnInstantMessage;
//import net.sf.jml.message.MsnMimeMessage;
//import net.sf.jml.message.MsnSystemMessage;
//import net.sf.jml.message.MsnUnknownMessage;



public class ChatView extends AbstractView implements ApplicationListener {
    //JTextArea text;

    Thread chatThread;

    JTextPane text;
    JTextField message;
    StyledDocument doc;

    ServerSocket serverSocket;
    Socket clientSocket;

    boolean connected = false;
    //MsnMessenger messenger;
    static Logger log = Logger.getLogger(ChatView.class);
    //dependencies
    GameHolder gameHolder;

    protected void setMessageEnabled(boolean v) {
        this.message.setEnabled(v);
    }

//    protected void initMessenger(MsnMessenger messenger) {
//		messenger.getOwner().setInitStatus(MsnUserStatus.ONLINE);
//		messenger.addListener(new PrettyMsnListener(this));
//	}

    @Override
	protected JComponent createControl() {
    	//final ChatView cv = this;

        TableLayoutBuilder tlb = new TableLayoutBuilder();


//        text = new JTextArea();
//        text.setWrapStyleWord(true);
//        text.setLineWrap(true);
        this.text = new JTextPane();
        this.doc = this.text.getStyledDocument();

        Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = this.doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        Style s = this.doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

        JScrollPane scp = new JScrollPane(this.text);
        scp.setPreferredSize(new Dimension(400, 100));
        tlb.cell(scp, "align=left rowSpec=fill:default:grow colspec=left:410px");
        tlb.gapCol();


        TableLayoutBuilder lb = new TableLayoutBuilder();
        JButton startChat = new JButton("C");
        lb.cell(startChat);
        startChat.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                if (ChatView.this.connected) {
                    ErrorDialog dlg = new ErrorDialog("Already connected. You must disconnect first.");
                    dlg.showDialog();
                    return;
                }
                final ChatConnection conn = new ChatConnection();

                final ConnectToChatServerForm frm = new ConnectToChatServerForm(FormModelHelper.createFormModel(conn));

                FormBackedDialogPage page = new FormBackedDialogPage(frm);

                CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
//                    protected void onAboutToShow() {
//                    }

                    @Override
					protected boolean onFinish() {
                        frm.commit();
                        try {
//                        	 messenger = MsnMessengerFactory.createMsnMessenger(conn.getUsername(),
//                        				conn.getPassword());
//                        	 initMessenger(messenger);
//                        	 messenger.login();
//                        	 setMessageEnabled(true);
                        }
                        catch (Exception exc) {
                            setMessageEnabled(false);
                            ErrorDialog.showErrorDialog(exc);
                        }
                        return true;
                    }
                };
                dialog.setTitle(Messages.getString("editNoteDialog.title"));
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
        lb.relatedGapRow();
        JButton disconnect = new JButton("D");
        lb.cell(disconnect);
        disconnect.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	disconnect();
            }
        });



        tlb.cell(lb.getPanel(),"align=left");
        tlb.relatedGapRow();
        tlb.relatedGapRow();

        this.message = new JTextField();
        this.message.setPreferredSize(new Dimension(400, 20));
        this.message.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
//            	String str = message.getText();
//            	MsnInstantMessage reply = new MsnInstantMessage();
//                reply.setContent(str);
//                messenger.getActiveSwitchboards()[0].sendMessage(reply);
//                addMsg("you: " + str);
//                message.setText("");
            }
        });
        setMessageEnabled(false);
        tlb.cell(this.message, "align=left");
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
        final Character c = (Character)this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c == null) return null;
        ImageSource imgSource = JOApplication.getImageSource();
        Icon ico = new ImageIcon(imgSource.getImage("acceptOrder.icon"));
        button.setIcon(ico);
        button.setPreferredSize(new Dimension(16, 10));
        button.setToolTipText("Accept order");
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                Order o = c.getOrders()[ow.getOrderIdx()];
                o.setOrderNo(ow.getOrderNo());
                o.setParameters(ow.getParameters());
                JOApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, o, this);
            }
        });

        return button;
    }

    public JButton getSelectCharButtonForOrderWrapper(OrderWrapper orderWrapper) {
        final OrderWrapper ow = orderWrapper;
        final JButton button = new JButton();
        final Character c = (Character)this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c == null) return null;
        ImageSource imgSource = JOApplication.getImageSource();
        Icon ico = new ImageIcon(imgSource.getImage("selectHexCommand.icon"));
        button.setIcon(ico);
        button.setPreferredSize(new Dimension(16, 16));
        button.setToolTipText("Find char on map");
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, new Point(c.getX(), c.getY()), this);
            }
        });

        return button;
    }

    public void orderWrapperReceived(OrderWrapper orderWrapper, String username) {
        String msgStr;
        final OrderWrapper ow = orderWrapper;
        final Character c = (Character)this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", ow.getCharId());
        if (c != null) {
            if (c.getHexNo() != ow.getHexNo()) {
                msgStr = username + ": Order for '" + ow.getCharId() + "' but character was found at a different location.";
                addMsg(msgStr);
                return;
            }
            msgStr = username + "> Order for '" + ow.getCharId() + "' " + ow.getOrderNo() + " " + ow.getParameters().replace(Order.DELIM, " ") + ".";
            addMsg(msgStr);

            JButton btn = getAcceptButtonForOrderWrapper(ow);
            Style s= this.doc.addStyle("button", this.doc.getStyle("regular"));
            StyleConstants.setComponent(s, btn);
            final Style ns = s;
                try {
					this.doc.insertString(this.doc.getLength(), " ", ns);
	                this.text.setCaretPosition(this.doc.getLength()-1);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//            btn = getSelectCharButtonForOrderWrapper(ow);
//            StyleConstants.setComponent(s, btn);
//            try {
//                doc.insertString(doc.getLength(), " ", doc.getStyle("regular"));
//                doc.insertString(doc.getLength(), " ", ns);
//                text.setCaretPosition(doc.getLength()-1);
//            }
//            catch (Exception exc) {};
        } else {
            msgStr = username + "> order for character " + ow.getCharId() + " but character was not found.";
            addMsg(msgStr);
        }
    }


    public void showObject(Object obj) {
        String msgStr = "";
        if (OrderWrapper.class.isInstance(obj)) {
            orderWrapperReceived((OrderWrapper)obj, "");
        } else if (MultiOrderWrapper.class.isInstance(obj)) {
            final MultiOrderWrapper mow = (MultiOrderWrapper)obj;
            addMsg("> " + "sent group of orders.");
            for (OrderWrapper ow : mow.getOrderWrappers()) {
                orderWrapperReceived(ow, "");
            }
            addMsg("");
            //addMsg(msg.getUser().getUsername() + ": " + "Accept all orders.");
            JButton btn = new JButton("Accept all orders in this group");
            btn.setCursor(Cursor.getDefaultCursor());
            btn.setMargin(new Insets(0,0,0,0));
            btn.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    for (OrderWrapper ow : mow.getOrderWrappers()) {
                        JButton b = getAcceptButtonForOrderWrapper(ow);
                        if (b == null) continue;
                        b.doClick();
                    }
                }
            });
            Style s= this.doc.getStyle("button");
            StyleConstants.setComponent(s, btn);
            final Style ns = s;
            try {
            	this.doc.insertString(this.doc.getLength(), " ", ns);
                this.text.setCaretPosition(this.doc.getLength()-1);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            addMsg("");

        }
        else {
            msgStr = "> " + obj.toString();
            addMsg(msgStr);
        }
    }

    public void messageReceived(String msg) {
        addMsg( msg);
        //text.setCaretPosition(text.getText().length());
    }

    private void addMsg(String msg) {
        try {
            final String m = msg;
            try {
                this.doc.insertString(this.doc.getLength(), "\n" + m, this.doc.getStyle("regular"));
                this.text.setCaretPosition(this.doc.getLength()-1);
            }
            catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void sendOrder(Order o) {
        //client.sendMessage(new OrderWrapper(o));
//        OrderWrapper ow = new OrderWrapper(o);
//        String str = "o: ";
//        MsnInstantMessage reply = new MsnInstantMessage();
//        str += ow.charId;
//        str += "##" + ow.getHexNo();
//        str += "##" + ow.orderIdx;
//        str += "##" + ow.orderNo;
//        str += "##" + ow.parameters;
//
//        reply.setContent(str);
//        messenger.getActiveSwitchboards()[0].sendMessage(reply);
//        addMsg("you: " + str);
        this.message.setText("");
    }

    public void sendOrders(ArrayList<Order> os) {
        MultiOrderWrapper mow = new MultiOrderWrapper();
        for (Order o : os) {
            mow.getOrderWrappers().add(new OrderWrapper(o));
        }
//        String str = "o: ";
//        OrderWrapper ow = mow.getOrderWrappers().get(0);
//        MsnInstantMessage reply = new MsnInstantMessage();
//        str += ow.charId;
//        str += "##" + ow.getHexNo();
//        str += "##" + ow.orderIdx;
//        str += "##" + ow.orderNo;
//        str += "##" + ow.parameters;
//
//        reply.setContent(str);
//        messenger.getActiveSwitchboards()[0].sendMessage(reply);
//        addMsg("you: " + str);
    }

    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.isLifecycleEvent(LifecycleEventsEnum.SendOrdersByChat)) {
                if (Order.class.isInstance(e.getObject())) {
                    sendOrder((Order)e.getObject());
                } else if (ArrayList.class.isInstance(e.getObject())) {
                    sendOrders((ArrayList<Order>)e.getObject());
                }

            }
        }
    }

    protected void disconnect() {
    	this.connected = false;
        setMessageEnabled(false);
        messageReceived("Disconnected from server.");
    }


//    private static class PrettyMsnListener extends MsnAdapter {
//
//    	ChatView chatView;
//
//
//        public PrettyMsnListener(ChatView chatView) {
//			super();
//			this.chatView = chatView;
//		}
//
//		public void exceptionCaught(MsnMessenger messenger, Throwable throwable) {
//            log.error(messenger + throwable.toString(), throwable);
//        }
//
//        public void loginCompleted(MsnMessenger messenger) {
//            log.info(messenger + " login complete ");
//            chatView.addMsg("login complete");
//        }
//
//        public void logout(MsnMessenger messenger) {
//            log.info(messenger + " logout");
//        }
//
//        public void instantMessageReceived(MsnSwitchboard switchboard,
//                                           MsnInstantMessage message,
//                                           MsnContact friend) {
//
//        	String str = message.getContent();
//        	chatView.addMsg(friend.getFriendlyName() +": " + str);
//        	if (str.startsWith("o: ")) {
//        		OrderWrapper ow = new OrderWrapper();
//        		try {
//	        		str = str.substring(3);
//	        		String[] ps = str.split("##");
//	        		ow.charId = ps[0];
//	        		ow.hexNo = Integer.parseInt(ps[1]);
//	        		ow.orderIdx = Integer.parseInt(ps[2]);
//	        		ow.orderNo = Integer.parseInt(ps[3]);
//	        		ow.parameters = ps[4];
//	        		chatView.orderWrapperReceived(ow, friend.getFriendlyName());
//        		}
//        		catch (Exception exc) {
//        			chatView.addMsg("Failed to parse order. Error: "+ exc.getMessage());
//        		}
//        	}
//
//        }
//
//        public void systemMessageReceived(MsnMessenger messenger,
//                                          MsnSystemMessage message) {
//            log.info(messenger + " recv system message " + message);
//        }
//
//        public void controlMessageReceived(MsnSwitchboard switchboard,
//                                           MsnControlMessage message,
//                                           MsnContact contact) {
//            log.info(switchboard + " recv control message from "
//                     + contact.getEmail());
//            message.setTypingUser(switchboard.getMessenger().getOwner().getEmail().getEmailAddress());
//            switchboard.sendMessage(message, false);
//        }
//
//        public void datacastMessageReceived(MsnSwitchboard switchboard,
//                                            MsnDatacastMessage message,
//                                            MsnContact friend) {
//            log.info(switchboard + " recv datacast message " + message);
//            switchboard.sendMessage(message, false);
//        }
//
//        public void unknownMessageReceived(MsnSwitchboard switchboard,
//                                           MsnUnknownMessage message,
//                                           MsnContact friend) {
//            log.info(switchboard + " recv unknown message " + message);
//        }
//
//        public void contactListInitCompleted(MsnMessenger messenger) {
//            log.info(messenger + " contact list init completeted");
//        }
//
//        public void contactListSyncCompleted(MsnMessenger messenger) {
//            log.info(messenger + " contact list sync completed");
//        }
//
//        public void contactStatusChanged(MsnMessenger messenger,
//                                         MsnContact friend) {
//            log.info(messenger + " friend " + friend.getEmail()
//                     + " status changed from " + friend.getOldStatus() + " to "
//                     + friend.getStatus());
//        }
//
//        public void ownerStatusChanged(MsnMessenger messenger) {
//            log.info(messenger + " status changed from "
//                     + messenger.getOwner().getOldStatus() + " to "
//                     + messenger.getOwner().getStatus());
//        }
//
//        public void contactAddedMe(MsnMessenger messenger, MsnContact friend) {
//            log.info(friend.getEmail() + " add " + messenger);
//        }
//
//        public void contactRemovedMe(MsnMessenger messenger, MsnContact friend) {
//            log.info(friend.getEmail() + " remove " + messenger);
//        }
//
//        public void switchboardClosed(MsnSwitchboard switchboard) {
//            log.info(switchboard + " closed");
//        }
//
//        public void switchboardStarted(MsnSwitchboard switchboard) {
//            log.info(switchboard + " started");
//        }
//
//        public void contactJoinSwitchboard(MsnSwitchboard switchboard,
//                                           MsnContact friend) {
//            log.info(friend.getEmail() + " join " + switchboard);
//        }
//
//        public void contactLeaveSwitchboard(MsnSwitchboard switchboard,
//                                            MsnContact friend) {
//            log.info(friend.getEmail() + " leave " + switchboard);
//        }
//	}
}
