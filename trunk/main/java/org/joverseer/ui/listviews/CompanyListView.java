package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Company;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.CompanyWrapper;
import org.springframework.richclient.application.Application;

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
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (!Game.isInitialized(g))
			return;
		Container items = g.getTurn().getContainer(TurnElementsEnum.Company);
		ArrayList<CompanyWrapper> filteredItems = new ArrayList<CompanyWrapper>();
		AbstractListViewFilter filter = getActiveFilter();
		for (Company o : (ArrayList<Company>) items.getItems()) {
			CompanyWrapper cw = new CompanyWrapper(o);
			if (filter == null || filter.accept(cw))
				filteredItems.add(cw);
		}
		;
		tableModel.setRows(filteredItems);
	}

}
