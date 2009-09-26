package org.joverseer.ui.listviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.JTableHeader;

import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.advancedCharacterListView.AdvancedCharacterTableModel;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.MultilineTableCellRenderer;
import com.jidesoft.grid.SortTableHeaderRenderer;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.SortableTableModel;

/**
 * List view that shows ArmyEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmyEstimatesListView extends ItemListView {

    public ArmyEstimatesListView() {
        super(TurnElementsEnum.ArmyEstimate, ArmyEstimatesTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {42, 42, 80, 64, 130, 48, 48, 100, 120, 120, 120};
    }
    
    

    /**
     * Implements its own createControlImpl without calling the base because I need to
     * create a JideTable (to use the MutliLineRenderer) instead of a standard Spring/Swing table
     */
    protected JComponent createControlImpl() {
        MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");

        // create the table model
        try {
            tableModel = (BeanTableModel) tableModelClass.getConstructor(new Class[] {MessageSource.class})
                    .newInstance(new Object[] {messageSource});
        } catch (InstantiationException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }

        TableLayoutBuilder tlb = new TableLayoutBuilder();

        JPanel fp = createFilterPanel();
        if (fp != null) {
	        tlb.cell(fp, "align=left");
	        tlb.row();
        }

        setItems();

        // create the JTable instance
        table = TableUtils.createStandardSortableTable(tableModel);
        table = new SortableTable(table.getModel()) {

            protected void initTable() {
                super.initTable();
                setSortTableHeaderRenderer();
            }

            protected SortTableHeaderRenderer createSortHeaderRenderer() {
                return new SortTableHeaderRenderer() {

                    protected void initComponents() {
                        super.initComponents();
                        _headerPanel.add(_titlePanel, BorderLayout.CENTER);
                    }

                    protected JLabel createLabel(String text) {
                        return new JLabel(text, JLabel.LEADING);
                    }
                };
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel);
            }
        };
        ((JideTable) table).setRowResizable(true);
        ((JideTable) table).setRowAutoResizes(true);
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(table, columnWidths());

        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
        if (pval.equals("yes")) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        table.getTableHeader().setBackground(Color.WHITE);
        table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(tableModel));
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(table.getBackground());
        tlb.cell(scrollPane);

        if (getDefaultSort() != null) {
            ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
            Icon ico = new ImageIcon(imgSource.getImage("restoreSorting.icon"));
            JLabel restoreSorting = new JLabel();
            restoreSorting.setIcon(ico);
            restoreSorting.setPreferredSize(new Dimension(16, 16));
            restoreSorting.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent arg0) {
                    //((SortableTableModel) table.getModel()).sortByColumns(getDefaultSort());
                }

            });
            //((SortableTableModel) table.getModel()).sortByColumns(getDefaultSort());
            restoreSorting.setToolTipText("Restore default sort order");
            tlb.cell(restoreSorting, "colspec=left:30px valign=top");
        }
        
        TableLayoutBuilder lb = new TableLayoutBuilder();
        for (JComponent compo : getButtons()) {
            lb.cell(compo, "colspec=left:30px valign=top");
            lb.relatedGapRow();
            lb.row();
        }
        JPanel pnl = lb.getPanel();
        pnl.setBackground(Color.WHITE);
        tlb.cell(pnl, "colspec=left:30px valign=top");
        
        JPanel p = tlb.getPanel();
        p.setBackground(Color.WHITE);

        MultilineTableCellRenderer r = new MultilineTableCellRenderer();
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        table.setDefaultRenderer(String.class, r);
        table.setDefaultRenderer(Integer.class, r);

        return p;
    }
    
    

    protected void startDragAndDropAction(MouseEvent e) {
        final ArmyEstimate[] selectedArmies = new ArmyEstimate[table.getSelectedRowCount()];
        String copyString = "";
        for (int i = 0; i < table.getSelectedRowCount(); i++) {
            int idx = ((com.jidesoft.grid.SortableTableModel) table.getModel())
                    .getActualRowAt(table.getSelectedRows()[i]);
            ArmyEstimate a = (ArmyEstimate) tableModel.getRow(idx);
            selectedArmies[i] = a;
            String ln = "";
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object v = table.getValueAt(i, j);
                if (v == null)
                    v = "";
                ln += (ln.equals("") ? "" : "\t") + v;
            }
            copyString += (copyString.equals("") ? "" : "\n") + ln;
        }
        final String str = copyString;

        TransferHandler handler = new GenericExportTransferHandler() {

            protected Transferable createTransferable(JComponent arg0) {
                try {
                    Transferable t = new GenericTransferable(new DataFlavor[] {
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                                    + ArmyEstimate[].class.getName() + "\""),
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class="
                                    + ArmyEstimate.class.getName()), DataFlavor.stringFlavor}, new Object[] {
                            selectedArmies, selectedArmies[0], str});
                    return t;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return null;
                }

            }
        };
        table.setTransferHandler(handler);
        handler.exportAsDrag(table, e, TransferHandler.COPY);
    }

	@Override
	protected JComponent[] getButtons() {
		ArrayList<JComponent> comps = new ArrayList<JComponent>();
        comps.addAll(Arrays.asList(super.getButtons()));
        JLabelButton popupMenu = new JLabelButton();
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        popupMenu.setIcon(ico);
        popupMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
            	ArmyEstimate ae = null;
            	int row = table.getSelectedRow();
                if (row >= 0) {
                    int idx = ((SortableTableModel) table.getModel()).getActualRowAt(row);
                    if (idx < tableModel.getRowCount()) {
	                    try {
	                        Object obj = tableModel.getRow(idx);
	                        ae = (ArmyEstimate) obj;
	                    } catch (Exception exc) {
	
	                    }
                    }
                }
                CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                        "armyEstimatesListView", new Object[] {
                        		new ShowENHICommand(ae),
                        		});
                return group.createPopupMenu();
            }
        });
        comps.add(popupMenu);
        return comps.toArray(new JComponent[] {});
	}

	
	class ShowENHICommand extends ActionCommand {
    	ArmyEstimate estimate;
    	
		public ShowENHICommand(ArmyEstimate estimate) {
			super("ShowENHICommand");
			this.estimate = estimate;
		}

		protected void doExecuteCommand() {
			if (estimate != null) {
				int enhi = new CombatUtils().getNakedHeavyInfantryEquivalent3(estimate);
				String str = "enHI for " + estimate.getCommanderName() + "'s army is " + enhi + " for estimated losses of " + (100 - estimate.getEffectiveLosses()) + "%";
				if (estimate.getMoraleRange().equals("?")) {
					str += "\nJOverseer failed to parse the army's morale from the combat narration, and is assuming 30 morale.";
				}
				MessageDialog dlg = new MessageDialog("Estimated enHI", str);
				dlg.showDialog();
			}
		}
    }


	@Override
	protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][] {
        		NationFilter.createNationFilters(),
        		AllegianceFilter.createAllegianceFilters()};
	}

}
