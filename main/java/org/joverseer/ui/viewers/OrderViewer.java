package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.joverseer.domain.Order;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dataFlavors.OrderDataFlavor;
import org.joverseer.ui.support.transferHandlers.OrderExportTransferHandler;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows orders
 * User by the Character viewer
 * 
 * @author Marios Skounakis
 */
public class OrderViewer extends ObjectViewer implements ActionListener {
    public static final String FORM_PAGE = "OrderViewer";

    JTextField orderText;
    JLabel orderResultIcon;
    JCheckBox draw;
    
    public OrderViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    public boolean appliesTo(Object obj) {
        return Order.class.isInstance(obj);
    }
    
    protected OrderResultTypeEnum addValidationResult(Order o, OrderValidationResult res, Integer paramI, OrderResultTypeEnum orderResultType, ArrayList<OrderResult> results) {
    	if (res == null) return orderResultType;
    	String e = "";
    	if (paramI != null) {
    		e = "Param " + paramI +": ";
    	}
    	e += res.getMessage();
    	if (res.getLevel() == OrderValidationResult.ERROR) {
    		OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Error);
    		results.add(or);
    		return OrderResultTypeEnum.Error;
    	} else if (res.getLevel() == OrderValidationResult.WARNING) {
    		OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Warning);
    		results.add(or);
    		if (orderResultType == null || orderResultType.getValue() < OrderResultTypeEnum.Warning.getValue()) {
    			return OrderResultTypeEnum.Warning;
    		}
    	} else if (res.getLevel() == OrderValidationResult.INFO) {
    		OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Info);
    		results.add(or);
    		if (orderResultType == null || orderResultType.getValue() < OrderResultTypeEnum.Info.getValue()) {
    			return OrderResultTypeEnum.Info;
    		}
    	}
    	return orderResultType;
    }
    
    public void setOrderValidationResults(Order o) {
    	OrderResultTypeEnum orderResultType = null;
    	Icon ico = null;
    	boolean joErrors = false;
    	ArrayList<OrderResult> results = new ArrayList<OrderResult>();
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
    	if (!o.isBlank()) {
	    	
	        OrderResultContainer container = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
	        orderResultType = container.getResultTypeForOrder(o);
	        if (orderResultType != null) {
	        	results = container.getResultsForOrder(o);
	        } else {
	        	// use parameter validator
	        	OrderParameterValidator opv = new OrderParameterValidator();
	        	OrderValidationResult ovr = opv.checkOrder(o);
	        	orderResultType = addValidationResult(o, ovr, null, orderResultType, results);
	        	for (int i=0; i<=o.getLastParamIndex(); i++) {
	        		ovr = opv.checkParam(o, i);
	        		if (ovr != null) {
	        			orderResultType = addValidationResult(o, ovr, i, orderResultType, results);
	        		}
	        	}
	        	joErrors = true;
	        }
    	}
    	if (orderResultType == null) {
            ico = null;
        } else if (orderResultType == OrderResultTypeEnum.Info) {
            ico = new ImageIcon(imgSource.getImage("orderresult.info.icon"));
        } else if (orderResultType == OrderResultTypeEnum.Help) {
            ico = new ImageIcon(imgSource.getImage("orderresult.help.icon"));
        } else if (orderResultType == OrderResultTypeEnum.Warning) {
            ico = new ImageIcon(imgSource.getImage("orderresult.warn.icon"));
        } else if (orderResultType == OrderResultTypeEnum.Error) {
            ico = new ImageIcon(imgSource.getImage("orderresult.error.icon"));
        } else if (orderResultType == OrderResultTypeEnum.Okay) {
            ico = new ImageIcon(imgSource.getImage("orderresult.okay.icon"));
        } 
        orderResultIcon.setIcon(ico);
        if (ico != null) {
            String txt = "";
            for (OrderResult result : results) {
                String resText = null;
                resText = result.getType().toString();
                txt += (txt.equals("") ? "" : "") + "<li>" + resText + (joErrors ? " [JO]" : "") + ": " + result.getMessage() + "</li>";
            }
            if (!txt.equals("")) {
                txt = "<html><body><lu>" + txt + "</lu></body></html>";
            } else {
                txt = null;
            }
            orderResultIcon.setToolTipText(txt);
        } else {
            orderResultIcon.setToolTipText(null);
        }
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);
        Order o = (Order)object;
        if (o.getOrderNo() <= 0) {
            orderText.setText("N/A");
        } else {
            orderText.setText(o.getNoAndCode() + " " + Order.getParametersAsString(o.getParameters()));
            orderText.setCaretPosition(0);
        }
        orderText.setTransferHandler(new OrderExportTransferHandler(o));
        
        setOrderValidationResults(o);
        
        OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
        draw.setSelected(ovd.contains(o));
        if (GraphicUtils.canRenderOrder(o)) {
        	draw.setEnabled(true);
        } else {
        	draw.setEnabled(false);
        	draw.setSelected(false);
        }
        
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(1, 1, 1, 3));
        glb.append(orderText = new JTextField());
        orderText.setBorder(null);
        orderText.setPreferredSize(new Dimension(170, 16));
        orderText.setText("N/A");

        orderText.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TransferHandler handler = orderText.getTransferHandler();
                handler.exportAsDrag(orderText, e, TransferHandler.COPY);
            }
        });
        
        orderText.setDropTarget(new DropTarget(orderText, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable t = dtde.getTransferable();
                    if (dtde.isDataFlavorSupported(new OrderDataFlavor())) {
                        Order no = (Order)t.getTransferData(new OrderDataFlavor());
                        Order o = (Order)getFormObject();

                        // swap characters
                        org.joverseer.domain.Character c = o.getCharacter();
                        o.setCharacter(no.getCharacter());
                        no.setCharacter(c);
                        
                        // set order no to character c
                        for (int i=0; i<c.getNumberOfOrders(); i++) {
                        	if (c.getOrders()[i] == o) {
                        		c.getOrders()[i] = no;
                        	}
                        }
                        
                        // set order o to character o.getCharacter
                        for (int i=0; i<o.getCharacter().getNumberOfOrders(); i++) {
                        	if (o.getCharacter().getOrders()[i] == no) {
                        		o.getCharacter().getOrders()[i] = o;
                        	}
                        }
                        
                        Application.instance().getApplicationContext().publishEvent(
                                new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));

                        Application.instance().getApplicationContext().publishEvent(
                                new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), no, this));
                    }
                }
                catch (Exception exc) {
                }
            }
        }));

        orderResultIcon = new JLabel("");
        orderResultIcon.setPreferredSize(new Dimension(16, 16));
        glb.append(orderResultIcon);
        
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("edit.image"));
        final JButton btn = new JButton(ico);
        btn.setToolTipText("Edit order");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Order order = (Order)getFormObject();
                Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
            }
        });
        
        btn.setPreferredSize(new Dimension(16, 16));
        glb.append(btn);

