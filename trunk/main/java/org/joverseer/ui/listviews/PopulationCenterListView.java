package org.joverseer.ui.listviews;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.listviews.commands.GenericCopyToClipboardCommand;
import org.joverseer.ui.listviews.commands.PopupMenuCommand;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.renderers.PopCenterInfoSourceTableCellRenderer;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;

/**
 * List view for PopulationCenter objects
 * 
 * @author Marios Skounakis
 */
public class PopulationCenterListView extends ItemListView {

	public PopulationCenterListView() {
		super(TurnElementsEnum.PopulationCenter, PopulationCenterTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 40, 96, 42, 64, 54, 80, 96, 68, 42, 42, 42, 42, 42, 42, 42, 42 };
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		this.table.setDefaultRenderer(InfoSource.class, new PopCenterInfoSourceTableCellRenderer(this.tableModel));
		return c;
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
		filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}) };
	}

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[] { new ColumnToSort(0, 2), new ColumnToSort(0, 1) };
	}

	@Override
	protected JComponent[] getButtons() {
		return new JComponent[] { new PopupMenuCommand().getButton(new Object[] { new GenericCopyToClipboardCommand(this.table) }) };
	}

	@Override
	protected boolean hasTextFilter() {
		return true;
	}

	@Override
	protected AbstractListViewFilter getTextFilter(String txt) {
		if (txt == null || txt.equals(""))
			return super.getTextFilter(txt);
		try {
			int hexNo = Integer.parseInt(txt.trim());
			return new HexFilter("", hexNo);
		} catch (Exception exc) {
			// do nothing
		}
		return new TextFilter("Name", "name", txt);
	}

	/**
	 * Drag and drop exports: - PopulationCenter[] array of all selected items -
	 * PopulationCenter the first selected item - String representation of all
	 * selected items
	 */
	@Override
	protected void startDragAndDropAction(MouseEvent e) {
		final PopulationCenter[] selectedItems = new PopulationCenter[this.table.getSelectedRowCount()];
		String copyString = "";
		for (int i = 0; i < this.table.getSelectedRowCount(); i++) {
			int idx = ((SortableTableModel) this.table.getModel()).convertSortedIndexToDataIndex(this.table.getSelectedRows()[i]);
			PopulationCenter a = (PopulationCenter) this.tableModel.getRow(idx);
			selectedItems[i] = a;
			String ln = "";
			for (int j = 0; j < this.table.getColumnCount(); j++) {
				Object v = this.table.getValueAt(i, j);
				if (v == null)
					v = "";
				ln += (ln.equals("") ? "" : "\t") + v;
			}
			copyString += (copyString.equals("") ? "" : "\n") + ln;
		}
		final String str = copyString;

		TransferHandler handler = new GenericExportTransferHandler() {

			@Override
			protected Transferable createTransferable(JComponent arg0) {
				try {
					Transferable t = new GenericTransferable(new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + PopulationCenter[].class.getName() + "\""), new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + PopulationCenter.class.getName()), DataFlavor.stringFlavor }, new Object[] { selectedItems, selectedItems[0], str });
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

}
