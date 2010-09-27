package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.ChangedPCInfo;

public class ChangedPCListView extends BaseItemListView {

	public ChangedPCListView() {
		super(ChangedPCTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 48, 64, 120, 64, 120, 48 };
	}

	protected boolean nationImported(int nationNo, int turnNo, Game g) {
		return g.getTurn(turnNo).getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo) != null;
	}

	protected ChangedPCInfo comparePCs(PopulationCenter oldPc, PopulationCenter newPc) {
		ChangedPCInfo cpi = new ChangedPCInfo();
		if (oldPc != null && newPc == null) {
			cpi.setHexNo(oldPc.getHexNo());
			cpi.setSize(oldPc.getSize());
			cpi.setNationNo(oldPc.getNationNo());
			cpi.setReason("Disappeared");
			return cpi;
		}
		if (oldPc == null && newPc != null) {
			cpi.setHexNo(newPc.getHexNo());
			cpi.setSize(newPc.getSize());
			cpi.setNationNo(newPc.getNationNo());
			cpi.setReason("Created");
			return cpi;
		}
		if (oldPc.getNationNo().equals(newPc.getNationNo())) {
			if (oldPc.getSize().equals(newPc.getSize()))
				return null;
			cpi.setHexNo(oldPc.getHexNo());
			cpi.setSize(newPc.getSize());
			cpi.setNationNo(oldPc.getNationNo());
			if (newPc.getSize().getCode() > oldPc.getSize().getCode()) {
				cpi.setReason("Improved");
			} else {
				cpi.setReason("Degrade");
			}
			return cpi;
		} else if (!oldPc.getNationNo().equals(newPc.getNationNo())) {
			cpi.setHexNo(oldPc.getHexNo());
			cpi.setSize(newPc.getSize());
			cpi.setNationNo(oldPc.getNationNo());
			if (newPc.getSize().getCode() < oldPc.getSize().getCode()) {
				cpi.setReason("Degrade & Lost");
			} else {
				cpi.setReason("Ownership change");
			}
			return cpi;
		}
		return null;
	}

	@Override
	protected void setItems() {
		ArrayList<ChangedPCInfo> items = new ArrayList<ChangedPCInfo>();
		if (GameHolder.hasInitializedGame()) {
			Game g = GameHolder.instance().getGame();
			Turn p = g.getTurn(g.getCurrentTurn() - 1);
			Turn t = g.getTurn();
			if (p != null) {
				for (PlayerInfo pi : (ArrayList<PlayerInfo>) p.getContainer(TurnElementsEnum.PlayerInfo).getItems()) {
					if (!nationImported(pi.getNationNo(), t.getTurnNo(), g))
						continue;
					for (PopulationCenter opc : (ArrayList<PopulationCenter>) p.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", pi.getNationNo())) {
						PopulationCenter pc = (PopulationCenter) t.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", opc.getHexNo());
						ChangedPCInfo cpi = comparePCs(opc, pc);
						if (cpi != null)
							items.add(cpi);
					}
					for (PopulationCenter pc : (ArrayList<PopulationCenter>) t.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", pi.getNationNo())) {
						PopulationCenter opc = (PopulationCenter) p.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
						ChangedPCInfo cpi = comparePCs(opc, pc);
						if (cpi != null)
							items.add(cpi);
					}
				}
			}

		}
		tableModel.setRows(items);

	}

}
