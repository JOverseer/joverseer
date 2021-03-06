package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
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

	@SuppressWarnings("null")
	protected ChangedPCInfo comparePCs(PopulationCenter oldPc, PopulationCenter newPc) {
		if ((oldPc == null) && (newPc==null)) return null;
		
		ChangedPCInfo cpi = new ChangedPCInfo();
		if (oldPc != null && newPc == null) {
			return cpi.copyFrom(oldPc,"Disappeared");
		}  
		if (oldPc == null && newPc != null) {
			return cpi.copyFrom(newPc,"Created");
		}
		if (oldPc.getNationNo().equals(newPc.getNationNo())) {
			if (oldPc.getSize().equals(newPc.getSize()))
				return null;
			if (newPc.getSize().getCode() > oldPc.getSize().getCode()) {
				return cpi.copyFrom(newPc,"Improved");
			} else {
				return cpi.copyFrom(newPc,"Degrade");
			}
		} else if (!oldPc.getNationNo().equals(newPc.getNationNo())) {
			if (newPc.getSize().getCode() < oldPc.getSize().getCode()) {
				return cpi.copyFrom(newPc,"Degrade & Lost");
			} else {
				return cpi.copyFrom(newPc,"Ownership change");
			}
		}
		return null;
	}

	@Override
	protected void setItems() {
		ArrayList<ChangedPCInfo> items = new ArrayList<ChangedPCInfo>();
		Turn t = this.getTurn();
		if (t != null) {
			Turn p = this.game.getTurn(this.game.getCurrentTurn() - 1);
			if (p != null) {
				for (PlayerInfo pi : (ArrayList<PlayerInfo>) p.getContainer(TurnElementsEnum.PlayerInfo).getItems()) {
					if (!nationImported(pi.getNationNo(), t.getTurnNo(), this.game))
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
		this.tableModel.setRows(items);

	}

}
