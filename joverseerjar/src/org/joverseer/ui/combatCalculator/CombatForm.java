package org.joverseer.ui.combatCalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.JOApplication;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.popup.JidePopup;

public class CombatForm extends AbstractForm {

	public static String FORM_ID = "combatForm"; //$NON-NLS-1$
	CombatArmyReducedTableModel otherTableModel;
	CombatArmyTableModel side1TableModel;
	CombatArmyTableModel side2TableModel;
	JTable otherTable;
	JTable side1Table;
	JTable side2Table;
	JTable pcTable;
	PopCenterTableModel popCenterTableModel;
	JTextField rounds;
	
	boolean before = false;
	int draggedFrom;
	protected int xDiff;
	protected int yDiff;

	//dependencies
	GameHolder gameHolder;

	public CombatForm(FormModel arg0,GameHolder gameHolder) {
		super(arg0, FORM_ID);
		this.gameHolder = gameHolder;
	}

	@Override
	protected JComponent createFormControl() {
		SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
		TableLayoutBuilder tlb = new TableLayoutBuilder();

		GraphicUtils.registerIntegerPropertyConverters(this, "hexNo"); //$NON-NLS-1$
		TableLayoutBuilder lb = new TableLayoutBuilder();
		lb.cell(new JLabel(Messages.getString("CombatForm.Description")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		lb.cell(sbf.createBoundTextField("description").getControl(), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		lb.gapCol();

		lb.relatedGapRow();

		lb.cell(new JLabel(Messages.getString("CombatForm.Hex")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		lb.cell(sbf.createBoundTextField("hexNo").getControl(), "colspec=left:120px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.relatedGapRow();

		lb.cell(new JLabel(Messages.getString("CombatForm.Terrain")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		JComboBox cb = (JComboBox) sbf.createBoundComboBox("terrain", new ListListModel(Arrays.asList(HexTerrainEnum.landValues())), "renderString").getControl(); //$NON-NLS-1$ //$NON-NLS-2$
		cb.setPreferredSize(new Dimension(100, 20));
		lb.cell(cb, "colspec=left:120px"); //$NON-NLS-1$
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();

		lb.cell(new JLabel(Messages.getString("CombatForm.Climate")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		cb = (JComboBox) sbf.createBoundComboBox("climate", new ListListModel(Arrays.asList(ClimateEnum.values()))).getControl(); //$NON-NLS-1$
		cb.setPreferredSize(new Dimension(100, 20));
		lb.cell(cb, "colspec=left:120px"); //$NON-NLS-1$
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();
		
		JPanel paRadioBut = new JPanel();
		paRadioBut.setLayout(new FlowLayout());
        // Create radio buttons
        JRadioButton beforeButton = new JRadioButton("Before");
        JRadioButton afterButton = new JRadioButton("After");

        // Group the radio buttons so only one can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(beforeButton);
        group.add(afterButton);

        // Add ActionListener to both radio buttons
        ActionListener radioButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (beforeButton.isSelected()) CombatForm.this.before = true;
                else if (afterButton.isSelected()) CombatForm.this.before = false;
                commit();
                runCombat();
            }
        };

        beforeButton.addActionListener(radioButtonListener);
        afterButton.addActionListener(radioButtonListener);

        paRadioBut.add(beforeButton);
        paRadioBut.add(afterButton);

        afterButton.setSelected(true);
		
		lb.cell(new JLabel("Troops: "), "colspec=left:80px");
		lb.cell(paRadioBut, "colspec=left:130px");
		
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();

		lb.cell(new JLabel(Messages.getString("CombatForm.AttackPC")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		JCheckBox chkb = (JCheckBox) sbf.createBoundCheckBox("attackPopCenter").getControl(); //$NON-NLS-1$
		chkb.setPreferredSize(new Dimension(100, 20));
		lb.cell(chkb, "colspec=left:120px"); //$NON-NLS-1$
		chkb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commit();
				runCombat();
			}
		});
		lb.gapCol();
		lb.gapCol();
		lb.relatedGapRow();
		
		tlb.cell(lb.getPanel()); //$NON-NLS-1$
		
		tlb.gapCol();
		JPanel tp = new JPanel();
		tp.setBorder(BorderFactory.createTitledBorder(
			      BorderFactory.createEtchedBorder(), "Other armies in hex", TitledBorder.LEFT,
			      TitledBorder.TOP));
		MessageSource messageSource = Messages.getMessageSource();
		this.otherTableModel = new CombatArmyReducedTableModel(this, messageSource);
		this.otherTable = TableUtils.createStandardSortableTable(this.otherTableModel);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.otherTable, this.otherTableModel.getColumnWidths());

		this.otherTable.setDragEnabled(true);
		this.otherTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.otherTable.setDropMode(DropMode.INSERT_ROWS);
		this.otherTable.setDropTarget(new DropTarget(this.otherTable, new AddArmyDropTargetAdapter(2)));
		this.otherTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					CombatForm.this.xDiff = e.getX();
					CombatForm.this.yDiff = e.getY();
				}
			};
		});
		this.otherTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int dx = Math.abs(e.getX() - CombatForm.this.xDiff);
				int dy = Math.abs(e.getY() - CombatForm.this.yDiff);
				if (dx > 5 || dy > 5) {
					CombatForm.this.draggedFrom = 2;
					CombatForm.this.otherTableModel.startDragAndDropAction(e, CombatForm.this.otherTable, 2);

				}
			}
		});
		
