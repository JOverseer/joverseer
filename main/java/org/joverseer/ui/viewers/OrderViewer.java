package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.NationMessage;
import org.joverseer.domain.Order;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.orders.OrderEditorForm;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


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

    public void setFormObject(Object object) {
        super.setFormObject(object);
        Order o = (Order)object;
        if (o.getOrderNo() <= 0) {
            orderText.setText("N/A");
        } else {
            orderText.setText(o.getNoAndCode() + " " + Order.getParametersAsString(o.getParameters()));
            orderText.setCaretPosition(0);
        }
        OrderResultContainer container = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
        OrderResultTypeEnum orderResultType = container.getResultTypeForOrder(o);
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = null;
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
            for (OrderResult result : container.getResultsForOrder(o)) {
                String resText = null;
                resText = result.getType().toString();
                txt += (txt.equals("") ? "" : "") + "<li>" + resText + ": " + result.getMessage() + "</li>";
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
        glb.setDefaultInsets(new Insets(0, 0, 0, 3));
        glb.append(orderText = new JTextField());
        orderText.setBorder(null);
        orderText.setPreferredSize(new Dimension(170, 12));
        orderText.setText("N/A");

        orderResultIcon = new JLabel("");
        orderResultIcon.setPreferredSize(new Dimension(16, 16));
        glb.append(orderResultIcon);
        
        GridBagLayoutBuilder glb1 = new GridBagLayoutBuilder();
        glb1.setDefaultInsets(new Insets(0, 0, 0, 3));
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("edit.image"));
        JLabel btn = new JLabel(ico);
        btn.setToolTipText("Edit order");
        btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                Order order = (Order)getFormObject();
                Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
            }
        });
        btn.setPreferredSize(new Dimension(16, 16));
        btn.setOpaque(true);
        btn.setBackground(Color.white);
        glb1.append(btn);

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
        glb1.append(draw);

        JPanel p = glb1.getPanel();
        p.setOpaque(true);
        p.setBackground(Color.white);
        glb.append(p);

        glb.nextLine();
        p = glb.getPanel();
        //p.setPreferredSize(new Dimension(166, 16));
        p.setBackground(Color.white);
        p.setBorder(null);
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
