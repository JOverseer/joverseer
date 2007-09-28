package org.joverseer.ui.listviews;

import java.util.ArrayList;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.TurnReportItem;

public class TurnReportListView extends ItemListView {

	public TurnReportListView() {
		super("abc", TurnReportTableModel.class);
	}

	protected int[] columnWidths() {
		return new int[]{64, 32, 400};
	}

	protected void setItems() {
		Game g = GameHolder.instance().getGame();
		
		ArrayList items = new ArrayList();
		
		Turn t = g.getTurn();
		Turn prevTurn = g.getTurn(g.getCurrentTurn() - 1);
		for (PlayerInfo pi : (ArrayList<PlayerInfo>)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).getItems()) {
			int nationNo = pi.getNationNo();
			for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo)) {
				PopulationCenter prevTurnPop = (PopulationCenter)prevTurn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
				if (prevTurnPop == null && pc.getSize().equals(PopulationCenterSizeEnum.camp)) {
					TurnReportItem tri = new TurnReportItem();
					tri.setNationNo(nationNo);
					tri.setHexNo(pc.getHexNo());
					tri.setDescription("Created camp '" + pc.getName() + "'");
					items.add(tri);
				}
			}
		}
		
		tableModel.setRows(items);
	}
}
