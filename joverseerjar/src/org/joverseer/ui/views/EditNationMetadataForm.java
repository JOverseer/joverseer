package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.CustomColourSetsManager;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.image.ImageSource;
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

	ArrayList<JLabel> icons = new ArrayList<JLabel>();
	ArrayList<JButton> colour1 = new ArrayList<JButton>();
	ArrayList<JButton> colour2 = new ArrayList<JButton>();
	
	HashMap<JidePopup, HashMap<SNAEnum, JCheckBox>> snaCheckBoxes = new HashMap<JidePopup, HashMap<SNAEnum, JCheckBox>>();
	//ArrayList<JTextField> colorLabels = new ArrayList<JTextField>();


	public JComboBox colourSet;
	public String gameNumber;
	public GameTypeEnum gameType;

	private String defaultColourSet;

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

		tlb.cell(new JLabel(Messages.getString("standardFields.Nation")));
		tlb.gapCol();
		tlb.cell(new JLabel(Messages.getString("standardFields.Name")));
		tlb.gapCol();
		tlb.cell(new JLabel(Messages.getString("editNationMetadataForm.ShortName")));
		tlb.gapCol();
		tlb.cell(new JLabel("Colour Scheme"));
		tlb.gapCol();
		tlb.cell(new JLabel(Messages.getString("editNationMetadataForm.Removed")));
		tlb.gapCol();
		tlb.cell(new JLabel(Messages.getString("editNationMetadataForm.SNAs")));
		tlb.gapCol();
		tlb.cell();
		/*
		tlb.gapCol();
		tlb.cell(new JLabel("Color"));
		*/
		
		tlb.relatedGapRow();
		tlb.row();
		
		for (int i = 0; i < 25; i++) {
			JTextField nationName = new JTextField();
			this.nationNames.add(nationName);
			nationName.setPreferredSize(this.uiSizes.newDimension(170/20, this.uiSizes.getHeight5()));
			JLabel lbl = new JLabel();
			lbl.setPreferredSize(this.uiSizes.newDimension(60/24, this.uiSizes.getHeight6()));
			lbl.setText(String.valueOf(i + 1));
			this.labels.add(lbl);
			tlb.cell(lbl);
			tlb.cell(nationName);
			JTextField nationShortName = new JTextField();
			this.nationShortNames.add(nationShortName);
			nationShortName.setPreferredSize(this.uiSizes.newDimension(80/20, this.uiSizes.getHeight5()));
			tlb.gapCol();
			tlb.cell(nationShortName);
			
			JPanel nationColourPanel = new JPanel();
			//nationColourPanel.setLayout(new BoxLayout(nationColourPanel, BoxLayout.X_AXIS));
			//nationColourPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			nationColourPanel.setLayout(new FlowLayout());
			
			Icon ico = PCRenderer.getNationPC(i);
			JLabel pcIcon = new JLabel(ico);
			this.icons.add(pcIcon);
			nationColourPanel.add(pcIcon);
			
//			JPanel smallButtonPanel = new JPanel();
//			smallButtonPanel.setLayout(new BoxLayout(smallButtonPanel, BoxLayout.Y_AXIS));
//			nationColourPanel.add(smallButtonPanel);
			
			JButton b1 = new JButton();
			b1.setRolloverEnabled(false);
			b1.setPreferredSize(new Dimension(20, 20));
			
			//JColorChooser c1 = this.makeColorChooser(b1.getBackground());
			b1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//JColorChooser c1 = EditNationMetadataForm.this.makeColorChooser(b1.getBackground());
					Color newC = JColorChooser.showDialog(EditNationMetadataForm.this.getControl(), "Colour 1",((JButton)e.getSource()).getBackground());
					if(newC.getRGB() == -16777216) newC = new Color(-16777215);
					CustomColourSetsManager.setColor1((String)EditNationMetadataForm.this.colourSet.getSelectedItem(), EditNationMetadataForm.this.colour1.indexOf(((JButton)e.getSource())) + 1, String.valueOf(newC.getRGB()));
					EditNationMetadataForm.this.applyColourSet();
				}
			});
			
			this.colour1.add(b1);
			nationColourPanel.add(b1);
			
			JButton b2 = new JButton();
			b2.setRolloverEnabled(false);
			b2.setPreferredSize(new Dimension(20, 20));
			b2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//JColorChooser c2 = EditNationMetadataForm.this.makeColorChooser(b2.getBackground());
					Color newC = JColorChooser.showDialog(EditNationMetadataForm.this.getControl(), "Colour 2",((JButton)e.getSource()).getBackground());
					if(newC.getRGB() == -16777216) newC = new Color(-16777215);
					CustomColourSetsManager.setColor2((String)EditNationMetadataForm.this.colourSet.getSelectedItem(), EditNationMetadataForm.this.colour2.indexOf(((JButton)e.getSource())) + 1, String.valueOf(newC.getRGB()));
					EditNationMetadataForm.this.applyColourSet();
				}
				
			});
			
			this.colour2.add(b2);
			nationColourPanel.add(b2);
			
			tlb.gapCol();
			tlb.cell(nationColourPanel);

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

			JButton editNationSNAs = new JButton(Messages.getString("standardActions.Edit"));
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

			/*
			final JTextField color = new JTextField("Click here");
			// color.setBorder(new LineBorder(Color.black));
			color.setPreferredSize(new Dimension(34, 20));
			color.setHorizontalAlignment(SwingConstants.CENTER);
			this.colorLabels.add(color);
			color.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			super.mouseClicked(arg0);
			Color newColor = JColorChooser.showDialog(color, "Choose Color",
					color.getBackground());
			if (newColor!=null) {
				color.setBackground(newColor);
				ColorPicker.getInstance().getColor1().put(i, newColor);
			}
			}
			
			});
			tlb.cell(color);
			*/
			tlb.row();
			
		}
		// trick to make sure line 25 is showing
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.row();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.relatedGapRow();
		tlb.row();

		JScrollPane scp = new JScrollPane(tlb.getPanel());

		scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scp.setPreferredSize(new Dimension(780,550));
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(0,5));
		
		p.add(scp, BorderLayout.CENTER);
		
		JPanel bottomP = new JPanel();
		bottomP.setLayout(new BoxLayout(bottomP, BoxLayout.X_AXIS));
		
		JLabel lb = new JLabel("Colour Set: ");
		bottomP.add(lb);
		
		Component spaceStrut = Box.createHorizontalStrut(5);
		bottomP.add(spaceStrut);
		
		this.colourSet = new JComboBox();
		this.colourSet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				((GameMetadata) EditNationMetadataForm.this.getFormObject()).setColourSet((String) EditNationMetadataForm.this.colourSet.getSelectedItem());
				
				EditNationMetadataForm.this.applyColourSet();
			}
			
		});
		bottomP.add(this.colourSet);
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		bottomP.add(horizontalStrut);
		
		JButton addSetBt = new JButton(new AddColourSet());
		addSetBt.setIcon(new ImageIcon(JOApplication.getImageSource().getImage("add.icon"))); //$NON-NLS-1$)
		bottomP.add(addSetBt);
		
		Component horizontalStrut1 = Box.createHorizontalStrut(5);
		bottomP.add(horizontalStrut1);
		
		JButton editSetBt = new JButton(new EditColourSetName());
		editSetBt.setIcon(new ImageIcon(JOApplication.getImageSource().getImage("edit.image"))); //$NON-NLS-1$)
		bottomP.add(editSetBt);
		
		Component horizontalStrut2 = Box.createHorizontalStrut(5);
		bottomP.add(horizontalStrut2);
		
		JButton deleteBt = new JButton(new DeleteColourSet());
		deleteBt.setIcon(new ImageIcon(JOApplication.getImageSource().getImage("remove.icon")));
		bottomP.add(deleteBt);
		
		//Can't get file renaming to work
