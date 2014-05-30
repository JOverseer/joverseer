package org.joverseer.ui.combatCalculator;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.Messages;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class CombatPopCenterForm extends AbstractForm {
	public static String FORM_ID = "combatPopCenterForm"; //$NON-NLS-1$

	public CombatPopCenterForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	protected JComponent createFormControl() {
		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel(Messages.getString("CombatPopCenterForm.NameColon")), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("name").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		ArrayList<Nation> nations = new ArrayList<Nation>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
		if (Game.isInitialized(g)) {
			GameMetadata gm = g.getMetadata();
			nations.addAll(gm.getNations());
		}

		ComboBoxBinding b = (ComboBoxBinding) sbf.createBoundComboBox("nation", new ValueHolder(nations), "name"); //$NON-NLS-1$ //$NON-NLS-2$
		b.setComparator(new PropertyComparator("number", true, true)); //$NON-NLS-1$

		tlb.cell(new JLabel(Messages.getString("CombatPopCenterForm.NationColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(b.getControl(), "align=left"); //$NON-NLS-1$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatPopCenterForm.LoyaltyColon")), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("loyalty").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		b = (ComboBoxBinding) sbf.createBoundComboBox("size", new ValueHolder(PopulationCenterSizeEnum.values()), "renderString"); //$NON-NLS-1$ //$NON-NLS-2$
		b.setComparator(new PropertyComparator("number", true, true)); //$NON-NLS-1$

		tlb.cell(new JLabel(Messages.getString("CombatPopCenterForm.SizeColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(b.getControl(), "align=left"); //$NON-NLS-1$
		tlb.relatedGapRow();

		b = (ComboBoxBinding) sbf.createBoundComboBox("fort", new ValueHolder(FortificationSizeEnum.values()), "renderString"); //$NON-NLS-1$ //$NON-NLS-2$
		b.setComparator(new PropertyComparator("number", true, true)); //$NON-NLS-1$

		tlb.cell(new JLabel(Messages.getString("CombatPopCenterForm.FortColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(b.getControl(), "align=left"); //$NON-NLS-1$
		tlb.relatedGapRow();

		return tlb.getPanel();
	}
}