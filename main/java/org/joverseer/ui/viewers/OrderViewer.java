package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.Order;
import org.joverseer.ui.orders.OrderEditorForm;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.LifecycleEventsEnum;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 12 Íןו 2006
 * Time: 11:27:45 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderViewer extends AbstractForm implements ActionListener {
    public static final String FORM_PAGE = "OrderViewer";

    JTextField orderText;

    public OrderViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
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
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));
        glb.append(orderText = new JTextField());
        orderText.setBorder(null);
        orderText.setPreferredSize(new Dimension(170, 12));
        orderText.setText("N/A");

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("edit.image"));
        JButton btn = new JButton(ico);
        btn.addActionListener(this);
        btn.setPreferredSize(new Dimension(16, 16));
        glb.append(btn);

        glb.nextLine();
        JPanel p = glb.getPanel();
        //p.setPreferredSize(new Dimension(166, 16));
        p.setBackground(Color.white);
        p.setBorder(null);
        return p;
    }

    public void actionPerformed(ActionEvent e) {
        FormModel formModel = FormModelHelper.createFormModel(getFormObject());
        final OrderEditorForm form = new OrderEditorForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                form.commit();
                // throw a selected hex changed event for current hex
                // first we need to find the current hex
                Order order = (Order)getFormObject();
                Point selHex = new Point(order.getX(), order.getY());
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selHex, this));

                return true;
            }
        };
        dialog.setTitle(getMessage("editOrderDialog.title"));
        dialog.showDialog();
    }

}
