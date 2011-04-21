package org.joverseer.ui.combatCalculator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.popup.JidePopup;

public class CombatForm extends AbstractForm {

	public static String FORM_ID = "combatForm";
	CombatArmyTableModel side1TableModel;
	CombatArmyTableModel side2TableModel;
	JTable side1Table;
	JTable side2Table;
	PopCenterTableModel popCenterTableModel;
	JTextField rounds;

	public CombatForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	protected JComponent createFormControl() {
		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
		TableLayoutBuilder tlb = new TableLayoutBuilder();

		GraphicUtils.registerIntegerPropertyConverters(this, "hexNo");
		TableLayoutBuilder lb = new TableLayoutBuilder();
		lb.cell(new JLabel("Description :"), "colspec=left:80px");
		lb.gapCol();
		lb.cell(sbf.createBoundTextField("description").getControl(), "colspec=left:120px");
		lb.gapCol();
		lb.gapCol();

		lb.relatedGapRow();

		lb.cell(new JLabel("Hex :"), "colspec=left:80px");
		lb.gapCol();
		lb.cell(sbf.createBoundTextField("hexNo").getControl(), "colspec=left:120px");
		lb.gapCol();

		JButton updateButton = new JButton("Refresh");
		updateButton.setPreferredSize(new Dimension(70, 20));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					commit();
					Combat c = (Combat) getFormObject();
					int hexNo = c.getHexNo();
					Hex h = GameHolder.instance().getGame().getMetadata().getHex(hexNo);
					HexInfo hi = (HexInfo) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hexNo);
					if (h != null && h.getTerrain() != null) {
						ValueModel vm = getFormModel().getValueModel("terrain");
						vm.setValue(h.getTerrain());
					}
					if (hi != null && hi.getClimate() != null) {
						ValueModel vm = getFormModel().getValueModel("climate");
						vm.setValue(hi.getClimate());
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
		lb.cell(updateButton, "colspec=left:80px");
		lb.relatedGapRow();

		lb.cell(new JLabel("Terrain :"), "colspec=left:80px");
		lb.gapCol();
		JComboBox cb = (JComboBox) sbf.createBoundComboBox("terrain", new ListListModel(Arrays.asList(HexTerrainEnum.landValues())), "renderString").getControl();
		cb.setPreferredSize(new Dimension(100, 20));
		lb.cell(cb, "colspec=left:120px");
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();

		lb.cell(new JLabel("Climate :"), "colspec=left:80px");
		lb.gapCol();
		cb = (JComboBox) sbf.createBoundComboBox("climate", new ListListModel(Arrays.asList(ClimateEnum.values()))).getControl();
		cb.setPreferredSize(new Dimension(100, 20));
		lb.cell(cb, "colspec=left:120px");
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();

		lb.cell(new JLabel("Attack PC :"), "colspec=left:80px");
		lb.gapCol();
		JCheckBox chkb = (JCheckBox) sbf.createBoundCheckBox("attackPopCenter").getControl();
		chkb.setPreferredSize(new Dimension(100, 20));
		lb.cell(chkb, "colspec=left:120px");
		chkb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();

		tlb.cell(lb.getPanel(), "colspan=2");
		tlb.relatedGapRow();

		MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");
		side1TableModel = new CombatArmyTableModel(this, messageSource);
		side1Table = TableUtils.createStandardSortableTable(side1TableModel);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(side1Table, side1TableModel.getColumnWidths());
		side1Table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == 3) {
					int idx = side1Table.rowAtPoint(e.getPoint());
					side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					showContextMenu(0, e);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = side1Table.rowAtPoint(e.getPoint());
					side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditSelectedArmyCommand(0).doExecuteCommand();
				}
			};
		});
		side1Table.setDropTarget(new DropTarget(side1Table, new AddArmyDropTargetAdapter(0)));

		JScrollPane scp = new JScrollPane(side1Table);
		scp.setPreferredSize(new Dimension(560, 130));
		scp.setDropTarget(new DropTarget(scp, new AddArmyDropTargetAdapter(0)));
		tlb.cell(scp);

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		Icon ico;
		JButton btn;
		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image"));
		btn = new JButton(ico);
		btn.setToolTipText("Edit Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("add.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Add New Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AddArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("remove.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Remove Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new RemoveSelectedArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("switch.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Switch Sides (Sends Army to other side)");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwitchSideCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("relations.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Change Relations for Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyRelationsCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();
		lb.cell(new JLabel(" "));
		lb.relatedGapRow();

		lb.cell(new JLabel(" "));
		lb.relatedGapRow();
		lb.row();

		tlb.gapCol();
		tlb.cell(lb.getPanel(), "colspec=left:60px");

		tlb.relatedGapRow();

		side2TableModel = new CombatArmyTableModel(this, messageSource);
		side2Table = TableUtils.createStandardSortableTable(side2TableModel);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(side2Table, side2TableModel.getColumnWidths());
		side2Table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == 3) {
					int idx = side2Table.rowAtPoint(e.getPoint());
					side2Table.getSelectionModel().setSelectionInterval(idx, idx);
					showContextMenu(1, e);
				}
			};

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = side2Table.rowAtPoint(e.getPoint());
					side2Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditSelectedArmyCommand(1).doExecuteCommand();
				}
			};
		});
		side2Table.setDropTarget(new DropTarget(side2Table, new AddArmyDropTargetAdapter(1)));

		scp = new JScrollPane(side2Table);
		scp.setPreferredSize(new Dimension(560, 130));
		scp.setDropTarget(new DropTarget(scp, new AddArmyDropTargetAdapter(1)));
		tlb.cell(scp);

		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image"));
		btn = new JButton(ico);
		btn.setToolTipText("Edit Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("add.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Add New Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AddArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("remove.icon"));
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new RemoveSelectedArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("switch.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Switch Sides (Sends Army to other side)");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwitchSideCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("relations.icon"));
		btn = new JButton(ico);
		btn.setToolTipText("Change Relations for Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyRelationsCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();
		lb.cell(new JLabel(" "));
		lb.relatedGapRow();

		lb.cell(new JLabel(" "));
		lb.relatedGapRow();

		lb.row();

		tlb.gapCol();
		tlb.cell(lb.getPanel());
		tlb.gapCol();

		tlb.relatedGapRow();

		popCenterTableModel = new PopCenterTableModel(this, messageSource);
		JTable pcTable = TableUtils.createStandardSortableTable(popCenterTableModel);
		pcTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = side1Table.rowAtPoint(e.getPoint());
					side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditPopCenterCommand().doExecuteCommand();
				}
			};
		});
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(pcTable, popCenterTableModel.getColumnWidths());
		scp = new JScrollPane(pcTable);
		scp.setPreferredSize(new Dimension(560, 48));

		scp.setDropTarget(new DropTarget(scp, new SetPopCenterDropTargetAdapter()));
		scp.setDropTarget(new DropTarget(pcTable, new SetPopCenterDropTargetAdapter()));

		tlb.cell(scp);

		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image"));
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditPopCenterCommand().doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("remove.icon"));
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new RemovePopCenterCommand().doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px");
		lb.gapCol();
		lb.relatedGapRow();

		tlb.cell(lb.getPanel());
		tlb.relatedGapRow();

		lb = new TableLayoutBuilder();
		lb.cell(new JLabel("Rounds :"), "colspec=left:80px");
		lb.gapCol();
		rounds = (JTextField) sbf.createBoundTextField("rounds").getControl();

		rounds.setEditable(false);
		lb.cell(rounds, "colspec=left:60px");
		lb.gapCol();

		tlb.cell(lb.getPanel());
		tlb.gapCol();
		tlb.relatedGapRow();

		tlb.cell(new JLabel(" "));
		tlb.gapCol();
		tlb.relatedGapRow();

		return tlb.getPanel();

	}

	private void removeItemSelectionFromTable(JTable table, int idx) {
		int newSelIdx = idx + 1;
		if (newSelIdx >= table.getRowCount()) {
			newSelIdx = idx - 1;
		}
		table.getSelectionModel().setSelectionInterval(newSelIdx, newSelIdx);
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		refreshArmies();
	}

	protected void runCombat() {
		Combat c = (Combat) getFormObject();
		for (CombatArmy ca : c.getSide1()) {
			if (ca != null)
				ca.setLosses(0);
		}
		for (CombatArmy ca : c.getSide2()) {
			if (ca != null)
				ca.setLosses(0);
		}
		c.runArmyBattle();
		int round = 0;
		if (side1TableModel.getRowCount() > 0 && side2TableModel.getRowCount() > 0) {
			round = 1;
		}
		if (c.getAttackPopCenter() && popCenterTableModel.getRows().size() > 0) {
			c.runPcBattle(0, round);
		}
		for (int i = 0; i < side1TableModel.getRowCount(); i++) {
			for (int j = 0; j < side1TableModel.getColumnCount(); j++) {
				side1TableModel.fireTableCellUpdated(i, j);
			}
		}
		for (int i = 0; i < side2TableModel.getRowCount(); i++) {
			for (int j = 0; j < side2TableModel.getColumnCount(); j++) {
				side2TableModel.fireTableCellUpdated(i, j);
			}
		}
		popCenterTableModel.fireTableDataChanged();
		rounds.setText(String.valueOf(c.getRounds()));
	}

	protected void refreshArmies() {
		Combat c = (Combat) getFormObject();
		ArrayList<Object> sa = new ArrayList<Object>();
		for (CombatArmy ca : c.getSide1()) {
			if (ca != null)
				sa.add(ca);
		}
		int selidx = side1Table.getSelectedRow();
		side1TableModel.setRows(sa);
		if (selidx > -1) {
			while (selidx >= sa.size()) {
				selidx--;
			}
			side1Table.getSelectionModel().setSelectionInterval(selidx, selidx);
		}

		sa = new ArrayList<Object>();
		for (CombatArmy ca : c.getSide2()) {
			if (ca != null)
				sa.add(ca);
		}
		side2TableModel.setRows(sa);

		sa = new ArrayList<Object>();
		if (c.getSide2Pc() != null) {
			sa.add(c.getSide2Pc());

		}
		popCenterTableModel.setRows(sa);

		runCombat();

		side1TableModel.fireTableDataChanged();
		side2TableModel.fireTableDataChanged();
		popCenterTableModel.fireTableDataChanged();

	}

	class SwitchSideCommand extends ActionCommand {

		int side;

		public SwitchSideCommand(int side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			Combat combat = (Combat) getFormObject();
			if (side == 0) {
				int idx1 = side1Table.getSelectedRow();
				if (idx1 < 0)
					return;
				int idx = ((SortableTableModel) side1Table.getModel()).convertSortedIndexToDataIndex(idx1);
				CombatArmy ca = (CombatArmy) side1TableModel.getRow(idx);
				if (combat.addToSide(1, ca)) {
					combat.removeFromSide(0, ca);
					removeItemSelectionFromTable(side1Table, idx1);
					side1TableModel.remove(idx);
					side2TableModel.addRow(ca);
					runCombat();
				}
			} else {
				int idx1 = side2Table.getSelectedRow();
				if (idx1 < 0)
					return;
				int idx = ((SortableTableModel) side2Table.getModel()).convertSortedIndexToDataIndex(idx1);
				CombatArmy ca = (CombatArmy) side2TableModel.getRow(idx);
				if (combat.addToSide(0, ca)) {
					combat.removeFromSide(1, ca);
					removeItemSelectionFromTable(side2Table, idx1);
					side2TableModel.remove(idx);
					side1TableModel.addRow(ca);
					runCombat();
				}
			}
		}
	}

	class AddArmyCommand extends ActionCommand {

		int side;

		public AddArmyCommand(int side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			CombatArmy ca = new CombatArmy();
			Combat combat = (Combat) getFormObject();
			if (combat.addToSide(side, ca)) {
				if (side == 0) {
					side1TableModel.addRow(ca);
				} else {
					side2TableModel.addRow(ca);
				}
				runCombat();
			}
		}
	}

	class RemoveSelectedArmyCommand extends ActionCommand {

		int side;

		public RemoveSelectedArmyCommand(int side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			final Combat combat = (Combat) getFormObject();
			int idx = -1;
			if (side == 0) {
				idx = side1Table.getSelectedRow();
			} else {
				idx = side2Table.getSelectedRow();
			}
			if (idx < 0)
				return;
			if (side == 0) {
				final int idx1 = idx;
				ConfirmationDialog md = new ConfirmationDialog("Remove army?", "Remove selected army from side 1?") {

					@Override
					protected void onConfirm() {
						int idx = ((SortableTableModel) side1Table.getModel()).convertSortedIndexToDataIndex(idx1);
						CombatArmy ca = (CombatArmy) side1TableModel.getRow(idx);
						if (combat.removeFromSide(0, ca)) {
							removeItemSelectionFromTable(side1Table, idx1);
							side1TableModel.remove(idx);
							runCombat();
						}
					}
				};
				md.setPreferredSize(new Dimension(300, 50));
				md.showDialog();
			} else {
				final int idx1 = idx;
				ConfirmationDialog md = new ConfirmationDialog("Remove army?", "Remove selected army from side 2?") {

					@Override
					protected void onConfirm() {
						int idx = ((SortableTableModel) side2Table.getModel()).convertSortedIndexToDataIndex(idx1);
						CombatArmy ca = (CombatArmy) side2TableModel.getRow(idx);
						if (combat.removeFromSide(1, ca)) {
							removeItemSelectionFromTable(side2Table, idx1);
							side2TableModel.remove(idx);
							runCombat();
						}
					}
				};
				md.setPreferredSize(new Dimension(300, 50));
				md.showDialog();
			}
		}
	}

	class EditSelectedArmyRelationsCommand extends ActionCommand {

		int side;

		public EditSelectedArmyRelationsCommand(int side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			int idx = -1;
			if (side == 0) {
				idx = side1Table.getSelectedRow();
			} else {
				idx = side2Table.getSelectedRow();
			}
			if (idx < 0)
				return;
			CombatArmy ca = null;
			if (side == 0) {
				ca = (CombatArmy) side1TableModel.getRow(((SortableTableModel) side1Table.getModel()).convertSortedIndexToDataIndex(idx));
			} else {
				ca = (CombatArmy) side2TableModel.getRow(((SortableTableModel) side2Table.getModel()).convertSortedIndexToDataIndex(idx));
			}
			if (ca == null)
				return;
			final Combat c = (Combat) getFormObject();
			final int armyIdx = c.getArmyIndex(side, ca);
			final JidePopup popup = new JidePopup();
			popup.getContentPane().setLayout(new BorderLayout());
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			final ArrayList<JComboBox> relations = new ArrayList<JComboBox>();
			CombatArmy[] enemySide;
			if (side == 0) {
				enemySide = c.getSide2();
			} else {
				enemySide = c.getSide1();
			}
			for (int i = 0; i < enemySide.length; i++) {
				String label = "Army " + (i + 1) + ": ";
				if (i == enemySide.length - 1)
					label = "Pop Center: ";
				tlb.cell(new JLabel(label));
				tlb.gapCol();
				final JComboBox rel = new JComboBox(NationRelationsEnum.values());
				if (side == 0) {
					rel.setSelectedItem(c.getSide1Relations()[armyIdx][i]);
				} else {
					rel.setSelectedItem(c.getSide2Relations()[armyIdx][i]);
				}
				tlb.cell(new JLabel("relations : "));
				tlb.gapCol();
				tlb.cell(rel);
				tlb.relatedGapRow();

				relations.add(rel);
				rel.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						int i = relations.indexOf(rel);
						if (side == 0) {
							c.getSide1Relations()[armyIdx][i] = (NationRelationsEnum) rel.getSelectedItem();
						} else {
							c.getSide2Relations()[armyIdx][i] = (NationRelationsEnum) rel.getSelectedItem();
						}
					}
				});
			}
			JButton closePopup = new JButton("Close");
			closePopup.setPreferredSize(new Dimension(70, 20));
			closePopup.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					popup.hidePopup();
					runCombat();
				}
			});
			tlb.cell(closePopup, "align=left");
			tlb.relatedGapRow();

			JScrollPane scp = new JScrollPane(tlb.getPanel());
			scp.setPreferredSize(new Dimension(200, 350));
			scp.getVerticalScrollBar().setUnitIncrement(16);
			popup.getContentPane().add(scp);
			popup.updateUI();
			popup.setOwner((side == 0 ? side1Table : side2Table));
			popup.setResizable(true);
			popup.setMovable(true);
			if (popup.isPopupVisible()) {
				popup.hidePopup();
			} else {
				popup.showPopup();
			}
		}
	}

	class EditSelectedArmyCommand extends ActionCommand {

		int side;

		public EditSelectedArmyCommand(int side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			int idx = -1;
			if (side == 0) {
				idx = side1Table.getSelectedRow();
			} else {
				idx = side2Table.getSelectedRow();
			}
			if (idx < 0)
				return;
			CombatArmy ca = null;
			if (side == 0) {
				ca = (CombatArmy) side1TableModel.getRow(((SortableTableModel) side1Table.getModel()).convertSortedIndexToDataIndex(idx));
			} else {
				ca = (CombatArmy) side2TableModel.getRow(((SortableTableModel) side2Table.getModel()).convertSortedIndexToDataIndex(idx));
			}
			if (ca == null)
				return;
			FormModel formModel = FormModelHelper.createFormModel(ca);
			final CombatArmyForm form = new CombatArmyForm(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {

				@Override
				protected void onAboutToShow() {
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					runCombat();
					return true;
				}
			};
			MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
			dialog.setTitle(ms.getMessage("editCombatArmy.title", new Object[] {}, Locale.getDefault()));
			dialog.showDialog();

		}

	}

	protected void showContextMenu(int side, MouseEvent e) {
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("contextMenu", new Object[] { new AddArmyCommand(side), new EditSelectedArmyCommand(side), new EditSelectedArmyRelationsCommand(side), new RemoveSelectedArmyCommand(side), new SwitchSideCommand(side) });
		JPopupMenu pm = group.createPopupMenu();
		pm.show((side == 0 ? side1Table : side2Table), e.getX(), e.getY());
	}

	public void addArmy(Army a) {
		Combat c = (Combat) getFormObject();
		int side = 0;
		NationAllegianceEnum ret = c.estimateAllegianceForSide(0);
		if (ret == null || ret == a.getNationAllegiance()) {
			side = 0;
		} else {
			side = 1;
		}
		CombatArmy ca = new CombatArmy(a);
		if (c.addToSide(side, ca)) {
			if (side == 0) {
				side1TableModel.addRow(ca);
			} else {
				side2TableModel.addRow(ca);
			}
			runCombat();
		}
	}

	public void addArmyEstimate(ArmyEstimate ae) {
	}

	class AddArmyDropTargetAdapter extends DropTargetAdapter {

		int side;

		public AddArmyDropTargetAdapter(int side) {
			super();
			this.side = side;
		}

		public void drop(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			try {
				DataFlavor armyArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Army[].class.getName() + "\"");
				DataFlavor armyFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName());
				DataFlavor armyEstimateArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ArmyEstimate[].class.getName() + "\"");
				DataFlavor armyEstimateFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ArmyEstimate.class.getName());

				Combat c = (Combat) getFormObject();
				if (t.isDataFlavorSupported(armyArrayFlavor)) {
					Army[] armies = (Army[]) t.getTransferData(armyArrayFlavor);
					for (Army a : armies) {
						CombatArmy ca = new CombatArmy(a);
						c.addToSide(side, ca);
						if (side == 0) {
							side1TableModel.addRow(ca);
						} else {
							side2TableModel.addRow(ca);
						}
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyFlavor)) {
					CombatArmy ca = new CombatArmy((Army) t.getTransferData(armyFlavor));
					c.addToSide(side, ca);
					if (side == 0) {
						side1TableModel.addRow(ca);
					} else {
						side2TableModel.addRow(ca);
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyEstimateArrayFlavor)) {
					ArmyEstimate[] armies = (ArmyEstimate[]) t.getTransferData(armyEstimateArrayFlavor);
					for (ArmyEstimate a : armies) {
						CombatArmy ca = new CombatArmy(a);
						c.addToSide(side, ca);
						if (side == 0) {
							side1TableModel.addRow(ca);
						} else {
							side2TableModel.addRow(ca);
						}
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyEstimateFlavor)) {
					CombatArmy ca = new CombatArmy((ArmyEstimate) t.getTransferData(armyEstimateFlavor));
					c.addToSide(side, ca);
					if (side == 0) {
						side1TableModel.addRow(ca);
					} else {
						side2TableModel.addRow(ca);
					}
					runCombat();
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	};

	class SetPopCenterDropTargetAdapter extends DropTargetAdapter {

		public SetPopCenterDropTargetAdapter() {
			super();
		}

		public void drop(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			try {
				DataFlavor popCenterFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + PopulationCenter.class.getName());

				if (t.isDataFlavorSupported(popCenterFlavor)) {
					PopulationCenter pc = (PopulationCenter) t.getTransferData(popCenterFlavor);
					CombatPopCenter cpc = new CombatPopCenter();
					cpc.setName(pc.getName());
					cpc.setNationNo(pc.getNationNo());
					cpc.setLoyalty(pc.getLoyalty());
					cpc.setFort(pc.getFortification());
					cpc.setSize(pc.getSize());
					popCenterTableModel.getRows().clear();
					popCenterTableModel.addRow(cpc);
					Combat c = (Combat) getFormObject();
					c.setSide2Pc(cpc);
					runCombat();
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	};

	class RemovePopCenterCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			final Combat c = (Combat) getFormObject();
			ConfirmationDialog md = new ConfirmationDialog("Remove pop center?", "Remove pop center?") {
				@Override
				protected void onConfirm() {
					c.setSide2Pc(null);
					popCenterTableModel.getRows().clear();
					popCenterTableModel.fireTableDataChanged();
					runCombat();
				}
			};
			md.showDialog();
		}

	}

	class EditPopCenterCommand extends ActionCommand {

		public EditPopCenterCommand() {
			super();
		}

		@Override
		protected void doExecuteCommand() {
			CombatPopCenter pc = null;
			if (popCenterTableModel.getRowCount() == 0) {
				pc = new CombatPopCenter();
			} else {
				pc = (CombatPopCenter) popCenterTableModel.getRow(0);
			}
			FormModel formModel = FormModelHelper.createFormModel(pc);
			final CombatPopCenterForm form = new CombatPopCenterForm(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {

				@Override
				protected void onAboutToShow() {
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					Combat c = (Combat) getFormObject();
					CombatPopCenter pc = (CombatPopCenter) form.getFormObject();
					if (popCenterTableModel.getRowCount() == 0) {
						c.setSide2Pc(pc);
						popCenterTableModel.addRow(pc);
						popCenterTableModel.fireTableDataChanged();
					}

					runCombat();
					return true;
				}
			};
			dialog.setTitle("Edit population center");
			dialog.showDialog();

		}

	}

}