//        imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
//        ico = new ImageIcon(imgSource.getImage("selectHexCommand.icon"));
        draw = new JCheckBox();
        draw.setToolTipText("Draw order");
        draw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
                if (draw.isSelected()) {
                    ovd.addOrder((Order)getFormObject());
                } else {
                    ovd.removeOrder((Order)getFormObject());
                }
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));
            }
        });
        draw.setPreferredSize(new Dimension(16, 16));
        draw.setOpaque(true);
        draw.setBackground(Color.white);
        draw.setVisible(true);
        draw.setEnabled(false);
        glb.append(draw);

        glb.nextLine();
        JPanel p = glb.getPanel();
        //p.setPreferredSize(new Dimension(166, 16));
        p.setBackground(Color.white);
        p.setBorder(null);
        
        p.setFocusTraversalPolicyProvider(true);
        p.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                if (aComponent == btn) {
                    return draw;
                } else if (aComponent == draw) {
                    return btn;
                }
                return btn;
            }

            public Component getComponentBefore(Container aContainer, Component aComponent) {
                if (aComponent == btn) {
                    return draw;
                } else if (aComponent == draw) {
                    return btn;
                }
                return draw;
            }

            
            public Component getDefaultComponent(Container aContainer) {
                return btn;
            }

            public Component getFirstComponent(Container aContainer) {
                return btn;
            }

            public Component getLastComponent(Container aContainer) {
                return (draw.isEnabled() ? draw : btn);
            }
            
        });
        return p;
    }

    public void actionPerformed(ActionEvent e) {
          Order order = (Order)getFormObject();
          Application.instance().getApplicationContext().publishEvent(
              new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
        //final OrderEditorForm form = (OrderEditorForm)Application.instance().getApplicationContext().getBean("orderEditorForm");
//        final OrderEditor form = new OrderEditor();
//        FormBackedDialogPage page = new FormBackedDialogPage(form);
//
//        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
//            protected void onAboutToShow() {
//                form.setFormObject(getFormObject());
//            }
//
//            protected boolean onFinish() {
//                form.commit();
//                // throw a selected hex changed event for current hex
//                // first we need to find the current hex
//                Order order = (Order)getFormObject();
//                Point selHex = new Point(order.getX(), order.getY());
//                Application.instance().getApplicationContext().publishEvent(
//                                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selHex, this));
//
//                // throw an order changed event
//                Application.instance().getApplicationContext().publishEvent(
//                                    new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), order, this));
//
//                return true;
//            }
//        };
//        dialog.setTitle(getMessage("editOrderDialog.title"));
//        dialog.setModal(false);
//        dialog.showDialog();
    }

}
