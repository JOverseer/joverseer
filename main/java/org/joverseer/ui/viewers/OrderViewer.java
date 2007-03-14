package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.NationMessage;
import org.joverseer.domain.Order;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.orders.OrderEditorForm;
import org.joverseer.ui.orders.OrderVisualizationData;
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
            orderText.setText(o.getNoAndCode() + " " + o.getParameters());
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
        } else if (orderResultType == OrderResultTypeEnum.Warn) {
            ico = new ImageIcon(imgSource.getImage("orderresult.warn.icon"));
        } else if (orderResultType == OrderResultTypeEnum.Error) {
            ico = new ImageIcon(imgSource.getImage("orderresult.error.icon"));
        }
        orderResultIcon.setIcon(ico);
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
        JButton btn = new JButton(ico);
        btn.addActionListener(this);
        btn.setPreferredSize(new Dimension(16, 16));
        glb1.append(btn);

        imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        ico = new ImageIcon(imgSource.getImage("selectHexCommand.icon"));
        btn = new JButton(ico);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
                ovd.clear();
                ovd.addOrder((Order)getFormObject());
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));
            }
        };
        btn.addActionListener(al);
        btn.setPreferredSize(new Dimension(16, 16));
        glb1.append(btn);

        glb.append(glb1.getPanel());

        glb.nextLine();
        JPanel p = glb.getPanel();
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
