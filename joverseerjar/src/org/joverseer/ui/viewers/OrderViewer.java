package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import javax.swing.border.Border;

import org.joverseer.JOApplication;
import org.joverseer.domain.Order;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dataFlavors.OrderDataFlavor;
import org.joverseer.ui.support.transferHandlers.OrderExportTransferHandler;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows orders
 * Used by the Character viewer
 *
 * @author Marios Skounakis
 */
public class OrderViewer extends ObjectViewer implements ActionListener {
    public static final String FORM_PAGE = "OrderViewer"; //$NON-NLS-1$

    JTextField orderText;
    JLabel orderResultIcon;
    JCheckBox draw;
    JButton btn;

    public OrderViewer(FormModel formModel,GameHolder gameHolder) {
        super(formModel, FORM_PAGE,gameHolder);
    }

    @Override
	public boolean appliesTo(Object obj) {
        return Order.class.isInstance(obj);
    }

    protected OrderResultTypeEnum addValidationResult(Order o, OrderValidationResult res, Integer paramI, OrderResultTypeEnum orderResultType, ArrayList<OrderResult> results) {
    	if (res == null) return orderResultType;
    	String e = ""; //$NON-NLS-1$
    	if (paramI != null) {
    		e = Messages.getString("OrderViewer.ParamNColon",new Object [] { paramI }); //$NON-NLS-1$
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
    	if (!o.isBlank()) {

	        OrderResultContainer container = this.gameHolder.getGame().getTurn().getOrderResults().getResultCont();
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
    	ico = JOApplication.getIcon(orderResultType);
        this.orderResultIcon.setIcon(ico);
        if (ico != null) {
            String txt = ""; //$NON-NLS-1$
            for (OrderResult result : results) {
                String resText = null;
                resText = result.getType().toString();
                txt += (txt.equals("") ? "" : "") + "<li>" + resText + (joErrors ? " [JO]" : "") + ": " + result.getMessage() + "</li>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            }
            if (!txt.equals("")) { //$NON-NLS-1$
                txt = "<html><body><lu>" + txt + "</lu></body></html>"; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                txt = null;
            }
            this.orderResultIcon.setToolTipText(txt);
        } else {
            this.orderResultIcon.setToolTipText(null);
        }
    }

    @Override
	public void setFormObject(Object object) {
        super.setFormObject(object);
        Order o = (Order)object;
        if (o.getOrderNo() <= 0) {
            this.orderText.setText(Messages.getString("OrderViewer.NA")); //$NON-NLS-1$
        } else {
            this.orderText.setText(o.getNoAndCode() + " " + Order.getParametersAsString(o.getParameters())); //$NON-NLS-1$
            this.orderText.setCaretPosition(0);
        }
        this.orderText.setTransferHandler(new OrderExportTransferHandler(o));

        setOrderValidationResults(o);

        OrderVisualizationData ovd = OrderVisualizationData.instance();
        this.draw.setSelected(ovd.contains(o));
        if (GraphicUtils.canRenderOrder(o)) {
        	this.draw.setEnabled(true);
        } else {
        	this.draw.setEnabled(false);
        	this.draw.setSelected(false);
        }

    }

    @Override
	protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(1, 1, 1, 3));
        glb.append(this.orderText = new JTextField());
		// diagnostic border
		Border  border = null;//BorderFactory.createLineBorder(Color.green);

		this.orderText.setBorder(border);
        this.orderText.setPreferredSize(this.uiSizes.newDimension(170/16, this.uiSizes.getHeight4()));
        this.orderText.setText(Messages.getString("OrderViewer.NA")); //$NON-NLS-1$

        this.orderText.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
                TransferHandler handler = OrderViewer.this.orderText.getTransferHandler();
                handler.exportAsDrag(OrderViewer.this.orderText, e, TransferHandler.COPY);
            }
        });

        this.orderText.setDropTarget(new DropTarget(this.orderText, new DropTargetAdapter() {
            @Override
			public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable t = dtde.getTransferable();
                    if (dtde.isDataFlavorSupported(new OrderDataFlavor())) {
                        Order no = (Order)t.getTransferData(new OrderDataFlavor());
                        Order o = (Order)getFormObject();

                        org.joverseer.domain.Character c = o.getCharacter();
                        org.joverseer.domain.Character nc = no.getCharacter();

                        //note order ... if both characters are the same, then the order object is unintentionally duplicated
                        int index1 = c.findOrderIndexOf(o);
                        int index2 = nc.findOrderIndexOf(no);
                        if ((index1 > -1) && (index1 > -1)) {
                        		
                        	Order temp;
                        	temp = c.getOrders()[index1];
                        	c.getOrders()[index1] = nc.getOrders()[index2];                        	
                        	nc.getOrders()[index2] = temp;
                        	// now fixup the owners of the order objects we swapped
                        	if (!c.equals(nc)) {
                        		o.setCharacter(nc);
                        		no.setCharacter(c);
                        	}                        	
                        }
                        OrderViewer.this.gameHolder.getGame().getTurn().getOrderResults().getResultCont().removeResultsForOrder(o);
                        JOApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, o, this);
                        OrderViewer.this.gameHolder.getGame().getTurn().getOrderResults().getResultCont().removeResultsForOrder(no);
                        JOApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, no, this);
                    }
                }
                catch (Exception exc) {
                }
            }
        }));

        this.orderResultIcon = new JLabel(""); //$NON-NLS-1$
        this.orderResultIcon.setPreferredSize(this.uiSizes.newDimension(1, this.uiSizes.getHeight4()));
        this.orderResultIcon.setBorder(border);
        glb.append(this.orderResultIcon);

        ImageSource imgSource = JOApplication.getImageSource();
        Icon ico = new ImageIcon(imgSource.getImage("edit.image")); //$NON-NLS-1$
        this.btn = new JButton(ico);
        this.btn.setToolTipText(Messages.getString("OrderViewer.EditOrder")); //$NON-NLS-1$
        this.btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                Order order = (Order)getFormObject();
                JOApplication.publishEvent(LifecycleEventsEnum.EditOrderEvent, order, this);
            }
        });

        this.btn.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
        this.btn.setBorder(border);
        glb.append(this.btn);

