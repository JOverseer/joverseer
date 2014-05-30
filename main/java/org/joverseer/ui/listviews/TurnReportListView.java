package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.TurnReportItem;
import org.joverseer.ui.support.Messages;

public class TurnReportListView extends ItemListView {

	public TurnReportListView() {
		super("abc", TurnReportTableModel.class); //$NON-NLS-1$
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 64, 32, 400 };
	}

	@Override
	protected void setItems() {
		Game g = GameHolder.instance().getGame();

		ArrayList<TurnReportItem> items = new ArrayList<TurnReportItem>();

		Turn t = g.getTurn();
		Turn prevTurn = g.getTurn(g.getCurrentTurn() - 1);
		for (PlayerInfo pi : g.getTurn().getPlayerInfo().getItems()) {
			int nationNo = pi.getNationNo();
			for (PopulationCenter pc : t.getPopulationCenters().findAllByProperty("nationNo", nationNo)) { //$NON-NLS-1$
				PopulationCenter prevTurnPop = prevTurn.getPopulationCenters().findFirstByProperty("hexNo", pc.getHexNo()); //$NON-NLS-1$
				if (prevTurnPop == null && pc.getSize().equals(PopulationCenterSizeEnum.camp)) {
					TurnReportItem tri = new TurnReportItem();
					tri.setNationNo(nationNo);
					tri.setHexNo(pc.getHexNo());
					tri.setDescription(Messages.getString("TurnReportListView.CreatedCamp",new String[] {pc.getName()})); //$NON-NLS-1$
					items.add(tri);
				}
			}
		}

		this.tableModel.setRows(items);
	}
}
