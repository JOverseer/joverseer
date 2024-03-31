package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.controls.NationDualListSelector;
import org.springframework.binding.form.FormModel;

/**
 * Creates a window which allows the player to set which nations they control which is then saved to PlayerInfo
 * This can then be used in other parts of JOverseer to potentially improve user experiences.
 * 
 * NOTE: At the moment, due to how PlayerInfo is used, it doesn't exist until an xml file is imported, so this form cannot be used before then.
 * 		 In addition, although the information of which nations they control is transfered to the newest imported turn from the previous max turn,
 * 		 it does not set it for all turns previously loaded in.
 * 
 * @author Sam Terrett
 */
public class EditPlayedNationsForm extends ScalableAbstractForm {
	public static final String FORM_PAGE = "editPlayedNationsForm";
	GameHolder gameHolder;
	
	NationDualListSelector nationSelector;

	public EditPlayedNationsForm(FormModel m, GameHolder gameHolder) {
		super(m, FORM_PAGE);
		this.gameHolder = gameHolder;
	}
	
	@Override
	protected JComponent createFormControl() {
		// TODO Auto-generated method stub
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		Component horizontalStrut_2_1 = Box.createHorizontalStrut(5);
		labelPanel.add(horizontalStrut_2_1);
		
		JLabel lbSel = new JLabel("<html><font size=2><em>Selected:</em></font><html>");
		labelPanel.add(lbSel);
		
		Component horizontalStrut_2_2 = Box.createHorizontalStrut(105);
		labelPanel.add(horizontalStrut_2_2);
		
		JLabel lbUSel = new JLabel("<html><font size=2><em>Unselected:</em></font><html>");
		labelPanel.add(lbUSel);
		
		p.add(labelPanel);
		
		this.nationSelector = new NationDualListSelector(this.gameHolder);
		this.nationSelector.setListSize(6, 120);
		
		//Loads nations into lists, if preferences already selected then loads them into 'selected' list accordingly
		PlayerInfo pI = this.gameHolder.getGame().getTurn().getPlayerInfo(this.gameHolder.getGame().getMetadata().getNationNo());
		if(pI.getControlledNations() == null) {
			this.nationSelector.load(true, false);
		}
		else {
			this.nationSelector.load(false, false, pI.getControlledNations());
		}

		p.add(this.nationSelector);
		return p;
	}
	
	@Override
	public void commit() {
		PlayerInfo pI = this.gameHolder.getGame().getTurn().getPlayerInfo(this.gameHolder.getGame().getMetadata().getNationNo());
		
		pI.setControlledNations(this.nationSelector.getSelectedNationNos());
		return;
	}

}
