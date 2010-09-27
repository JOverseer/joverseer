package org.joverseer.ui.views;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Edit a pop center
 * 
 * @author Marios Skounakis
 */
// TODO needs validation
public class EditPopulationCenterForm extends AbstractForm {

	public static final String FORM_PAGE = "editPopulationCenterForm";

	public EditPopulationCenterForm(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
		formBuilder.add("name");
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
		b = (ComboBoxBinding) sbf.createBoundComboBox("size", new ValueHolder(PopulationCenterSizeEnum.values()), "renderString");
		formBuilder.add(b);
		formBuilder.row();
		b = (ComboBoxBinding) sbf.createBoundComboBox("fortification", new ValueHolder(FortificationSizeEnum.values()), "renderString");
		formBuilder.add(b);
		formBuilder.row();
		b = (ComboBoxBinding) sbf.createBoundComboBox("harbor", new ValueHolder(HarborSizeEnum.values()), "renderString");
		formBuilder.add(b);
		formBuilder.row();
		formBuilder.add("hidden", new JCheckBox());
		formBuilder.row();
		formBuilder.add("capital", new JCheckBox());
		formBuilder.row();
		formBuilder.add("loyalty");
		formBuilder.row();
		return formBuilder.getForm();
	}

}