//        imgSource = joApplication.getImageSource();
//        ico = new ImageIcon(imgSource.getImage("selectHexCommand.icon"));
        this.draw = new JCheckBox();
        this.draw.setToolTipText(Messages.getString("OrderViewer.DrawOrder")); //$NON-NLS-1$
        this.draw.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                OrderVisualizationData ovd = OrderVisualizationData.instance();
                if (OrderViewer.this.draw.isSelected()) {
                    ovd.addOrder((Order)getFormObject());
                } else {
                    ovd.removeOrder((Order)getFormObject());
                }
                JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, getFormObject(), this);
            }
        });
        this.draw.setPreferredSize(this.uiSizes.newDimension(1, this.uiSizes.getHeight4()));
        this.draw.setBorder(border);
        this.draw.setOpaque(true);
        this.draw.setBackground(Color.white);
        this.draw.setVisible(true);
        this.draw.setEnabled(false);
        glb.append(this.draw);

        glb.nextLine();
        JPanel p = glb.getPanel();
        //p.setPreferredSize(new Dimension(166, 16));
        p.setBackground(Color.white);
        p.setBorder(border);

        p.setFocusTraversalPolicyProvider(true);
        p.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
			public Component getComponentAfter(Container aContainer, Component aComponent) {
                if (aComponent == OrderViewer.this.btn) {
                    return OrderViewer.this.draw;
                } else if (aComponent == OrderViewer.this.draw) {
                    return OrderViewer.this.btn;
                }
                return OrderViewer.this.btn;
            }

            @Override
			public Component getComponentBefore(Container aContainer, Component aComponent) {
                if (aComponent == OrderViewer.this.btn) {
                    return OrderViewer.this.draw;
                } else if (aComponent == OrderViewer.this.draw) {
                    return OrderViewer.this.btn;
                }
                return OrderViewer.this.draw;
            }


            @Override
			public Component getDefaultComponent(Container aContainer) {
                return OrderViewer.this.btn;
            }

            @Override
			public Component getFirstComponent(Container aContainer) {
                return OrderViewer.this.btn;
            }

            @Override
			public Component getLastComponent(Container aContainer) {
                return (OrderViewer.this.draw.isEnabled() ? OrderViewer.this.draw : OrderViewer.this.btn);
            }

        });
        return p;
    }

    public void setEnabledButton(boolean b) {
    	this.btn.setEnabled(b);
    	if(!b) this.btn.setToolTipText(Messages.getString("CharacterViewer.showOrders.ToolTip"));
    	else this.btn.setToolTipText(Messages.getString("OrderViewer.EditOrder"));
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
          Order order = (Order)getFormObject();
          JOApplication.publishEvent(LifecycleEventsEnum.EditOrderEvent, order, this);
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
//                joApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, selHex, this);
//
//                // throw an order changed event
//                joApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, order, this);
//
//                return true;
//            }
//        };
//        dialog.setTitle(getMessage("editOrderDialog.title"));
//        dialog.setModal(false);
//        dialog.showDialog();
    }

}
