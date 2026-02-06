package org.joverseer.ui.listviews;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.table.JTableHeader;

import org.joverseer.JOApplication;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.tools.CombatUtils;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.MultilineTableCellRenderer;
import com.jidesoft.grid.SortTableHeaderRenderer;
import com.jidesoft.grid.SortableTable;

/**
 * List view that shows ArmyEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmyEstimatesListView extends ItemListView {

	public ArmyEstimatesListView() {
		super(TurnElementsEnum.ArmyEstimate, ArmyEstimatesTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 42, 42, 80, 64, 130, 48, 48, 100, 120, 120, 120 };
	}

	/**
	 * Implements its own createControlImpl without calling the base because I
	 * need to create a JideTable (to use the MutliLineRenderer) instead of a
	 * standard Spring/Swing table
	 */
	@Override
	protected JComponent createControlImpl() {
		this.tableModel = super.createBeanTableModel();

		TableLayoutBuilder tlb = new TableLayoutBuilder();

		JPanel fp = createFilterPanel();
		if (fp != null) {
			tlb.cell(fp, "align=left"); //$NON-NLS-1$
			tlb.row();
		}

		setItems();

		// create the JTable instance
		this.table = TableUtils.createStandardSortableTable(this.tableModel);
		this.table = new SortableTable(this.table.getModel()) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7989424858163400299L;

			@Override
			protected void initTable() {
				super.initTable();
				setSortTableHeaderRenderer();
			}

			@Override
			protected SortTableHeaderRenderer createSortHeaderRenderer() {
				return new SortTableHeaderRenderer() {

					@Override
					protected void initComponents() {
						super.initComponents();
						this._headerPanel.add(this._titlePanel, BorderLayout.CENTER);
					}

					@Override
					protected JLabel createLabel(String text) {
						return new JLabel(text, SwingConstants.LEADING);
					}
				};
			}

			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(this.columnModel);
			}
		};
		((JideTable) this.table).setRowResizable(true);
		((JideTable) this.table).setRowAutoResizes(true);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.table, columnWidths());

		String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols"); //$NON-NLS-1$
		if (pval.equals("yes")) { //$NON-NLS-1$
			this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		} else {
			this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		this.table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.addMouseListener(this);
		this.table.addMouseMotionListener(this);
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(this.table.getBackground());
		tlb.cell(scrollPane);

		if (getDefaultSort() != null) {
			ImageSource imgSource = JOApplication.getImageSource();
			Icon ico = new ImageIcon(imgSource.getImage("restoreSorting.icon")); //$NON-NLS-1$
			JLabel restoreSorting = new JLabel();
			restoreSorting.setIcon(ico);
			restoreSorting.setPreferredSize(new Dimension(16, 16));
			restoreSorting.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// ((SortableTableModel)
					// table.getModel()).sortByColumns(getDefaultSort());
				}

			});
			// ((SortableTableModel)
			// table.getModel()).sortByColumns(getDefaultSort());
			restoreSorting.setToolTipText("Restore default sort order"); //$NON-NLS-1$
			tlb.cell(restoreSorting, "colspec=left:30px valign=top"); //$NON-NLS-1$
		}

		TableLayoutBuilder lb = new TableLayoutBuilder();
		for (JComponent compo : getButtons()) {
			lb.cell(compo, "colspec=left:30px valign=top"); //$NON-NLS-1$
			lb.relatedGapRow();
			lb.row();
		}
		JPanel pnl = lb.getPanel();
		tlb.cell(pnl, "colspec=left:30px valign=top"); //$NON-NLS-1$

		JPanel p = tlb.getPanel();

		MultilineTableCellRenderer r = new MultilineTableCellRenderer();
		r.setWrapStyleWord(true);
		r.setLineWrap(true);
		this.table.setDefaultRenderer(String.class, r);
		this.table.setDefaultRenderer(Integer.class, r);

		return p;
	}

	@Override
	protected void startDragAndDropAction(MouseEvent e) {
		final ArmyEstimate[] selectedArmies = new ArmyEstimate[this.table.getSelectedRowCount()];
		String copyString = ""; //$NON-NLS-1$
		for (int i = 0; i < this.table.getSelectedRowCount(); i++) {
			int idx = ((com.jidesoft.grid.SortableTableModel) this.table.getModel()).getActualRowAt(this.table.getSelectedRows()[i]);
			ArmyEstimate a = (ArmyEstimate) this.tableModel.getRow(idx);
			selectedArmies[i] = a;
			String ln = ""; //$NON-NLS-1$
			for (int j = 0; j < this.table.getColumnCount(); j++) {
				Object v = this.table.getValueAt(i, j);
				if (v == null)
					v = ""; //$NON-NLS-1$
				ln += (ln.equals("") ? "" : "\t") + v; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			copyString += (copyString.equals("") ? "" : "\n") + ln; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		final String str = copyString;

		TransferHandler handler = new GenericExportTransferHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5117995273740870509L;

			@Override
			protected Transferable createTransferable(JComponent arg0) {
				try {
					Transferable t = new GenericTransferable(new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ArmyEstimate[].class.getName() + "\""), new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ArmyEstimate.class.getName()), DataFlavor.stringFlavor }, new Object[] { selectedArmies, selectedArmies[0], str }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					return t;
				} catch (Exception exc) {
					exc.printStackTrace();
					return null;
				}

			}
		};
		this.table.setTransferHandler(handler);
		handler.exportAsDrag(this.table, e, TransferHandler.COPY);
	}

	private ArmyEstimate getSelectedArmyEstimate()
	{
		ArmyEstimate ae = null;
		try {
			ae = (ArmyEstimate)this.getSelectedObject();
		} catch (Exception exc) {
			// do nothing.
		}
		return ae;
	}
	@Override
	protected JComponent[] getButtons() {
		ArrayList<JComponent> comps = new ArrayList<JComponent>();
		comps.addAll(Arrays.asList(super.getButtons()));
		JLabelButton popupMenu = new JLabelButton();
		ImageSource imgSource = JOApplication.getImageSource();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		popupMenu.setIcon(ico);
		popupMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				ArmyEstimate ae = ArmyEstimatesListView.this.getSelectedArmyEstimate(); 
				CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("armyEstimatesListView", new Object[] { new ShowENHICommand(ae), }); //$NON-NLS-1$
				return group.createPopupMenu();
			}
		});
		comps.add(popupMenu);
		return comps.toArray(new JComponent[] {});
	}

	class ShowENHICommand extends ActionCommand {
		ArmyEstimate estimate;

		public ShowENHICommand(ArmyEstimate estimate) {
			super("ShowENHICommand"); //$NON-NLS-1$
			this.estimate = estimate;
		}

		@Override
		protected void doExecuteCommand() {
			if (this.estimate != null) {
				String str = Messages.getString("ArmyEstimatesListView.ForArmy", new String[] { this.estimate.getCommanderName() }); //$NON-NLS-1$
				new CombatUtils();
				int enhi = CombatUtils.getNakedHeavyInfantryEquivalent3(this.estimate, 0);
				str += Messages.getString("ArmyEstimatesListView.AverageLosses", new Object[] { enhi , (100 - this.estimate.getEffectiveLosses(0))}); //$NON-NLS-1$ 
				new CombatUtils();
				enhi = CombatUtils.getNakedHeavyInfantryEquivalent3(this.estimate, -1);
				str += Messages.getString("ArmyEstimatesListView.LowestLosses", new Object[] { enhi  , (100 - this.estimate.getEffectiveLosses(-1))}); //$NON-NLS-1$
				new CombatUtils();
				enhi = CombatUtils.getNakedHeavyInfantryEquivalent3(this.estimate, 1);
				str += Messages.getString("ArmyEstimatesListView.HighestLosses", new Object[] { enhi , (100 - this.estimate.getEffectiveLosses(1))}); //$NON-NLS-1$
				if (this.estimate.getMoraleRange().equals("?")) { //$NON-NLS-1$
					str += Messages.getString("ArmyEstimatesListView.Failed"); //$NON-NLS-1$
				}
				MessageDialog dlg = new MessageDialog(Messages.getString("ArmyEstimatesListView.dialog.title"), str); //$NON-NLS-1$
				dlg.showDialog();
			}
		}
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][] { NationFilter.createNationFilters(), AllegianceFilter.createAllegianceFilters(false) };
	}

}
