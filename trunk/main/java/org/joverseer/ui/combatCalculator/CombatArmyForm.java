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
	public static String FORM_ID = "combatArmyForm";

	public CombatArmyForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	protected JComponent createFormControl() {
		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Commander :"), "colspec=left:120px");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("commander").getControl(), "align=left");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("Command Rank :"), "colspec=left:120px");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("commandRank").getControl(), "align=left");
		tlb.relatedGapRow();

		ArrayList<Nation> nations = new ArrayList<Nation>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (Game.isInitialized(g)) {
			GameMetadata gm = g.getMetadata();
			nations.addAll(gm.getNations());
		}

		ComboBoxBinding b = (ComboBoxBinding) sbf.createBoundComboBox("nation", new ValueHolder(nations), "name");
		b.setComparator(new PropertyComparator("number", true, true));

		tlb.cell(new JLabel("Nation :"), "align=left");
		tlb.gapCol();
		tlb.cell(b.getControl(), "align=left");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("Morale :"), "align=left");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("morale").getControl(), "align=left");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("Tactic :"), "align=left");
		tlb.gapCol();
		tlb.cell(sbf.createBoundComboBox("tactic", new ListListModel(Arrays.asList(TacticEnum.values()))).getControl(), "align=left");
		tlb.relatedGapRow();

		TableLayoutBuilder lb = new TableLayoutBuilder();
		lb.cell(new JLabel(" "));
		lb.gapCol();
		lb.cell(new JLabel("No"));
		lb.gapCol();
		lb.cell(new JLabel("Train."));
		lb.gapCol();
		lb.cell(new JLabel("Weap."));
		lb.gapCol();
		lb.cell(new JLabel("Armor"));
		lb.relatedGapRow();

		for (String at : new String[] { "HC", "LC", "HI", "LI", "AR", "MA" }) {
			lb.cell(new JLabel(at + ":"), "colspec=left:120px");
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".number").getControl(), "colspec=left:60px");
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".training").getControl(), "colspec=left:60px");
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".weapons").getControl(), "colspec=left:60px");
			lb.gapCol();
			lb.cell(sbf.createBoundTextField(at + ".armor").getControl(), "colspec=left:60px");
			lb.relatedGapRow();
		}
		tlb.cell(lb.getPanel(), "colspan=2");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("Offense Pts :"), "align=left");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("offensiveAddOns").getControl(), "align=left");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("Defense Pts :"), "align=left");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("defensiveAddOns").getControl(), "align=left");
		tlb.relatedGapRow();

		tlb.cell(new JLabel("War Machines :"), "align=left");
		tlb.gapCol();
		tlb.cell(sbf.createBoundTextField("WM.number").getControl(), "align=left");
		tlb.relatedGapRow();

		return tlb.getPanel();
	}

}