		JScrollPane scp = new JScrollPane(this.otherTable);
		scp.setPreferredSize(new Dimension(350, 130));
		scp.setDropTarget(new DropTarget(scp, new AddArmyDropTargetAdapter(2)));
		tp.add(scp);
		tlb.cell(tp, "colspan=1");
	
		tlb.relatedGapRow();

		this.side1TableModel = new CombatArmyTableModel(this, messageSource);
		this.side1Table = TableUtils.createStandardSortableTable(this.side1TableModel);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.side1Table, this.side1TableModel.getColumnWidths());
		this.side1Table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					CombatForm.this.xDiff = e.getX();
					CombatForm.this.yDiff = e.getY();
				}
				if (e.getClickCount() == 1 && e.getButton() == 3) {
					int idx = CombatForm.this.side1Table.rowAtPoint(e.getPoint());
					CombatForm.this.side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					showContextMenu(0, e);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = CombatForm.this.side1Table.rowAtPoint(e.getPoint());
					CombatForm.this.side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditSelectedArmyCommand(0).doExecuteCommand();
				}
			};
		});
		this.side1Table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int dx = Math.abs(e.getX() - CombatForm.this.xDiff);
				int dy = Math.abs(e.getY() - CombatForm.this.yDiff);
				if (dx > 5 || dy > 5) {
					CombatForm.this.draggedFrom = 0;
					CombatForm.this.side1TableModel.startDragAndDropAction(e, CombatForm.this.side1Table, 0);
				}
			}
		});
		this.side1Table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.side1Table.setDropTarget(new DropTarget(this.side1Table, new AddArmyDropTargetAdapter(0)));
		this.side1Table.setDragEnabled(true);
		this.side1Table.setDropMode(DropMode.INSERT_ROWS);
		
		this.side1Table.setDefaultRenderer(String.class, new IncompleteArmyRenderer(this.side1TableModel));
		
		scp = new JScrollPane(this.side1Table);
		scp.setPreferredSize(new Dimension(700, 130));
		scp.setDropTarget(new DropTarget(scp, new AddArmyDropTargetAdapter(0)));
		tlb.cell(scp, "colspan=2");
		
		ImageSource imgSource = JOApplication.getImageSource();
		Icon ico;
		JButton btn;
		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText(Messages.getString("CombatForm.EditArmyToolTip")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("add.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText(Messages.getString("CombatForm.AddNewArmyToolTip")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("remove.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText(Messages.getString("CombatForm.RemoveArmyToolTip")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new RemoveSelectedArmyCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("switch.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText(Messages.getString("CombatForm.SwitchSidesToolTip")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwitchSideCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("relations.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText(Messages.getString("CombatForm.ChangeRelationsToolTip")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyRelationsCommand(0).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();

		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();
		lb.row();

		tlb.gapCol();
		tlb.cell(lb.getPanel(), "colspec=left:60px"); //$NON-NLS-1$

		tlb.relatedGapRow();

		this.side2TableModel = new CombatArmyTableModel(this, messageSource);
		this.side2Table = TableUtils.createStandardSortableTable(this.side2TableModel);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.side2Table, this.side2TableModel.getColumnWidths());
		this.side2Table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					CombatForm.this.xDiff = e.getX();
					CombatForm.this.yDiff = e.getY();
				}
				if (e.getClickCount() == 1 && e.getButton() == 3) {
					int idx = CombatForm.this.side2Table.rowAtPoint(e.getPoint());
					CombatForm.this.side2Table.getSelectionModel().setSelectionInterval(idx, idx);
					showContextMenu(1, e);
				}
			};

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = CombatForm.this.side2Table.rowAtPoint(e.getPoint());
					CombatForm.this.side2Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditSelectedArmyCommand(1).doExecuteCommand();
				}
			};
		});
		this.side2Table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int dx = Math.abs(e.getX() - CombatForm.this.xDiff);
				int dy = Math.abs(e.getY() - CombatForm.this.yDiff);
				if (dx > 5 || dy > 5) {
					CombatForm.this.draggedFrom = 1;
					CombatForm.this.side2TableModel.startDragAndDropAction(e, CombatForm.this.side2Table, 1);
				}
			}
		});
		this.side2Table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.side2Table.setDropTarget(new DropTarget(this.side2Table, new AddArmyDropTargetAdapter(1)));
		this.side2Table.setDragEnabled(true);
		this.side2Table.setDropMode(DropMode.INSERT_ROWS);
		
		this.side2Table.setDefaultRenderer(String.class, new IncompleteArmyRenderer(this.side2TableModel));
		
		scp = new JScrollPane(this.side2Table);
		scp.setPreferredSize(new Dimension(700, 130));
		scp.setDropTarget(new DropTarget(scp, new AddArmyDropTargetAdapter(1)));
		tlb.cell(scp, "colspan=2");

		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText("Edit Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("add.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText("Add New Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("remove.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new RemoveSelectedArmyCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("switch.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText("Switch Sides (Sends Army to other side)");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwitchSideCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("relations.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText("Change Relations for Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditSelectedArmyRelationsCommand(1).doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();

		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();

		lb.row();

		tlb.gapCol();
		tlb.cell(lb.getPanel());
		tlb.gapCol();

		tlb.relatedGapRow();

		this.popCenterTableModel = new PopCenterTableModel(this, messageSource);
		this.pcTable = TableUtils.createStandardSortableTable(this.popCenterTableModel);
		this.pcTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					int idx = CombatForm.this.side1Table.rowAtPoint(e.getPoint());
					CombatForm.this.side1Table.getSelectionModel().setSelectionInterval(idx, idx);
					new EditPopCenterCommand().doExecuteCommand();
				}
			};
		});
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.pcTable, this.popCenterTableModel.getColumnWidths());
		scp = new JScrollPane(this.pcTable);
		scp.setPreferredSize(new Dimension(650, 48));

		scp.setDropTarget(new DropTarget(scp, new SetPopCenterDropTargetAdapter()));
		scp.setDropTarget(new DropTarget(this.pcTable, new SetPopCenterDropTargetAdapter()));

		tlb.cell(scp, "colspan=2");

		lb = new TableLayoutBuilder();
		ico = new ImageIcon(imgSource.getImage("edit.image")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditPopCenterCommand().doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();

		ico = new ImageIcon(imgSource.getImage("remove.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new RemovePopCenterCommand().doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();
		
		lb.relatedGapRow();

		ico = new ImageIcon(imgSource.getImage("relations.icon")); //$NON-NLS-1$
		btn = new JButton(ico);
		btn.setToolTipText("Change Relations for Army");
		btn.setPreferredSize(new Dimension(20, 20));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditPopCenterRelationsCommand().doExecuteCommand();
			}
		});
		lb.cell(btn, "colspec=left:30px"); //$NON-NLS-1$
		lb.gapCol();
		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();

		lb.cell(new JLabel(" ")); //$NON-NLS-1$
		lb.relatedGapRow();

		lb.row();
		
		lb.relatedGapRow();

		tlb.cell(lb.getPanel());
		tlb.relatedGapRow();

		lb = new TableLayoutBuilder();
		lb.cell(new JLabel(Messages.getString("CombatForm.Rounds")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		this.rounds = (JTextField) sbf.createBoundTextField("rounds").getControl(); //$NON-NLS-1$

		this.rounds.setEditable(false);
		lb.cell(this.rounds, "colspec=left:60px"); //$NON-NLS-1$
		lb.gapCol();

		tlb.cell(lb.getPanel());
		tlb.gapCol();
		tlb.relatedGapRow();

		tlb.cell(new JLabel(" ")); //$NON-NLS-1$
		tlb.gapCol();
		tlb.relatedGapRow();

		return tlb.getPanel();

	}
	
	public void refreshCombat() {
		try {
			commit();
			Combat c = (Combat) getFormObject();
			int hexNo = c.getHexNo();
			Hex h = CombatForm.this.gameHolder.getGame().getMetadata().getHex(hexNo);
			HexInfo hi = CombatForm.this.gameHolder.getGame().getTurn().getHexInfo(hexNo);
			if (h != null && h.getTerrain() != null) {
				ValueModel vm = getFormModel().getValueModel("terrain"); //$NON-NLS-1$
				vm.setValue(h.getTerrain());
			}
			if (hi != null && hi.getClimate() != null) {
				ValueModel vm = getFormModel().getValueModel("climate"); //$NON-NLS-1$
				vm.setValue(hi.getClimate());
			}
			
			c.setArmiesAndPCFromHex();
			refreshArmies();
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
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
		c.setPlayerLogUp();
		for (CombatArmy ca : c.getSide1()) {
			if (ca != null)
				ca.setLosses(0);
		}
		for (CombatArmy ca : c.getSide2()) {
			if (ca != null)
				ca.setLosses(0);
		}
		
		if (!this.before) {
		
			c.runArmyBattle();
			int round = 0;
			if (this.side1TableModel.getRowCount() > 0 && this.side2TableModel.getRowCount() > 0) {
				round = 1;
			}
			if (c.getAttackPopCenter() && this.popCenterTableModel.getRows().size() > 0) {
				c.runPcBattle(0, round);
			}
			
		}
		
		c.logger.addMode = false;
		
		for (int i = 0; i < this.side1TableModel.getRowCount(); i++) {
			for (int j = 0; j < this.side1TableModel.getColumnCount(); j++) {
				this.side1TableModel.fireTableCellUpdated(i, j);
			}
		}
		for (int i = 0; i < this.side2TableModel.getRowCount(); i++) {
			for (int j = 0; j < this.side2TableModel.getColumnCount(); j++) {
				this.side2TableModel.fireTableCellUpdated(i, j);
			}
		}
		this.popCenterTableModel.fireTableDataChanged();
		this.rounds.setText(String.valueOf(c.getRounds()));
	}

	protected void refreshArmies() {
		Combat c = (Combat) getFormObject();
		ArrayList<Object> sa = new ArrayList<Object>();
		for (CombatArmy ca : c.getSide1()) {
			if (ca != null)
				sa.add(ca);
		}
		int selidx = this.side1Table.getSelectedRow();
		this.side1TableModel.setRows(sa);
		if (selidx > -1) {
			while (selidx >= sa.size()) {
				selidx--;
			}
			this.side1Table.getSelectionModel().setSelectionInterval(selidx, selidx);
		}

		sa = new ArrayList<Object>();
		for (CombatArmy ca : c.getSide2()) {
			if (ca != null)
				sa.add(ca);
		}
		this.side2TableModel.setRows(sa);

		sa = new ArrayList<Object>();
		for (CombatArmy ca : c.getOtherSide()) {
			if (ca != null)
				sa.add(ca);
		}
		this.otherTableModel.setRows(sa);
		
		sa = new ArrayList<Object>();
		if (c.getSide2Pc() != null) {
			sa.add(c.getSide2Pc());

		}
		this.popCenterTableModel.setRows(sa);
		
		c.autoSetCombatRelations();

		runCombat();

		this.side1TableModel.fireTableDataChanged();
		this.side2TableModel.fireTableDataChanged();
		this.otherTableModel.fireTableDataChanged();
		this.popCenterTableModel.fireTableDataChanged();

	}
	
	public void openLog() {
		FormModel formModel = FormModelHelper.createFormModel((Combat)this.getFormObject());
		final CombatLog form = new CombatLog(formModel, ((Combat)this.getFormObject()).logger);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {

			@Override
			protected void onAboutToShow() {
			}

			@Override
			protected boolean onFinish() {
				form.commit();
				return true;
			}
		};
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		dialog.setTitle(ms.getMessage("CombatLog.title", new Object[] {}, Locale.getDefault())); //$NON-NLS-1$
		dialog.showDialog();
	}
	
	class IncompleteArmyRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 6703872492730589499L;
	    BeanTableModel tableModel;
	    
	    public IncompleteArmyRenderer(BeanTableModel tableModel) {
	        this.tableModel = tableModel;
	    }
	
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	        if (isSelected) {
	            return cellComponent;
	        }
	        
	        int objRow = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
	        Object obj = this.tableModel.getRow(objRow);
	        CombatArmy ca = (CombatArmy) obj;
	        
	        if(!ca.completeInfo()) {
	            Color bg = ColorPicker.getInstance().getColor("TurnReport.default");
	        	cellComponent.setBackground(bg);
	        	
	        	JLabel lb = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        	lb.setToolTipText("Not accurate strength, incomplete data, edit army.");
	        }
	        else {
	        	cellComponent.setBackground(UIManager.getColor("Table.background"));
	        	JLabel lb = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        	lb.setToolTipText(null);
	        	
	        }

	        return cellComponent;
		}
		
	    public BeanTableModel getTableModel() {
	        return this.tableModel;
	    }

	    
	    public void setTableModel(BeanTableModel tableModel) {
	        this.tableModel = tableModel;
	    }
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
			if (this.side == 0) {
				int idx1 = CombatForm.this.side1Table.getSelectedRow();
				if (idx1 < 0)
					return;
				int idx = ((SortableTableModel) CombatForm.this.side1Table.getModel()).convertSortedIndexToDataIndex(idx1);
				CombatArmy ca = (CombatArmy) CombatForm.this.side1TableModel.getRow(idx);
				if (combat.addToSide(1, ca)) {
					combat.removeFromSide(0, ca);
					removeItemSelectionFromTable(CombatForm.this.side1Table, idx1);
					CombatForm.this.side1TableModel.remove(idx);
					CombatForm.this.side2TableModel.addRow(ca);
					runCombat();
				}
			} else {
				int idx1 = CombatForm.this.side2Table.getSelectedRow();
				if (idx1 < 0)
					return;
				int idx = ((SortableTableModel) CombatForm.this.side2Table.getModel()).convertSortedIndexToDataIndex(idx1);
				CombatArmy ca = (CombatArmy) CombatForm.this.side2TableModel.getRow(idx);
				if (combat.addToSide(0, ca)) {
					combat.removeFromSide(1, ca);
					removeItemSelectionFromTable(CombatForm.this.side2Table, idx1);
					CombatForm.this.side2TableModel.remove(idx);
					CombatForm.this.side1TableModel.addRow(ca);
					runCombat();
				}
			}
		}
	}

	class AddArmyCommand extends ActionCommand {

		int side;
		EditSelectedArmyCommand editArmy;

		public AddArmyCommand(int side) {
			super();
			this.side = side;
			this.editArmy = new EditSelectedArmyCommand(side);
		}

		@Override
		protected void doExecuteCommand() {
			CombatArmy ca = new CombatArmy();
			Combat combat = (Combat) getFormObject();
			if (combat.addToSide(this.side, ca)) {
				if (this.side == 0) {
					CombatForm.this.side1TableModel.addRow(ca);
					CombatForm.this.side1Table.clearSelection();
					CombatForm.this.side1Table.setRowSelectionInterval(CombatForm.this.side1Table.getRowCount() - 1, CombatForm.this.side1Table.getRowCount() - 1);;
					this.editArmy.doExecuteCommand();
				} else {
					CombatForm.this.side2TableModel.addRow(ca);
					CombatForm.this.side2Table.clearSelection();
					CombatForm.this.side2Table.setRowSelectionInterval(CombatForm.this.side2Table.getRowCount() - 1, CombatForm.this.side2Table.getRowCount() - 1);;
					this.editArmy.doExecuteCommand();
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
			if (this.side == 0) {
				idx = CombatForm.this.side1Table.getSelectedRow();
			} else if (this.side == 1){
				idx = CombatForm.this.side2Table.getSelectedRow();
			} else {
				idx = CombatForm.this.otherTable.getSelectedRow();
			}
			if (idx < 0)
				return;
			if (this.side == 0) {
				final int idx1 = idx;
				ConfirmationDialog md = new ConfirmationDialog(Messages.getString("CombatForm.RemoveArmy.title"), Messages.getString("CombatForm.RemoveArmySide1")) { //$NON-NLS-1$

					@Override
					protected void onConfirm() {
						int idx2 = ((SortableTableModel) CombatForm.this.side1Table.getModel()).convertSortedIndexToDataIndex(idx1);
						CombatArmy ca = (CombatArmy) CombatForm.this.side1TableModel.getRow(idx2);
						if (combat.removeFromSide(0, ca)) {
							removeItemSelectionFromTable(CombatForm.this.side1Table, idx1);
							CombatForm.this.side1TableModel.remove(idx2);
							combat.addToSide(2, ca);
							CombatForm.this.refreshArmies();
							runCombat();
						}
					}
				};
				md.setPreferredSize(new Dimension(300, 50));
				md.showDialog();
			} else {
				final int idx1 = idx;
				ConfirmationDialog md = new ConfirmationDialog(Messages.getString("CombatForm.RemoveArmy.title"), Messages.getString("CombatForm.RemoveArmySide2")) { //$NON-NLS-2$

					@Override
					protected void onConfirm() {
						int idx2 = ((SortableTableModel) CombatForm.this.side2Table.getModel()).convertSortedIndexToDataIndex(idx1);
						CombatArmy ca = (CombatArmy) CombatForm.this.side2TableModel.getRow(idx2);
						if (combat.removeFromSide(1, ca)) {
							removeItemSelectionFromTable(CombatForm.this.side2Table, idx1);
							CombatForm.this.side2TableModel.remove(idx2);
							combat.addToSide(2, ca);
							CombatForm.this.refreshArmies();
							runCombat();
						}
					}
				};
				md.setPreferredSize(new Dimension(300, 50));
				md.showDialog();
			}
		}
	}
	
	class EditPopCenterRelationsCommand extends ActionCommand {
		public EditPopCenterRelationsCommand() {
			super();
		}
		
		@Override
		protected void doExecuteCommand() {
			CombatPopCenter cPC = null;
			int idx = 0;
			
			cPC = (CombatPopCenter) CombatForm.this.popCenterTableModel.getRow(idx);
			if(cPC == null) return;
			final Combat c = (Combat) getFormObject();
			
			final JidePopup popup = new JidePopup();
			popup.getContentPane().setLayout(new BorderLayout());
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			final ArrayList<JComboBox> relations = new ArrayList<JComboBox>();
			CombatArmy[] enemySide;

			enemySide = c.getSide1();
			for (int i = 0; i < enemySide.length-1; i++) {
				String label = Messages.getString("CombatForm.ArmySpace") + (i + 1) + ": "; //$NON-NLS-1$ //$NON-NLS-2$
//				if (i == enemySide.length - 1)
//					label = Messages.getString("CombatForm.PC"); //$NON-NLS-1$
				tlb.cell(new JLabel(label));
				tlb.gapCol();
				final JComboBox rel = new JComboBox(NationRelationsEnum.values());
				rel.setSelectedItem(c.getPCRelations()[i]);
				tlb.cell(new JLabel(Messages.getString("CombatForm.relations"))); //$NON-NLS-1$
				tlb.gapCol();
				tlb.cell(rel);
				tlb.relatedGapRow();

				relations.add(rel);
				rel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						int i1 = relations.indexOf(rel);
						c.getPCRelations()[i1] = (NationRelationsEnum) rel.getSelectedItem();
						runCombat();
					}
				});
			}
			JButton closePopup = new JButton(Messages.getString("CombatForm.Close")); //$NON-NLS-1$
			closePopup.setPreferredSize(new Dimension(70, 20));
			closePopup.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					popup.hidePopup();
					runCombat();
				}
			});
			tlb.cell(closePopup, "align=left"); //$NON-NLS-1$
			tlb.relatedGapRow();

			JScrollPane scp = new JScrollPane(tlb.getPanel());
			scp.setPreferredSize(new Dimension(200, 350));
			scp.getVerticalScrollBar().setUnitIncrement(16);
			popup.getContentPane().add(scp);
			popup.updateUI();
			popup.setOwner(CombatForm.this.pcTable);
			popup.setResizable(true);
			popup.setMovable(true);
			if (popup.isPopupVisible()) {
				popup.hidePopup();
			} else {
				popup.showPopup();
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
			if (this.side == 0) {
				idx = CombatForm.this.side1Table.getSelectedRow();
			} else {
				idx = CombatForm.this.side2Table.getSelectedRow();
			}
			if (idx < 0)
				return;
			CombatArmy ca = null;
			if (this.side == 0) {
				ca = (CombatArmy) CombatForm.this.side1TableModel.getRow(((SortableTableModel) CombatForm.this.side1Table.getModel()).convertSortedIndexToDataIndex(idx));
			} else {
				ca = (CombatArmy) CombatForm.this.side2TableModel.getRow(((SortableTableModel) CombatForm.this.side2Table.getModel()).convertSortedIndexToDataIndex(idx));
			}
			if (ca == null)
				return;
			final Combat c = (Combat) getFormObject();
			final int armyIdx = c.getArmyIndex(this.side, ca);
			final JidePopup popup = new JidePopup();
			popup.getContentPane().setLayout(new BorderLayout());
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			final ArrayList<JComboBox> relations = new ArrayList<JComboBox>();
			CombatArmy[] enemySide;
			if (this.side == 0) {
				enemySide = c.getSide2();
			} else {
				enemySide = c.getSide1();
			}
			for (int i = 0; i < enemySide.length; i++) {
				String label = Messages.getString("CombatForm.ArmySpace") + (i + 1) + ": "; //$NON-NLS-1$ //$NON-NLS-2$
				if (i == enemySide.length - 1)
					label = Messages.getString("CombatForm.PC"); //$NON-NLS-1$
				tlb.cell(new JLabel(label));
				tlb.gapCol();
				final JComboBox rel = new JComboBox(NationRelationsEnum.values());
				if (this.side == 0) {
					rel.setSelectedItem(c.getSide1Relations()[armyIdx][i]);
				} else {
					rel.setSelectedItem(c.getSide2Relations()[armyIdx][i]);
				}
				tlb.cell(new JLabel(Messages.getString("CombatForm.relations"))); //$NON-NLS-1$
				tlb.gapCol();
				tlb.cell(rel);
				tlb.relatedGapRow();

				relations.add(rel);
				rel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						int i1 = relations.indexOf(rel);
						if (EditSelectedArmyRelationsCommand.this.side == 0) {
							c.getSide1Relations()[armyIdx][i1] = (NationRelationsEnum) rel.getSelectedItem();
						} else {
							c.getSide2Relations()[armyIdx][i1] = (NationRelationsEnum) rel.getSelectedItem();
						}
						runCombat();
					}
				});
			}
			JButton closePopup = new JButton(Messages.getString("CombatForm.Close")); //$NON-NLS-1$
			closePopup.setPreferredSize(new Dimension(70, 20));
			closePopup.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					popup.hidePopup();
					runCombat();
				}
			});
			tlb.cell(closePopup, "align=left"); //$NON-NLS-1$
			tlb.relatedGapRow();

			JScrollPane scp = new JScrollPane(tlb.getPanel());
			scp.setPreferredSize(new Dimension(200, 350));
			scp.getVerticalScrollBar().setUnitIncrement(16);
			popup.getContentPane().add(scp);
			popup.updateUI();
			popup.setOwner((this.side == 0 ? CombatForm.this.side1Table : CombatForm.this.side2Table));
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
			if (this.side == 0) {
				idx = CombatForm.this.side1Table.getSelectedRow();
			} else if(this.side == 1){
				idx = CombatForm.this.side2Table.getSelectedRow();
			} else {
				idx = CombatForm.this.otherTable.getSelectedRow();
			}
			if (idx < 0)
				return;
			CombatArmy ca = null;
			if (this.side == 0) {
				ca = (CombatArmy) CombatForm.this.side1TableModel.getRow(((SortableTableModel) CombatForm.this.side1Table.getModel()).convertSortedIndexToDataIndex(idx));
			} else if (this.side == 1){
				ca = (CombatArmy) CombatForm.this.side2TableModel.getRow(((SortableTableModel) CombatForm.this.side2Table.getModel()).convertSortedIndexToDataIndex(idx));
			} else {
				ca = (CombatArmy) CombatForm.this.otherTableModel.getRow(((SortableTableModel) CombatForm.this.otherTable.getModel()).convertSortedIndexToDataIndex(idx));
			}
			if (ca == null)
				return;
			FormModel formModel = FormModelHelper.createFormModel(ca);
			final CombatArmyForm form = new CombatArmyForm(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {

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
			dialog.setTitle(ms.getMessage("editCombatArmy.title", new Object[] {}, Locale.getDefault())); //$NON-NLS-1$
			dialog.showDialog();

		}

	}

	protected void showContextMenu(int side, MouseEvent e) {
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("contextMenu", new Object[] { new AddArmyCommand(side), new EditSelectedArmyCommand(side), new EditSelectedArmyRelationsCommand(side), new RemoveSelectedArmyCommand(side), new SwitchSideCommand(side) }); //$NON-NLS-1$
		JPopupMenu pm = group.createPopupMenu();
		pm.show((side == 0 ? this.side1Table : this.side2Table), e.getX(), e.getY());
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
				this.side1TableModel.addRow(ca);
			} else {
				this.side2TableModel.addRow(ca);
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

		@Override
		public void drop(DropTargetDropEvent e) {
			System.out.println("Drop event");
			Transferable t = e.getTransferable();
			try {
				DataFlavor armyArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Army[].class.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				DataFlavor armyFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName()); //$NON-NLS-1$
				DataFlavor armyEstimateArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ArmyEstimate[].class.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				DataFlavor armyEstimateFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ArmyEstimate.class.getName()); //$NON-NLS-1$
				DataFlavor combatArmyArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + CombatArmy[].class.getName() + "\"");  //$NON-NLS-1$
				DataFlavor combatArmyFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + CombatArmy.class.getName());  //$NON-NLS-1$

				Combat c = (Combat) getFormObject();
				if (t.isDataFlavorSupported(armyArrayFlavor)) {
					Army[] armies = (Army[]) t.getTransferData(armyArrayFlavor);
					for (Army a : armies) {
						CombatArmy ca = new CombatArmy(a);
						c.addToSide(this.side, ca);
						if (this.side == 0) {
							CombatForm.this.side1TableModel.addRow(ca);
						} else if (this.side == 1){
							CombatForm.this.side2TableModel.addRow(ca);
						}
						else {
							CombatForm.this.otherTableModel.addRow(ca);
						}
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyFlavor)) {
					CombatArmy ca = new CombatArmy((Army) t.getTransferData(armyFlavor));
					c.addToSide(this.side, ca);
					if (this.side == 0) {
						CombatForm.this.side1TableModel.addRow(ca);
					} else  if (this.side == 1){
						CombatForm.this.side2TableModel.addRow(ca);
					} else {
						CombatForm.this.otherTableModel.addRow(ca);
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyEstimateArrayFlavor)) {
					ArmyEstimate[] armies = (ArmyEstimate[]) t.getTransferData(armyEstimateArrayFlavor);
					for (ArmyEstimate a : armies) {
						CombatArmy ca = new CombatArmy(a);
						c.addToSide(this.side, ca);
						if (this.side == 0) {
							CombatForm.this.side1TableModel.addRow(ca);
						} else  if (this.side == 1){
							CombatForm.this.side2TableModel.addRow(ca);
						}
						else {
							CombatForm.this.otherTableModel.addRow(ca);
						}
						
						
					}
					runCombat();
				} else if (t.isDataFlavorSupported(armyEstimateFlavor)) {
					CombatArmy ca = new CombatArmy((ArmyEstimate) t.getTransferData(armyEstimateFlavor));
					c.addToSide(this.side, ca);
					if (this.side == 0) {
						CombatForm.this.side1TableModel.addRow(ca);
					} else  if (this.side == 1){
						CombatForm.this.side2TableModel.addRow(ca);
					}
					else {
						CombatForm.this.otherTableModel.addRow(ca);
					}
					runCombat();
				} else if(t.isDataFlavorSupported(combatArmyArrayFlavor)) {
					if (this.side == CombatForm.this.draggedFrom) return;
					CombatArmy[] cas = (CombatArmy[]) t.getTransferData(combatArmyArrayFlavor);
					for (CombatArmy ca : cas) {
						c.addToSide(this.side, ca);
						if (this.side == 0) {
							CombatForm.this.side1TableModel.addRow(ca);
						} else  if (this.side == 1){
							CombatForm.this.side2TableModel.addRow(ca);
						}
						else {
							CombatForm.this.otherTableModel.addRow(ca);
						}
						c.removeFromSide(CombatForm.this.draggedFrom, ca);
						CombatForm.this.refreshArmies();
					}
					runCombat();
					
				} else if(t.isDataFlavorSupported(combatArmyFlavor)) {
					if (this.side == CombatForm.this.draggedFrom) return;
					CombatArmy ca = (CombatArmy) t.getTransferData(combatArmyArrayFlavor);
					c.addToSide(this.side, ca);
					if (this.side == 0) {
						CombatForm.this.side1TableModel.addRow(ca);
					} else  if (this.side == 1){
						CombatForm.this.side2TableModel.addRow(ca);
					}
					else {
						CombatForm.this.otherTableModel.addRow(ca);
					}
					c.removeFromSide(CombatForm.this.draggedFrom, ca);
					CombatForm.this.refreshArmies();

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

		@Override
		public void drop(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			try {
				DataFlavor popCenterFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + PopulationCenter.class.getName()); //$NON-NLS-1$

				if (t.isDataFlavorSupported(popCenterFlavor)) {
					PopulationCenter pc = (PopulationCenter) t.getTransferData(popCenterFlavor);
					CombatPopCenter cpc = new CombatPopCenter();
					cpc.setName(pc.getName());
					cpc.setNationNo(pc.getNationNo());
					cpc.setLoyalty(pc.getLoyalty());
					cpc.setFort(pc.getFortification());
					cpc.setSize(pc.getSize());
					CombatForm.this.popCenterTableModel.getRows().clear();
					CombatForm.this.popCenterTableModel.addRow(cpc);
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
			ConfirmationDialog md = new ConfirmationDialog(Messages.getString("CombatForm.RemovePC"), Messages.getString("Remove pop center?")) { //$NON-NLS-1$
				@Override
				protected void onConfirm() {
					c.setSide2Pc(null);
					CombatForm.this.popCenterTableModel.getRows().clear();
					CombatForm.this.popCenterTableModel.fireTableDataChanged();
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
			if (CombatForm.this.popCenterTableModel.getRowCount() == 0) {
				pc = new CombatPopCenter();
			} else {
				pc = (CombatPopCenter) CombatForm.this.popCenterTableModel.getRow(0);
			}
			FormModel formModel = FormModelHelper.createFormModel(pc);
			final CombatPopCenterForm form = new CombatPopCenterForm(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {

				@Override
				protected void onAboutToShow() {
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					Combat c = (Combat) getFormObject();
					CombatPopCenter pc1 = (CombatPopCenter) form.getFormObject();
					if (CombatForm.this.popCenterTableModel.getRowCount() == 0) {
						c.setSide2Pc(pc1);
						CombatForm.this.popCenterTableModel.addRow(pc1);
						CombatForm.this.popCenterTableModel.fireTableDataChanged();
					}

					runCombat();
					return true;
				}
			};
			dialog.setTitle(Messages.getString("CombatForm.EditPC.title")); //$NON-NLS-1$
			dialog.showDialog();

		}

	}

}
