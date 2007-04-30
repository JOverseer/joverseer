package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.combobox.CheckBoxListComboBox;

public class EditNationsForm extends AbstractForm {
	public static String FORM_ID = "editNationsForm";

    ArrayList<JTextField> nationNames = new ArrayList<JTextField>();
    ArrayList<JTextField> nationShortNames = new ArrayList<JTextField>();
    ArrayList<CheckBoxListComboBox> nationSNAs = new ArrayList<CheckBoxListComboBox>();
    ArrayList<JLabel> labels = new ArrayList<JLabel>();

    public EditNationsForm(FormModel arg0) {
		super(arg0, FORM_ID);
    }

    protected String[] getSNAList() {
        ArrayList<String> snas = new ArrayList<String>();
        for (SNAEnum sna : SNAEnum.values()) {
            snas.add(sna.toString());
        }
        return snas.toArray(new String[]{});
    }
    
    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder(); 
        
        for (int i=0; i<25; i++) {
            JTextField nationName = new JTextField();
            nationNames.add(nationName);
            nationName.setPreferredSize(new Dimension(200, 20));
            JLabel lbl = new JLabel();
            lbl.setPreferredSize(new Dimension(100, 24));
            lbl.setText("Nation " + (i + 1) + " :");
            labels.add(lbl);
            tlb.cell(lbl);
            tlb.cell(nationName);
            JTextField nationShortName = new JTextField();
            nationShortNames.add(nationShortName);
            nationShortName.setPreferredSize(new Dimension(100, 20));
            tlb.gapCol();
            tlb.cell(nationShortName);
            
            CheckBoxListComboBox nationSNAList = new CheckBoxListComboBox(getSNAList(), String[].class);
            nationSNAList.setEditable(false);
            nationSNAs.add(nationSNAList);
            nationSNAList.setPreferredSize(new Dimension(200, 20));
            tlb.gapCol();
            tlb.cell(nationSNAList);
            
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
                n.setName(nationNames.get(i-1).getText());
                n.setShortName(nationShortNames.get(i-1).getText());
            }
        }
    }
    
    public void setFormObject(Object arg0) {
        super.setFormObject(arg0);
        GameMetadata gm = (GameMetadata)arg0;
        for (int i=1; i<26; i++) {
            if (i < gm.getNations().size()) {
                Nation n = (Nation)gm.getNations().get(i);
                nationNames.get(i-1).setText(n.getName());
                nationShortNames.get(i-1).setText(n.getShortName());
                
            } else {
                labels.get(i-1).setEnabled(false);
                nationNames.get(i-1).setEnabled(false);
                nationShortNames.get(i-1).setEnabled(false);
                nationSNAs.get(i-1).setEnabled(false);
            }
        }
    }
}
