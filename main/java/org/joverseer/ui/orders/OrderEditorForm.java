package org.joverseer.ui.orders;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;

/**
 * Simple order editor
 * 
 * @author Marios Skounakis
 * 
 * @deprecated
 */
@Deprecated
public class OrderEditorForm extends AbstractForm implements ActionListener {
	public static final String FORM_PAGE = "orderEditorForm";
	GameMetadata gm;
	JTextArea orderDescription;
	JComboBox orderCombo;

	public OrderEditorForm() {
		super(FormModelHelper.createFormModel(new Order(new Character())), FORM_PAGE);
	}

	public OrderEditorForm(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	private GameMetadata getGameMetadata() {
		if (gm == null) {
			gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
		}
		return gm;

	}

	@Override
	protected JComponent createFormControl() {
		JTextField txt;
		TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

		// todo
		Container<OrderMetadata> orderMetadata = getGameMetadata().getOrders();
		ListListModel orders = new ListListModel();
		for (OrderMetadata om : orderMetadata.getItems()) {
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
		orderDescription = (JTextArea) formBuilder.addTextArea("metadataDescription")[1];
		orderDescription.setLineWrap(true);
		orderDescription.setWrapStyleWord(true);
		orderDescription.setPreferredSize(new Dimension(250, 40));
		orderDescription.setBorder(null);
		orderDescription.setEditable(false);
		Font f = new Font(orderDescription.getFont().getName(), Font.ITALIC, orderDescription.getFont().getSize());
		orderDescription.setFont(f);
		return formBuilder.getForm();
	}

	public void actionPerformed(ActionEvent e) {
		String txt = "";
		try {
			String selOrder = (String) orderCombo.getSelectedItem();
			int i = selOrder.indexOf(' ');
			int no = Integer.parseInt(selOrder.substring(0, i));
			Container<OrderMetadata> orderMetadata = getGameMetadata().getOrders();

			OrderMetadata om = orderMetadata.findFirstByProperty("number", no);
			txt = om.getName() + ", " + om.getDifficulty() + ", " + om.getRequirement() + "\n" + om.getParameters();
		} catch (Exception exc) {
			// do nothing
		}
		orderDescription.setText(txt);
	}
}
