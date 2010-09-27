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
		ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
		filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
		filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
		return new AbstractListViewFilter[][] { filters.toArray(new AbstractListViewFilter[] {}) };
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(tableModel) {

			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				Component c = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				JLabel lbl = (JLabel) c;
				Integer v = (Integer) arg1;
				if (v == null || v.equals(0)) {
					lbl.setText("");
				}
				return c;
			}

		});
		return c;
	}
}
