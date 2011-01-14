package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.popup.JidePopup;

/**
 * Form for editing nation metadata
 * 
 * The form is created dynamically from nation metadata
 * 
 * @author Marios Skounakis
 */
public class EditNationMetadataForm extends AbstractForm {
	public static String FORM_ID = "editNationMetadataForm";

	ArrayList<JTextField> nationNames = new ArrayList<JTextField>();
	ArrayList<JTextField> nationShortNames = new ArrayList<JTextField>();
	ArrayList<JTextField> nationSNAs = new ArrayList<JTextField>();
	ArrayList<JCheckBox> removed = new ArrayList<JCheckBox>();
	ArrayList<JButton> editSNAButtons = new ArrayList<JButton>();
	ArrayList<JLabel> labels = new ArrayList<JLabel>();

	ArrayList<JidePopup> popups = new ArrayList<JidePopup>();

	HashMap<JidePopup, HashMap<SNAEnum, JCheckBox>> snaCheckBoxes = new HashMap<JidePopup, HashMap<SNAEnum, JCheckBox>>();
	ArrayList<JTextField> colorLabels = new ArrayList<JTextField>();

	public EditNationMetadataForm(FormModel arg0) {
		super(arg0, FORM_ID);

	}

	protected String[] getSNAList() {
		ArrayList<String> snas = new ArrayList<String>();
		for (SNAEnum sna : SNAEnum.values()) {
			snas.add(sna.toString());
		}
		return snas.toArray(new String[] {});
	}

	protected void updateSNATextFieldFromCheckBoxes(int i) {
		JTextField nationSNATextBox = nationSNAs.get(i);
		HashMap<SNAEnum, JCheckBox> checkboxes = snaCheckBoxes.get(popups.get(i));
		nationSNATextBox.setText("");
		for (SNAEnum sna : SNAEnum.values()) {
			if (checkboxes.get(sna).isSelected()) {
				nationSNATextBox.setText(nationSNATextBox.getText() + (nationSNATextBox.getText().equals("") ? "" : ", ") + sna.getDescription());
			}
		}
		nationSNATextBox.setCaretPosition(0);
	}

	@Override
	protected JComponent createFormControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();

