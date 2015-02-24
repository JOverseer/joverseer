package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.controls.ResourceButton;
import org.joverseer.ui.support.controls.ResourceLabel;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
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
public class EditNationMetadataForm extends ScalableAbstractForm {
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
		JTextField nationSNATextBox = this.nationSNAs.get(i);
		HashMap<SNAEnum, JCheckBox> checkboxes = this.snaCheckBoxes.get(this.popups.get(i));
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
		tlb.cell(new ResourceLabel("standardFields.Name"));
		tlb.gapCol();
		tlb.cell(new ResourceLabel("editNationMetadataForm.ShortName"));
		tlb.gapCol();
		tlb.cell(new ResourceLabel("editNationMetadataForm.Removed"));
		tlb.gapCol();
		tlb.cell(new ResourceLabel("editNationMetadataForm.SNAs"));
		tlb.gapCol();
		tlb.cell();
		// tlb.gapCol();
		// tlb.cell(new ResourceLabel("Color"));
		tlb.relatedGapRow();
		tlb.row();
		for (int i = 0; i < 25; i++) {
			JTextField nationName = new JTextField();
			this.nationNames.add(nationName);
			nationName.setPreferredSize(this.uiSizes.newDimension(170/20, this.uiSizes.getHeight5()));
			JLabel lbl = new JLabel();
			lbl.setPreferredSize(this.uiSizes.newDimension(60/24, this.uiSizes.getHeight6()));
			lbl.setText(Application.instance().getApplicationContext().getMessage("editNationMetadataForm.NationLabel", new Object[] { (i + 1) }, null));
			this.labels.add(lbl);
			tlb.cell(lbl);
			tlb.cell(nationName);
			JTextField nationShortName = new JTextField();
			this.nationShortNames.add(nationShortName);
			nationShortName.setPreferredSize(this.uiSizes.newDimension(80/20, this.uiSizes.getHeight5()));
			tlb.gapCol();
			tlb.cell(nationShortName);

			JCheckBox rem = new JCheckBox();
			rem.setText("");
			tlb.gapCol();
			tlb.cell(rem, "align=center");
			this.removed.add(rem);

			JTextField nationSNAList = new JTextField();
			nationSNAList.setEditable(false);
			this.nationSNAs.add(nationSNAList);
			nationSNAList.setPreferredSize(this.uiSizes.newDimension(250/20, this.uiSizes.getHeight5()));
			tlb.gapCol();
			tlb.cell(nationSNAList);

			JButton editNationSNAs = new ResourceButton("standardActions.Edit");
			this.editSNAButtons.add(editNationSNAs);
			editNationSNAs.setPreferredSize(this.uiSizes.newDimension(50/16, this.uiSizes.getHeight4()));
			tlb.gapCol();
			tlb.cell(editNationSNAs);
			final JidePopup popup = new JidePopup();
			// create popup
			popup.getContentPane().setLayout(new BorderLayout());
			final HashMap<SNAEnum, JCheckBox> checkboxes = new HashMap<SNAEnum, JCheckBox>();
			this.snaCheckBoxes.put(popup, checkboxes);
			TableLayoutBuilder lb = new TableLayoutBuilder();
			for (SNAEnum sna : SNAEnum.values()) {
				JCheckBox checkBox = new JCheckBox(sna.getDescription());
				lb.cell(checkBox);
				lb.row();
				checkboxes.put(sna, checkBox);
				checkBox.addActionListener(new ActionListener() {
					// checkbox value changed, update the textfield
					@Override
					public void actionPerformed(ActionEvent e) {
						int i1 = EditNationMetadataForm.this.popups.indexOf(popup);
						updateSNATextFieldFromCheckBoxes(i1);
					}
				});
			}
			// create button to close the popup
			JButton closePopup = new JButton("Close");
			closePopup.setPreferredSize(this.uiSizes.newDimension(70/20, this.uiSizes.getHeight5()));
			closePopup.addActionListener(new ActionListener() {
				@Override
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
			this.popups.add(popup);

			// add action listener for showing the popup
			editNationSNAs.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int i1 = EditNationMetadataForm.this.editSNAButtons.indexOf(e.getSource());
					JidePopup popup1 = EditNationMetadataForm.this.popups.get(i1);
					popup1.updateUI();
					popup1.setOwner((Component) e.getSource());
					popup1.setResizable(true);
					popup1.setMovable(true);
					if (popup1.isPopupVisible()) {
						popup1.hidePopup();
					} else {
						popup1.showPopup();
					}
				}
			});
			tlb.gapCol();

			// final JTextField color = new JTextField("Col");
			// // color.setBorder(new LineBorder(Color.black));
			// color.setPreferredSize(new Dimension(34, 16));
			// color.setHorizontalAlignment(JLabel.CENTER);
			// colorLabels.add(color);
			// color.addMouseListener(new MouseAdapter() {
			//
			// @Override
			// public void mouseClicked(MouseEvent arg0) {
			// super.mouseClicked(arg0);
			// JColorChooser.showDialog(color, "Choose Color",
			// color.getBackground());
			// }
			//
			// });
			// tlb.cell(color);

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
				n.setName(this.nationNames.get(i - 1).getText());
				n.setShortName(this.nationShortNames.get(i - 1).getText());
				n.setRemoved(this.removed.get(i - 1).isSelected());
				HashMap<SNAEnum, JCheckBox> checkboxes = this.snaCheckBoxes.get(this.popups.get(i - 1));
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
				this.nationNames.get(i - 1).setText(n.getName());
				this.nationShortNames.get(i - 1).setText(n.getShortName());
				HashMap<SNAEnum, JCheckBox> checkboxes = this.snaCheckBoxes.get(this.popups.get(i - 1));
				for (SNAEnum sna : SNAEnum.values()) {
					checkboxes.get(sna).setSelected(n.getSnas().contains(sna));
				}
				this.removed.get(i - 1).setSelected(n.getRemoved());
				updateSNATextFieldFromCheckBoxes(i - 1);
				// JTextField color = colorLabels.get(i - 1);
				// color.setBackground(ColorPicker.getInstance().getColor1(i));
				// color.setForeground(ColorPicker.getInstance().getColor2(i));
			} else {
				this.labels.get(i - 1).setEnabled(false);
				this.nationNames.get(i - 1).setEnabled(false);
				this.nationShortNames.get(i - 1).setEnabled(false);
				this.removed.get(i - 1).setEnabled(false);
				this.nationSNAs.get(i - 1).setEnabled(false);
			}
		}
	}
}
