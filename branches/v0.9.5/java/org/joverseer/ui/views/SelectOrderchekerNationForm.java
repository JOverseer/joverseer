package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class SelectOrderchekerNationForm extends AbstractForm {
	public static String FORM_PAGE = "selectOrderchekerNationForm";
	
	JComboBox nationCombo;
	Nation nation;

	public SelectOrderchekerNationForm(FormModel arg0) {
		super(arg0, FORM_PAGE);
	}
	
	private void loadNationCombo() {
        nationCombo.removeAllItems();
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return;
        if (g.getTurn() == null) return;
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            if (n.getNumber() == 0) continue;
            PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", n.getNumber());
            if (pi == null) continue;
            nationCombo.addItem(n.getName());
        }
        if (nationCombo.getItemCount() > 0) {
            nationCombo.setSelectedIndex(0);
        }
    }
 
	protected JComponent createFormControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Select the nation for which to check orders:"));
		tlb.relatedGapRow();
		tlb.cell(nationCombo = new JComboBox(), "align=left");
		nationCombo.setPreferredSize(new Dimension(200, 20));
		nationCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Game g = GameHolder.instance().getGame();
                if (nationCombo.getSelectedItem() == null) return;
                Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
                setFormObject(n);
			}
			
		});
		loadNationCombo();
		return tlb.getPanel();
	}

	public Object getFormObject() {
        return nation;
	}

	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		nation = (Nation)arg0;
	}
	
	
	
	

}