		tlb.cell(new JLabel(" "));
		tlb.gapCol();
		tlb.cell(new JLabel("Name"));
		tlb.gapCol();
		tlb.cell(new JLabel("Short Name"));
		tlb.gapCol();
		tlb.cell(new JLabel("Removed"));
		tlb.gapCol();
		tlb.cell(new JLabel("SNAs"));
		tlb.gapCol();
		tlb.cell();
		tlb.gapCol();
		tlb.cell(new JLabel("Color"));
		tlb.relatedGapRow();
		tlb.row();
		for (int i = 0; i < 25; i++) {
			JTextField nationName = new JTextField();
			nationNames.add(nationName);
			nationName.setPreferredSize(new Dimension(170, 20));
			JLabel lbl = new JLabel();
			lbl.setPreferredSize(new Dimension(60, 24));
			lbl.setText("Nation " + (i + 1) + " :");
			labels.add(lbl);
			tlb.cell(lbl);
			tlb.cell(nationName);
			JTextField nationShortName = new JTextField();
			nationShortNames.add(nationShortName);
			nationShortName.setPreferredSize(new Dimension(80, 20));
			tlb.gapCol();
			tlb.cell(nationShortName);

			JCheckBox rem = new JCheckBox();
			rem.setText("");
			tlb.gapCol();
			tlb.cell(rem, "align=center");
			removed.add(rem);

			JTextField nationSNAList = new JTextField();
			nationSNAList.setEditable(false);
			nationSNAs.add(nationSNAList);
			nationSNAList.setPreferredSize(new Dimension(250, 20));
			tlb.gapCol();
			tlb.cell(nationSNAList);

			JButton editNationSNAs = new JButton("Edit");
			editSNAButtons.add(editNationSNAs);
			editNationSNAs.setPreferredSize(new Dimension(50, 16));
			tlb.gapCol();
			tlb.cell(editNationSNAs);
			final JidePopup popup = new JidePopup();
			// create popup
			popup.getContentPane().setLayout(new BorderLayout());
			final HashMap<SNAEnum, JCheckBox> checkboxes = new HashMap<SNAEnum, JCheckBox>();
			snaCheckBoxes.put(popup, checkboxes);
			TableLayoutBuilder lb = new TableLayoutBuilder();
			for (SNAEnum sna : SNAEnum.values()) {
				JCheckBox checkBox = new JCheckBox(sna.getDescription());
				lb.cell(checkBox);
				lb.row();
				checkboxes.put(sna, checkBox);
				checkBox.addActionListener(new ActionListener() {
					// checkbox value changed, update the textfield
					public void actionPerformed(ActionEvent e) {
						int i = popups.indexOf(popup);
						updateSNATextFieldFromCheckBoxes(i);
					}
				});
			}
			// create button to close the popup
			JButton closePopup = new JButton("Close");
			closePopup.setPreferredSize(new Dimension(70, 20));
			closePopup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					popup.hidePopup();
				}
			});
			lb.cell(closePopup, "align=left");
			lb.relatedGapRow();

			JScrollPane scp = new JScrollPane(lb.getPanel());
			scp.setPreferredSize(new Dimension(200, 300));
			scp.getVerticalScrollBar().setUnitIncrement(16);
			popup.getContentPane().add(scp);
			popups.add(popup);

			// add action listener for showing the popup
			editNationSNAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = editSNAButtons.indexOf(e.getSource());
					JidePopup popup = popups.get(i);
					popup.updateUI();
					popup.setOwner((Component) e.getSource());
					popup.setResizable(true);
					popup.setMovable(true);
					if (popup.isPopupVisible()) {
						popup.hidePopup();
					} else {
						popup.showPopup();
					}
				}
			});
			tlb.gapCol();

			final JTextField color = new JTextField("Col");
			// color.setBorder(new LineBorder(Color.black));
			color.setPreferredSize(new Dimension(34, 16));
			color.setHorizontalAlignment(JLabel.CENTER);
			colorLabels.add(color);
			color.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					super.mouseClicked(arg0);
					JColorChooser.showDialog(color, "Choose Color", color.getBackground());
				}

			});
			tlb.cell(color);

			tlb.row();
		}
		// trick to make sure line 25 is showing
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.row();
		return new JScrollPane(tlb.getPanel());
	}

	@Override
	public void commit() {
		super.commit();
		GameMetadata gm = (GameMetadata) getFormObject();
		for (int i = 1; i < 26; i++) {
			if (i < gm.getNations().size()) {
				Nation n = gm.getNations().get(i);
				n.setName(nationNames.get(i - 1).getText());
				n.setShortName(nationShortNames.get(i - 1).getText());
				n.setRemoved(removed.get(i - 1).isSelected());
				HashMap<SNAEnum, JCheckBox> checkboxes = snaCheckBoxes.get(popups.get(i - 1));
				for (SNAEnum sna : SNAEnum.values()) {
					if (checkboxes.get(sna).isSelected() && !n.getSnas().contains(sna)) {
						n.getSnas().add(sna);
					} else if (!checkboxes.get(sna).isSelected() && n.getSnas().contains(sna)) {
						n.getSnas().remove(sna);
					}
				}
			}
		}
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		GameMetadata gm = (GameMetadata) arg0;
		for (int i = 1; i < 26; i++) {
			if (i < gm.getNations().size()) {
				Nation n = gm.getNations().get(i);
				nationNames.get(i - 1).setText(n.getName());
				nationShortNames.get(i - 1).setText(n.getShortName());
				HashMap<SNAEnum, JCheckBox> checkboxes = snaCheckBoxes.get(popups.get(i - 1));
				for (SNAEnum sna : SNAEnum.values()) {
					checkboxes.get(sna).setSelected(n.getSnas().contains(sna));
				}
				removed.get(i - 1).setSelected(n.getRemoved());
				updateSNATextFieldFromCheckBoxes(i - 1);
				JTextField color = colorLabels.get(i - 1);
				color.setBackground(ColorPicker.getInstance().getColor1(i));
				color.setForeground(ColorPicker.getInstance().getColor2(i));
			} else {
				labels.get(i - 1).setEnabled(false);
				nationNames.get(i - 1).setEnabled(false);
				nationShortNames.get(i - 1).setEnabled(false);
				removed.get(i - 1).setEnabled(false);
				nationSNAs.get(i - 1).setEnabled(false);
			}
		}
	}
}