//		bottomP.add(horizontalStrut);
//		
//		JButton editSetBt = new JButton(new EditColourSetName());
//		bottomP.add(editSetBt);
		
		p.add(bottomP, BorderLayout.PAGE_END);
		return p;
	}
	
	public void setColourDropDown(String selected) {
		System.out.println(this.gameNumber +  " " + this.gameType);
		ArrayList<String> sets;
		if (((GameMetadata) this.getFormObject()).getColourSet() == null) sets = CustomColourSetsManager.getColourSets(this.gameNumber, this.gameType);
		else sets = CustomColourSetsManager.getColourSets();
		this.colourSet.setModel(new DefaultComboBoxModel(sets.toArray()));
		if(selected != null) {
			if(sets.contains(selected)) this.colourSet.setSelectedItem(selected);
			return;
		}
		if(this.defaultColourSet != null) {
			if(sets.contains(this.defaultColourSet)) this.colourSet.setSelectedItem(this.defaultColourSet);
			return;
		}
		if(this.gameNumber != null) {
			if(sets.contains(this.gameNumber)) this.colourSet.setSelectedItem(this.gameNumber);
		}
		
		((GameMetadata) this.getFormObject()).setColourSet((String) this.colourSet.getSelectedItem());
		//CustomColourSetsManager.getColor1(this.gameType.name(), 2);
	}
	
	public void setColourDropDown() {
		this.setColourDropDown(null);
	}
	
	public void applyColourSet() {
		for(int j = 0; j < this.icons.size(); j++) {
			int i = j + 1;
			this.icons.get(j).setIcon(PCRenderer.getNationPC(i));
			this.colour1.get(j).setBackground(PCRenderer.getColor1(i));
			this.colour2.get(j).setBackground(PCRenderer.getColor2(i));
		}
		
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
		PreferenceRegistry.instance().setPreferenceValue("map.nationColors", "custom");
		Game g = JOApplication.getGame();
        JOApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, g, g);
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		GameMetadata gm = (GameMetadata) arg0;
		this.gameNumber = String.valueOf(gm.getGameNo());
		this.gameType = gm.getGameType();
		this.defaultColourSet = gm.getColourSet();
		this.setColourDropDown(this.defaultColourSet);
		this.applyColourSet();
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
				/*
				JTextField color = colorLabels.get(i - 1);
				color.setBackground(ColorPicker.getInstance().getColor1(i));
				color.setForeground(ColorPicker.getInstance().getColor2(i));
				*/
			} else {
				this.labels.get(i - 1).setEnabled(false);
				this.nationNames.get(i - 1).setEnabled(false);
				this.nationShortNames.get(i - 1).setEnabled(false);
				this.removed.get(i - 1).setEnabled(false);
				this.nationSNAs.get(i - 1).setEnabled(false);
			}
		}
	}
	
	class AddColourSet extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public AddColourSet() {
			super("New Set");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String newTitle;
			do {
				newTitle = (String)JOptionPane.showInputDialog("Input a new Colour Set title that doesn't currently exist:", EditNationMetadataForm.this.gameNumber);
			} while(CustomColourSetsManager.getColourSets().contains(newTitle));
			CustomColourSetsManager.createNewColourSetFile(newTitle, EditNationMetadataForm.this.gameType);
			EditNationMetadataForm.this.setColourDropDown(newTitle);
			EditNationMetadataForm.this.applyColourSet();
		}
		
	}
	
	class DeleteColourSet extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public DeleteColourSet() {
			super("Delete Set");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String name = (String)EditNationMetadataForm.this.colourSet.getSelectedItem();
			int del = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete " + name);
			
			if(del == JOptionPane.YES_OPTION) System.out.println(CustomColourSetsManager.deleteFile(name));
			
			EditNationMetadataForm.this.setColourDropDown();
			EditNationMetadataForm.this.applyColourSet();
		}
		
	}
	
	class EditColourSetName extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public EditColourSetName() {
			super("Edit Name");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String newTitle;
			do {
				newTitle = (String)JOptionPane.showInputDialog("Input a new Colour Set title that doesn't currently exist:", EditNationMetadataForm.this.colourSet.getSelectedItem());
			} while(CustomColourSetsManager.getColourSets().contains(newTitle));
			if (newTitle == null) return;
			System.out.println(CustomColourSetsManager.renameFile((String)EditNationMetadataForm.this.colourSet.getSelectedItem(), newTitle));
			EditNationMetadataForm.this.setColourDropDown(newTitle);
		}
		
	}
	
	static class PCRenderer {
		static ImageSource imgSource = JOApplication.getImageSource();
		static String PC_SIZE_PATH = "majorTown.image";
		
		public static Icon getNationPC(int nationNo) {
			BufferedImage im = toBufferedImage(imgSource.getImage(PC_SIZE_PATH));
	        im = changeColor(im, Color.red, getColor1(nationNo));
	        im = changeColor(im, Color.black, getColor2(nationNo));
	        
	        return new ImageIcon(im);
		}
		
		public static Color getColor1(int no) {
			return ColorPicker.getInstance().forceCustomColor1(no);
		}
		
		public static Color getColor2(int no) {
			return ColorPicker.getInstance().forceCustomColor2(no);
		}
		
		private static BufferedImage changeColor(BufferedImage im, Color toRemove, Color toReplace) {
	        int w = im.getWidth();
	        int h = im.getHeight();
	        int rgbRemove = toRemove.getRGB();
	        int rgbReplace = toReplace.getRGB();
	        // Copy pixels a scan line at a time
	        int buf[] = new int[w];
	        for (int y = 0; y < h; y++) {
	            im.getRGB(0, y, w, 1, buf, 0, w);
	            for (int x = 0; x < w; x++) {
	                if (buf[x] == rgbRemove) {
	                    buf[x] = rgbReplace;
	                }
	            }
	            im.setRGB(0, y, w, 1, buf, 0, w);
	        }
	        return im;
		}
		
		public static BufferedImage toBufferedImage(Image img)
		{
		    if (img instanceof BufferedImage)
		    {
		        return (BufferedImage) img;
		    }

		    // Create a buffered image with transparency
		    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		    // Draw the image on to the buffered image
		    Graphics2D bGr = bimage.createGraphics();
		    bGr.drawImage(img, 0, 0, null);
		    bGr.dispose();

		    // Return the buffered image
		    return bimage;
		}
	}
}
