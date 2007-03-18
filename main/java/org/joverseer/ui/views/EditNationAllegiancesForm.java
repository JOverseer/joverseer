package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComboBox;
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
    ArrayList<JLabel> labels = new ArrayList<JLabel>();
    
    public EditNationAllegiancesForm(FormModel m) {
        super(m, FORM_PAGE);
    }
    
    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder(); 
        
        for (int i=0; i<25; i++) {
            JComboBox combo = new JComboBox(NationAllegianceEnum.values());
            allegiances.add(combo);
            combo.setPreferredSize(new Dimension(200, 20));
            JLabel lbl = new JLabel();
            lbl.setPreferredSize(new Dimension(100, 24));
            lbl.setText("Nation " + (i + 1) + " :");
            labels.add(lbl);
            tlb.cell(lbl);
            tlb.cell(combo);
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
            } else {
                labels.get(i-1).setEnabled(false);
                allegiances.get(i-1).setEnabled(false);
            }
        }
    }
    

}
