package org.joverseer.ui.listviews;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.ChangedPCInfo;
import org.joverseer.ui.listviews.ArmyListView.ExportArmyDataAction;
import org.joverseer.ui.listviews.commands.GenericCopyToClipboardCommand;
import org.joverseer.ui.listviews.commands.PopupMenuCommand;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.table.SortableTableModel;

public class ChangedPCListView extends BaseItemListView {

    public ChangedPCListView() {
        super(ChangedPCTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {48, 64, 120, 64, 120, 48};
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
    		if (oldPc.getSize().equals(newPc.getSize())) return null;
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
		ArrayList items = new ArrayList();
		if (GameHolder.instance().hasInitializedGame()) {
			Game g = GameHolder.instance().getGame();
			Turn p = g.getTurn(g.getCurrentTurn()-1);
			Turn t = g.getTurn();
			if (p != null) {
				for (PlayerInfo pi : (ArrayList<PlayerInfo>)p.getContainer(TurnElementsEnum.PlayerInfo).getItems()) {
					if (!nationImported(pi.getNationNo(), t.getTurnNo(), g)) continue;
					for (PopulationCenter opc : (ArrayList<PopulationCenter>)p.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", pi.getNationNo())) {
						PopulationCenter pc = (PopulationCenter)t.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", opc.getHexNo());
						ChangedPCInfo cpi = comparePCs(opc, pc);
						if (cpi != null) items.add(cpi);
					}
					for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", pi.getNationNo())) {
						PopulationCenter opc = (PopulationCenter)p.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
						ChangedPCInfo cpi = comparePCs(opc, pc);
						if (cpi != null) items.add(cpi);
					}
				}
			}
			
		}
		tableModel.setRows(items);
		
	}

    
}
