package org.joverseer.ui.combatCalculator;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.TacticEnum;
import org.joverseer.ui.support.Messages;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class CombatArmyForm extends AbstractForm {
	public static String FORM_ID = "combatArmyForm"; //$NON-NLS-1$

	public CombatArmyForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	protected JComponent createFormControl() {
		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.CommanderColon")), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField(Messages.getString("CombatArmyForm.commander")).getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.CommandRankColon")), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField(Messages.getString("CombatArmyForm.commandRank")).getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		ArrayList<Nation> nations = new ArrayList<Nation>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
		if (Game.isInitialized(g)) {
			GameMetadata gm = g.getMetadata();
			nations.addAll(gm.getNations());
		}

		ComboBoxBinding b = (ComboBoxBinding) sbf.createBoundComboBox("nation", new ValueHolder(nations), "name"); //$NON-NLS-1$ //$NON-NLS-2$
		b.setComparator(new PropertyComparator("number", true, true)); //$NON-NLS-1$

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.NationColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(b.getControl(), "align=left"); //$NON-NLS-1$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.MoraleColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("morale").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.TacticColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundComboBox("tactic", new ListListModel(Arrays.asList(TacticEnum.values()))).getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		TableLayoutBuilder lb = new TableLayoutBuilder();
		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(Messages.getString("CombatArmyForm.AbbNumber"))); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(Messages.getString("CombatArmyForm.AbbTraining"))); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(Messages.getString("CombatArmyForm.AbbWeapon"))); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(Messages.getString("CombatArmyForm.AbbArmour"))); //$NON-NLS-1$
		lb.relatedGapRow();

		for (String at : new String[] { Messages.getString("CombatArmyForm.AbbHC"), Messages.getString("CombatArmyForm.AbbLC"), Messages.getString("CombatArmyForm.AbbHI"), Messages.getString("CombatArmyForm.AbbLI"), Messages.getString("CombatArmyForm.AbbAR"), Messages.getString("CombatArmyForm.AbbMA") }) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			lb.cell(new JLabel(at + ":"), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".number").getControl(), "colspec=left:60px"); //$NON-NLS-1$ //$NON-NLS-2$
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".training").getControl(), "colspec=left:60px"); //$NON-NLS-1$ //$NON-NLS-2$
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".weapons").getControl(), "colspec=left:60px"); //$NON-NLS-1$ //$NON-NLS-2$
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".armor").getControl(), "colspec=left:60px"); //$NON-NLS-1$ //$NON-NLS-2$
			lb.relatedGapRow();
		}
		tlb.cell(lb.getPanel(), "colspan=2"); //$NON-NLS-1$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.OffencePtsColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("offensiveAddOns").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.DefencePtsColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("defensiveAddOns").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		tlb.cell(new JLabel(Messages.getString("CombatArmyForm.WarMachinesColon")), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("WM.number").getControl(), "align=left"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.relatedGapRow();

		return tlb.getPanel();
	}

}
