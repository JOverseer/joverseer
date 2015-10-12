package org.joverseer.ui.views;

import java.util.ArrayList;

import javax.swing.JComponent;

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
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Edit a character
 * 
 * @author Marios Skounakis
 */
// TODO needs validation
public class EditCharacterForm extends AbstractForm {
	public static final String FORM_PAGE = "editCharacterForm";

	public EditCharacterForm(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

		GraphicUtils.registerIntegerPropertyConverters(this, "hexNo");

		formBuilder.add("name");
		formBuilder.row();

		// formBuilder.add("id");
		// formBuilder.row();

		formBuilder.add("hexNo");
		formBuilder.row();

		ArrayList<Nation> nations = new ArrayList<Nation>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (Game.isInitialized(g)) {
			GameMetadata gm = g.getMetadata();
			nations.addAll(gm.getNations());
		}

		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
		ComboBoxBinding b = (ComboBoxBinding) sbf.createBoundComboBox("nation", new ValueHolder(nations), "name");
		b.setComparator(new PropertyComparator("number", true, true));
		formBuilder.add(b);
		formBuilder.row();

		formBuilder.add("command");
		formBuilder.row();

		formBuilder.add("commandTotal");
		formBuilder.row();

		formBuilder.add("agent");
		formBuilder.row();

		formBuilder.add("agentTotal");
		formBuilder.row();

		formBuilder.add("emmisary");
		formBuilder.row();

		formBuilder.add("emmisaryTotal");
		formBuilder.row();

		formBuilder.add("mage");
		formBuilder.row();

		formBuilder.add("mageTotal");
		formBuilder.row();

		formBuilder.add("stealth");
		formBuilder.row();

		formBuilder.add("stealthTotal");
		formBuilder.row();

		formBuilder.add("challenge");
		formBuilder.row();

		formBuilder.add("health");
		formBuilder.row();

		formBuilder.add("numberOfOrders");
		formBuilder.row();
		return formBuilder.getForm();
	}
}
