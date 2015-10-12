package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Edit a note
 * 
 * @author Marios Skounakis
 */
// TODO needs validation
public class EditNoteForm extends AbstractForm {
	public static final String FORM_PAGE = "editNoteForm";

	public EditNoteForm(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
		formBuilder.setLabelAttributes("valign=top colGrId=label colSpec=left:pref");

		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
		GraphicUtils.registerIntegerPropertyConverters(this, "targetDescription");

		JTextField tf = (JTextField) formBuilder.add(sbf.createBoundTextField("targetDescription"))[1];
		tf.setEditable(false);

		formBuilder.row();

		ArrayList<Nation> nations = new ArrayList<Nation>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (Game.isInitialized(g)) {
			GameMetadata gm = g.getMetadata();
			nations.addAll(gm.getNations());
		}

		ComboBoxBinding b = (ComboBoxBinding) sbf.createBoundComboBox("nation", new ValueHolder(nations), "name");
		b.setComparator(new PropertyComparator("number", true, true));
		formBuilder.add(b);
		formBuilder.row();

		JCheckBox checkBox = (JCheckBox) formBuilder.add("persistent")[1];
		checkBox.setText("");
		formBuilder.row();

		formBuilder.add("tags");
		formBuilder.row();

		Binding tb = sbf.createBoundTextArea("text");
		JTextArea area = (JTextArea) tb.getControl();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBorder(null);
		JScrollPane scp = (JScrollPane) formBuilder.addInScrollPane(tb)[1];
		scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scp.setPreferredSize(new Dimension(350, 150));
		return formBuilder.getForm();
	}

}
