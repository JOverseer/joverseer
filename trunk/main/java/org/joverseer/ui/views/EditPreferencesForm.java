package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.joverseer.preferences.Preference;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.preferences.PreferenceValue;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.swing.JideTabbedPane;

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
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.LEFT);
        TableLayoutBuilder tlb = null;
        String group = "";
        
        PreferenceRegistry reg = (PreferenceRegistry)getFormObject();
        // sort prefs by group
        ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
        for (Preference p : prefs) {
            if (!p.getGroup().equals(group)) {
            	if (tlb != null) {
            		//add to tabPane
            		String tabName = group.replace(".", " - ");
            		
            		tabPane.addTab(tabName, null, tlb.getPanel(), tabName);
            	}
            	tlb = new TableLayoutBuilder();
                // if new group, show separator
                tlb.separator(p.getGroup().replace(".", " - "));
                tlb.relatedGapRow();
                group = p.getGroup();
            }
            // show pref label
            tlb.gapCol();
            tlb.cell(new JLabel(p.getDescription()), "colspec=left:220px");
            tlb.gapCol();
            // show control for editing pref, based on pref type
            if (p.getType().equals(Preference.TYPE_DROPDOWN)) {
	            JComboBox combo = new JComboBox();
	            combo.setPreferredSize(new Dimension(190, 20));
	            for (PreferenceValue pv : p.getDomain()) {
	                combo.addItem(pv.getDescription());
                        // find the appriate combo box item from the key
	                if (reg.getPreferenceValue(p.getKey()).equals(pv.getKey())) {
	                    combo.setSelectedItem(pv.getDescription());
	                }
	            }
	            components.put(p.getKey(), combo);
	            tlb.cell(combo, "colspec=left:200px");
            } else {
            	JTextField tf = new JTextField();
            	tf.setPreferredSize(new Dimension(190, 20));
            	tf.setText(reg.getPreferenceValue(p.getKey()));
            	components.put(p.getKey(), tf);
            	tlb.cell(tf, "colspec=left:200px");
            }
            
            tlb.gapCol();
            tlb.relatedGapRow();
        }
        
        // add last group
        String tabName = group.replace(".", " - ");
        tabPane.addTab(tabName, null, tlb.getPanel(), tabName);

        //panel = tlb.getPanel();
        //panel = tabPane;
        //JScrollPane scp = new JScrollPane(panel);
        JScrollPane scp = new JScrollPane(tabPane);
        scp.setPreferredSize(new Dimension(650, 350));
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
