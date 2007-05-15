package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.joverseer.metadata.domain.*;

public class EditNationAllegiancesForm extends AbstractForm {
    public static final String FORM_PAGE = "editNationAllegiancesForm";
    
    ArrayList<JComboBox> allegiances = new ArrayList<JComboBox>();
    ArrayList<JCheckBox> eliminated = new ArrayList<JCheckBox>();
    ArrayList<JLabel> labels = new ArrayList<JLabel>();
    
    public EditNationAllegiancesForm(FormModel m) {
        super(m, FORM_PAGE);
    }
    
    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder(); 
        
        tlb.cell(new JLabel(" "));
        tlb.gapCol();
        tlb.cell(new JLabel("Allegiance"));
        tlb.gapCol();
        tlb.cell(new JLabel("Eliminated"));
        tlb.relatedGapRow();
        
        for (int i=0; i<25; i++) {
            JComboBox combo = new JComboBox(NationAllegianceEnum.values());
            allegiances.add(combo);
            combo.setPreferredSize(new Dimension(200, 20));
            JLabel lbl = new JLabel();
            lbl.setPreferredSize(new Dimension(100, 24));
            lbl.setText("Nation " + (i + 1) + " :");
            labels.add(lbl);
            tlb.cell(lbl);
            tlb.gapCol();
            tlb.cell(combo);
            tlb.gapCol();
            
            JCheckBox elim = new JCheckBox("");
            tlb.cell(elim, "align=center");
            eliminated.add(elim);
            tlb.row();
        }
        return new JScrollPane(tlb.getPanel());
    }

    public void commit() {
        super.commit();
        GameMetadata gm = (GameMetadata)getFormObject();
        for (int i=1; i<26; i++) {
            if (i < gm.getNations().size()) {
                Nation n = (Nation)gm.getNations().get(i);
                n.setAllegiance((NationAllegianceEnum)allegiances.get(i-1).getSelectedItem());
                n.setEliminated(eliminated.get(i-1).isSelected());
            }
        }
    }

    public void setFormObject(Object arg0) {
        super.setFormObject(arg0);
        GameMetadata gm = (GameMetadata)arg0;
        for (int i=1; i<26; i++) {
            if (i < gm.getNations().size()) {
                Nation n = (Nation)gm.getNations().get(i);
                labels.get(i-1).setText(n.getName() + " :");
                allegiances.get(i-1).setSelectedItem(n.getAllegiance());
                eliminated.get(i-1).setSelected(n.getEliminated());
                if (n.getRemoved()) {
                	labels.get(i-1).setEnabled(false);
                    allegiances.get(i-1).setEnabled(false);
                    eliminated.get(i-1).setEnabled(false);
                }
            } else {
                labels.get(i-1).setEnabled(false);
                allegiances.get(i-1).setEnabled(false);
                eliminated.get(i-1).setEnabled(false);
            }
        }
    }
    

}
