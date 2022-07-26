package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JComponent;

import org.joverseer.domain.Company;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.ui.domain.CompanyWrapper;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.joverseer.ui.support.controls.TableUtils;

/**
 * List view for companies
 * 
 * @author Marios Skounakis
 */
public class CompanyListView extends BaseItemListView {
	public CompanyListView() {
		super(CompanyTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 40, 48, 64, 320 };
	}

	@Override
	protected void setItems() {
		Turn t = this.getTurn();
		if (t == null)
			return;
		Container<Company> items = t.getCompanies();
		ArrayList<CompanyWrapper> filteredItems = new ArrayList<CompanyWrapper>();
		AbstractListViewFilter filter = getActiveFilter();
		for (Company o : items.getItems()) {
			CompanyWrapper cw = new CompanyWrapper(o);
			if (filter == null || filter.accept(cw))
				filteredItems.add(cw);
		}
		;
		this.tableModel.setRows(filteredItems);
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		TableUtils.setTableColumnRenderer(this.table, CompanyTableModel.iHexNo, new HexNumberCellRenderer(this.tableModel));
		return c;
	}

}
