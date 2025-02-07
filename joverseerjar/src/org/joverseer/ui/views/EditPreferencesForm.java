package org.joverseer.ui.views;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.joverseer.preferences.Preference;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.preferences.PreferenceValue;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.PLaFHelper;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor;

/**
 * Preferences form
 * 
 * Generated dynamically from the existing preferences
 * 
 * @author Marios Skounakis
 */
public class EditPreferencesForm extends ScalableAbstractForm {
	public static String FORM_ID = "editPreferencesForm";
	// a local copy so we can select the appropriate tab
	JTabbedPane tabbedPanel;
	//holds the preferences
	HashMap<String, JComponent> components = new HashMap<String, JComponent>();
	private String startingGroup;
	private PLaFHelper plaf;
	// note ready for primetime.
	private boolean enablePlaf=false;

	public String getStartingGroup() {
		return this.startingGroup;
	}

	public void setStartingGroup(String startingGroup) {
		this.startingGroup = startingGroup;
	}

	public EditPreferencesForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	protected JComponent createFormControl() {
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.setTabPlacement(SwingConstants.LEFT);
		TableLayoutBuilder tlb = null;
		String group = "";
		if (this.enablePlaf) {
			this.plaf = new PLaFHelper();
		}
		PreferenceRegistry reg = (PreferenceRegistry) getFormObject();
		// sort prefs by group
		ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
		for (Preference p : prefs) {
			if (!p.getGroup().equals(group)) {
				if (tlb != null) {
					// add to tabPane
					tlb.relatedGapRow();
					tlb.relatedGapRow();
					
					String tabName = group.replace(".", " - ");

					tabPane.addTab(tabName, null, tlb.getPanel(), tabName);
				}
				
				tlb = new TableLayoutBuilder();
				// if new group, show separator
				tlb.separator(p.getGroup().replace(".", " - "));
				tlb.relatedGapRow();
				group = p.getGroup();
			}
			if (tlb != null) {
				// show pref label
				tlb.gapCol();
				tlb.cell(new JLabel(p.getDescription()), "colspec=left:220px");
				tlb.gapCol();
				// show control for editing pref, based on pref type
				if (p.getType().equals(Preference.TYPE_DROPDOWN)) {
					JComboBox combo = new JComboBox();
					combo.setPreferredSize(this.uiSizes.newDimension(190 / 20, this.uiSizes.getHeight5()));
					for (PreferenceValue pv : p.getDomain()) {
						combo.addItem(pv.getDescription());
						// find the appropriate combo box item from the key
						if (reg.getPreferenceValue(p.getKey()).equals(pv.getKey())) {
							combo.setSelectedItem(pv.getDescription());
						}
					}
					this.components.put(p.getKey(), combo);
					tlb.cell(combo, "colspec=left:200px");
				} else if (p.getType().equals(Preference.TYPE_CHECKBOX)) {
					JCheckBox check = new JCheckBox();
					check.setSelected(reg.getPreferenceValue(p.getKey()).equals("yes"));
					this.components.put(p.getKey(), check);
					tlb.cell(check);
				} else if (p.getType().equals(Preference.TYPE_LAF)) {
					if (this.enablePlaf) {
						JComboBox combo = new JComboBox();
						this.plaf.fill(combo);
						combo.setSelectedItem(this.plaf.nameFromClass(reg.getPreferenceValue(p.getKey())));
						this.components.put(p.getKey(), combo);
						tlb.cell(combo, "colspec=left:200px");
					}
				}
				else {
					JTextField tf = new JTextField();
					tf.setPreferredSize(this.uiSizes.newDimension(190 / 20, this.uiSizes.getHeight5()));
					tf.setText(reg.getPreferenceValue(p.getKey()));
					this.components.put(p.getKey(), tf);
					tlb.cell(tf, "colspec=left:200px");
				}

				tlb.gapCol();
				tlb.relatedGapRow();
			}
		}

		if (tlb != null) {
			// add last group
			String tabName = group.replace(".", " - ");
			tabPane.addTab(tabName, null, tlb.getPanel(), tabName);
		}

		this.tabbedPanel = tabPane;
		if (this.startingGroup != null) {
			String tabName = this.startingGroup.replace(".", " - ");
			if (this.tabbedPanel != null) {
				int index = this.tabbedPanel.indexOfTab(tabName);
				if (index > -1) {
					this.tabbedPanel.setSelectedIndex(index);
				}
			}
		}
		JScrollPane scp = new JScrollPane(tabPane);
		scp.setPreferredSize(new Dimension(650, 450));
		return scp;
	}

	@Override
	public void commit() {
		super.commit();
		PreferenceRegistry reg = (PreferenceRegistry) getFormObject();
		ArrayList<Preference> prefs = reg.getPreferencesSortedByGroup();
		for (Preference p : prefs) {
			JComponent c = this.components.get(p.getKey());
			if (p.getType().equals(Preference.TYPE_DROPDOWN)) {
				JComboBox combo = (JComboBox) c;
				if (combo.getSelectedItem() != null) {
					for (PreferenceValue pv : p.getDomain()) {
						// translate the selected combo box value to a
						// preference value key
						if (pv.getDescription().equals(combo.getSelectedItem().toString())) {
							reg.setPreferenceValue(p.getKey(), pv.getKey());
						}
					}
				}
			} else if (p.getType().equals(Preference.TYPE_CHECKBOX)) {
				JCheckBox check = (JCheckBox) c;
				if (check.isSelected()) {
					reg.setPreferenceValue(p.getKey(), "yes");
				} else {
					reg.setPreferenceValue(p.getKey(), "no");
				}
			} else if (p.getType().equals(Preference.TYPE_LAF)) {
				if (this.enablePlaf) {
					JComboBox combo = (JComboBox) c;
					if (combo.getSelectedItem() != null) {
						String sel=combo.getSelectedItem().toString();
						try {
							UIManager.setLookAndFeel(this.plaf.fullClassFromName(sel));
							this.plaf.updateAll();
							// only update the preference if it worked.
							reg.setPreferenceValue(p.getKey(), this.plaf.fullClassFromName(sel));
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
								| UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}
				}
		    } else {
				JTextField tf = (JTextField) c;
				reg.setPreferenceValue(p.getKey(), tf.getText());
			}
		}
		JideApplicationLifecycleAdvisor advisor = (JideApplicationLifecycleAdvisor) Application.instance().getLifecycleAdvisor();
		advisor.refreshClearMapItemsVisibility();
	}
}
