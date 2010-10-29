package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Form that is dynamically created based on the nation metadata of the game
 * 
 * It shows one combobox and one checkbox per nation so that the user can
 * specify the nation allegiances and whether the nation has been eliminated or
 * not
 * 
 * @author Marios Skounakis
 */
public class EditNationAllegiancesForm extends AbstractForm {
	public static final String FORM_PAGE = "editNationAllegiancesForm";

	Hashtable<Integer, JComboBox> allegiances = new Hashtable<Integer, JComboBox>();
	Hashtable<Integer, JCheckBox> eliminated = new Hashtable<Integer, JCheckBox>();
	Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
	JCheckBox stopAsking;

	public EditNationAllegiancesForm(FormModel m) {
		super(m, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();

		tlb.cell(new JLabel(" "));
		tlb.gapCol();
		tlb.cell(new JLabel("Allegiance"));
		tlb.gapCol();
		tlb.cell(new JLabel("Eliminated"));
		tlb.gapCol();
		tlb.cell(new JLabel(" "));
		tlb.gapCol();
		tlb.cell(new JLabel("Allegiance"));
		tlb.gapCol();
		tlb.cell(new JLabel("Eliminated"));
		tlb.gapCol();
		tlb.cell(new JLabel(" "));
		tlb.gapCol();
		tlb.cell(new JLabel("Allegiance"));
		tlb.gapCol();
		tlb.cell(new JLabel("Eliminated"));
		tlb.gapCol();
		tlb.relatedGapRow();

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 3; j++) {
				int n = (i + 1) + (j * 10);
				if (n <= 25) {
					JComboBox combo = new JComboBox(NationAllegianceEnum.values());
					allegiances.put(n, combo);
					combo.setPreferredSize(new Dimension(100, 20));
					JLabel lbl = new JLabel();
					lbl.setPreferredSize(new Dimension(100, 24));
					lbl.setText("Nation " + n + " :");
					lbl.setHorizontalAlignment(JLabel.RIGHT);
					labels.put(n, lbl);
					tlb.cell(lbl);
					tlb.gapCol();
					tlb.cell(combo);
					tlb.gapCol();
					JCheckBox elim = new JCheckBox("");
					tlb.cell(elim, "align=center");
					eliminated.put(n, elim);
					tlb.gapCol();
				} else {
					tlb.cell();
					tlb.gapCol();
					tlb.cell();
					tlb.gapCol();
					tlb.cell();
					tlb.gapCol();
				}
			}
			tlb.row();
		}
		tlb.relatedGapRow();
		tlb.separator("");
		tlb.relatedGapRow();
		tlb.cell(new JLabel("Stop asking for neutral nations allegiance changes :"), "colspan=3");
		tlb.gapCol();
		tlb.cell(stopAsking = new JCheckBox(), "align=left");
		tlb.gapCol();
		tlb.cell();
		tlb.cell();
		tlb.cell();
		tlb.cell();
		tlb.cell();
		tlb.relatedGapRow();
		return new JScrollPane(tlb.getPanel());
	}

	@Override
	public void commit() {
		super.commit();
		Game g = (Game) getFormObject();
		GameMetadata gm = g.getMetadata();
		for (int i = 1; i < 26; i++) {
			if (i < gm.getNations().size()) {
				Nation n = gm.getNations().get(i);
				n.setAllegiance((NationAllegianceEnum) allegiances.get(i).getSelectedItem());
				n.setEliminated(eliminated.get(i).isSelected());
			}
		}
		g.setParameter("StopAskingForAllegianceChanges", stopAsking.isSelected() ? "1" : "0");
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		Game g = (Game) arg0;
		GameMetadata gm = g.getMetadata();
		for (int i = 1; i < 26; i++) {
			if (i < gm.getNations().size()) {
				Nation n = gm.getNations().get(i);
				labels.get(i).setText(n.getName() + " :");
				allegiances.get(i).setSelectedItem(n.getAllegiance());
				eliminated.get(i).setSelected(n.getEliminated());
				if (n.getRemoved()) {
					labels.get(i).setEnabled(false);
					allegiances.get(i).setEnabled(false);
					eliminated.get(i).setEnabled(false);
				}
			} else {
				labels.get(i).setEnabled(false);
				allegiances.get(i).setEnabled(false);
				eliminated.get(i).setEnabled(false);
			}
		}
		boolean stopAskingV = false;
		if (g.containsParameter("StopAskingForAllegianceChanges") && "1".equals(g.getParameter("StopAskingForAllegianceChanges"))) {
			stopAskingV = true;
		}
		stopAsking.setSelected(stopAskingV);
	}
}
