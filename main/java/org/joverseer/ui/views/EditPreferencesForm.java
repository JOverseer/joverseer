package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.joverseer.preferences.Preference;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.preferences.PreferenceValue;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class EditPreferencesForm extends AbstractForm {
    public static String FORM_ID = "editPreferencesForm";
    JPanel panel;
    HashMap<String, JComboBox> components = new HashMap<String, JComboBox>();
    
    public EditPreferencesForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        
        String group = "";
        
        PreferenceRegistry reg = (PreferenceRegistry)getFormObject();
        ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
        for (Preference p : prefs) {
            if (!p.getGroup().equals(group)) {
                tlb.separator(p.getGroup().replace(".", " - "));
                tlb.relatedGapRow();
                group = p.getGroup();
            }
            tlb.cell(new JLabel(p.getDescription()));
            tlb.gapCol();
            JComboBox combo = new JComboBox();
            combo.setPreferredSize(new Dimension(100, 20));
            for (PreferenceValue pv : p.getDomain()) {
                combo.addItem(pv.getDescription());
                if (reg.getPreferenceValue(p.getKey()).equals(pv.getKey())) {
                    combo.setSelectedItem(pv.getDescription());
                }
            }
            components.put(p.getKey(), combo);
            tlb.cell(combo);
            tlb.relatedGapRow();
        }

        panel = tlb.getPanel();
        JScrollPane scp = new JScrollPane(panel);
        scp.setPreferredSize(new Dimension(300, 500));
        return scp;
    }

    public void commit() {
        super.commit();
        PreferenceRegistry reg = (PreferenceRegistry)getFormObject();
        ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
        for (Preference p : prefs) {
            JComboBox combo = components.get(p.getKey());
            if (combo.getSelectedItem() != null) {
                for (PreferenceValue pv : p.getDomain()) {
                    if (pv.getDescription().equals(combo.getSelectedItem().toString())) {
                        reg.setPreferenceValue(p.getKey(), pv.getKey());
                    }
                }
            }
        }
    }
    
    
    
}
