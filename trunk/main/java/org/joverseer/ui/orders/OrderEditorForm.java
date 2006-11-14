package org.joverseer.ui.orders;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.list.ComboBoxListModelAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 10 Íןו 2006
 * Time: 10:12:57 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderEditorForm extends AbstractForm implements ActionListener {
    public static final String FORM_PAGE = "orderEditorForm";
    GameMetadata gm;
    JTextArea orderDescription;
    JComboBox orderCombo;

    public OrderEditorForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
        gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
    }

    protected JComponent createFormControl() {
        JTextField txt;
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        //todo
        Container orderMetadata = gm.getOrders();
        ListListModel orders = new ListListModel();
        for (OrderMetadata om : (ArrayList<OrderMetadata>)orderMetadata.getItems()) {
            orders.add(om.getNumber() + " " + om.getCode());
        }
        SortedListModel slm = new SortedListModel(orders);

        formBuilder.add("noAndCode", orderCombo = new JComboBox(new ComboBoxListModelAdapter(slm)));

        orderCombo.setPreferredSize(new Dimension(50, 20));
        orderCombo.addActionListener(this);
        formBuilder.row();

        formBuilder.add("parameters", txt = new JTextField());
        txt.setPreferredSize(new Dimension(250, 20));

        formBuilder.row();
        orderDescription = (JTextArea)formBuilder.addTextArea("metadataDescription")[1];
        orderDescription.setLineWrap(true);
        orderDescription.setWrapStyleWord(true);
        orderDescription.setPreferredSize(new Dimension(250, 40));
        orderDescription.setBorder(null);
        orderDescription.setEditable(false);
        Font f = new Font(orderDescription.getFont().getName(),
                            Font.ITALIC,
                            orderDescription.getFont().getSize());
        orderDescription.setFont(f);
        return formBuilder.getForm();
    }

    public void actionPerformed(ActionEvent e) {
        String txt = "";
        try {
            String selOrder = (String)orderCombo.getSelectedItem();
            int i = selOrder.indexOf(' ');
            int no = Integer.parseInt(selOrder.substring(0, i));
            Container orderMetadata = gm.getOrders();

            OrderMetadata om = (OrderMetadata)orderMetadata.findFirstByProperty("number", no);
            txt = om.getName() + ", " + om.getDifficulty() + ", " + om.getRequirement();
        }
        catch (Exception exc) {
            // do nothing
        }
        orderDescription.setText(txt);
    }
}
