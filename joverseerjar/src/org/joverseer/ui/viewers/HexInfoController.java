package org.joverseer.ui.viewers;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.tools.HexInfoHistory;
import org.joverseer.ui.support.Messages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class HexInfoController implements Initializable {

	@FXML
	Label valHexNo;
	@FXML
	Label valTerrain;
	@FXML
	Label valClimate;
	@FXML
	Label valTurnInfo;
	@FXML
	TableView<MovementCost> iMovementCost;
	@FXML
	TableColumn<MovementCost, String> iDirection; 
	@FXML
	TableColumn<MovementCost, Integer> iInfantryCost; // can be '-' = not available.
	@FXML
	TableColumn<MovementCost, Integer> iCavalryCost;
	
	final HashMap<MovementDirection, MovementCost> costs = new HashMap<MovementDirection, MovementCost>();

	final ObservableList<MovementCost> data = FXCollections.observableArrayList();

	private static class DashIntegerTableCell extends TableCell<MovementCost,Integer> 
	{
		@Override
		public void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			setText(empty? null: getString());
			setGraphic(null);
		}
		private String getString() {
			if (getItem() != null) {
				if (getItem() == -1) {
					return "-";
				} else {
					return getItem().toString();
				}
			} else {
				return "-";
			}
		}
	}
	// we allocate the space for the movement costs first
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.iDirection.setSortable(false);
		this.iDirection.setCellValueFactory(new PropertyValueFactory<MovementCost, String>("direction"));
		MovementCost cost;
		for (MovementDirection md : MovementDirection.values()) {
			cost = new MovementCost(resources.getString("movementCosts." + md.getDir()),-1,-1);
			costs.put(md, cost);
			data.add(cost);
			
		}
		this.iInfantryCost.setSortable(false);
		this.iInfantryCost.setCellValueFactory(new PropertyValueFactory<MovementCost, Integer>("infantryCost"));
		this.iInfantryCost.setCellFactory(new Callback<TableColumn<MovementCost, Integer>,TableCell<MovementCost, Integer>>() {
			@Override
			public TableCell call(TableColumn<MovementCost, Integer> p) {
				return new DashIntegerTableCell();
			}
		});
		
		
		this.iCavalryCost.setSortable(false);
		this.iCavalryCost.setCellValueFactory(new PropertyValueFactory<MovementCost, Integer>("cavalryCost"));
		this.iCavalryCost.setCellFactory(new Callback<TableColumn<MovementCost, Integer>,TableCell<MovementCost, Integer>>() {
			@Override
			public TableCell call(TableColumn<MovementCost, Integer> p) {
				return new DashIntegerTableCell();
			}
		});
		
		this.iMovementCost.setItems(this.data);
		
	}
	public void hideAll()
	{
		iMovementCost.getParent().setVisible(false);
	}
	public void showAll()
	{
		iMovementCost.getParent().setVisible(true);
	}
	public void populate(Hex h)
	{
		valHexNo.setText(h.getHexNoStr());
		valTerrain.setText(h.getTerrain().getRenderString());
		
		Integer latestTurnInfo = HexInfoHistory.getLatestHexInfoTurnNoForHex(h.getHexNo());
		if (latestTurnInfo == null || latestTurnInfo == -1) {
			this.valTurnInfo.setText(Messages.getString("HexInfoViewer.Never")); //$NON-NLS-1$
		} else {
			this.valTurnInfo.setText(Messages.getString("HexInfoViewer.Turn") + latestTurnInfo); //$NON-NLS-1$
		}
		
		int startHexNo = h.getHexNo();
		if (startHexNo > 0) {
			int cost;
			for (MovementDirection md : MovementDirection.values()) {
				cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), false, true, true, null, startHexNo);
				costs.get(md).setInfantryCost(cost);
				cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), true, true, true, null, startHexNo);
				costs.get(md).setCavalryCost(cost);
			}
		}
		// we've changed the the data but not via the observableList, so we need to prod the update.
		// a better way would be nice.
		this.iMovementCost.setItems(null);
		this.iMovementCost.layout();
		this.iMovementCost.setItems(this.data);
	}
}
