package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.joverseer.preferences.Preference;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.preferences.PreferenceValue;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Preferences form
 * 
 * Generated dynamically from the existing preferences 
 * @author Marios Skounakis
 */
public class EditPreferencesForm extends AbstractForm {
    public static String FORM_ID = "editPreferencesForm";
    JPanel panel;
    HashMap<String, JComponent> components = new HashMap<String, JComponent>();
    
    public EditPreferencesForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        
        String group = "";
        
        PreferenceRegistry reg = (PreferenceRegistry)getFormObject();
        // sort prefs by group
        ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
        for (Preference p : prefs) {
            if (!p.getGroup().equals(group)) {
                // if new group, show separator
                tlb.separator(p.getGroup().replace(".", " - "));
                tlb.relatedGapRow();
                group = p.getGroup();
            }
            // show pref label
            tlb.cell(new JLabel(p.getDescription()), "colspec=left:270px");
            tlb.gapCol();
            // show control for editing pref, based on pref type
            if (p.getType().equals(Preference.TYPE_DROPDOWN)) {
	            JComboBox combo = new JComboBox();
	            combo.setPreferredSize(new Dimension(150, 20));
	            for (PreferenceValue pv : p.getDomain()) {
	                combo.addItem(pv.getDescription());
                        // find the appriate combo box item from the key
	                if (reg.getPreferenceValue(p.getKey()).equals(pv.getKey())) {
	                    combo.setSelectedItem(pv.getDescription());
	                }
	            }
	            components.put(p.getKey(), combo);
	            tlb.cell(combo);
            } else {
            	JTextField tf = new JTextField();
            	tf.setPreferredSize(new Dimension(150, 20));
            	tf.setText(reg.getPreferenceValue(p.getKey()));
            	components.put(p.getKey(), tf);
            	tlb.cell(tf, "colspec=left:170px");
            }
            tlb.relatedGapRow();
        }

        panel = tlb.getPanel();
        JScrollPane scp = new JScrollPane(panel);
        scp.setPreferredSize(new Dimension(500, 700));
        return scp;
    }

    public void commit() {
        super.commit();
        PreferenceRegistry reg = (PreferenceRegistry)getFormObject();
        ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
        for (Preference p : prefs) {
            JComponent c = components.get(p.getKey());
            if (p.getType().equals(Preference.TYPE_DROPDOWN)) {
            	JComboBox combo = (JComboBox)c;
	            if (combo.getSelectedItem() != null) {
	                for (PreferenceValue pv : p.getDomain()) {
                            // translate the selected combo box value to a preference value key
	                    if (pv.getDescription().equals(combo.getSelectedItem().toString())) {
	                        reg.setPreferenceValue(p.getKey(), pv.getKey());
	                    }
	                }
	            }
            } else {
            	JTextField tf = (JTextField)c;
            	reg.setPreferenceValue(p.getKey(), tf.getText());
            }
        }
    }
    
    
    
}
