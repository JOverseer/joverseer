package org.joverseer.ui.listviews;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;

/**
 * List view for Starting Characters
 * 
 * @author Marios Skounakis
 */
public class StartingCharacterListView extends ItemListView {

	public StartingCharacterListView() {
		super("characters", StartingCharacterTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 120, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
		filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}) };
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				Component renderer = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				JLabel lbl = (JLabel) renderer;
				Integer v = (Integer) arg1;
				if (v == null || v.equals(0)) {
					lbl.setText("");
				}
				return renderer;
			}

		});
		return c;
	}
}
