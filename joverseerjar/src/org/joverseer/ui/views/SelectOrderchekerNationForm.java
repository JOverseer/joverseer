package org.joverseer.ui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.NationComboBox;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Form for selecting the nation for which to run OrderChecker for
 * 
 * @author Marios Skounakis
 */
public class SelectOrderchekerNationForm extends ScalableAbstractForm {

    public static String FORM_PAGE = "selectOrderchekerNationForm"; //$NON-NLS-1$

    NationComboBox nationCombo;
    Nation nation;

    public SelectOrderchekerNationForm(FormModel arg0) {
        super(arg0, FORM_PAGE);
    }

    @Override
	protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel(Messages.getString("SelectOrderchekerNationForm.2"))); //$NON-NLS-1$
        tlb.relatedGapRow();
        tlb.cell(this.nationCombo = new NationComboBox(GameHolder.instance()), "align=left"); //$NON-NLS-1$
//        this.nationCombo.setPreferredSize(this.uiSizes.newDimension(160/16, this.uiSizes.getComboxBoxHeight()));
        this.nationCombo.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                Nation n = SelectOrderchekerNationForm.this.nationCombo.getSelectedNation();
                if (n != null) {
                	setFormObject(n);
                }
            }

        });
        this.nationCombo.load(true, false);
        return tlb.getPanel();
    }

    @Override
	public Object getFormObject() {
        return this.nation;
    }

    @Override
	public void setFormObject(Object arg0) {
        super.setFormObject(arg0);
        this.nation = (Nation) arg0;
    }


}
