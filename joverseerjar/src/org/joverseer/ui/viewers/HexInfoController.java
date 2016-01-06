package org.joverseer.ui.viewers;

import java.net.URL;
import java.util.ResourceBundle;

import org.joverseer.metadata.domain.Hex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HexInfoController implements Initializable {

	@FXML
	Label valHexNo;
	@FXML
	Label valTerrain;
	@FXML
	TableView<MovementCost> iMovementCost;
	@FXML
	TableColumn<MovementCost, String> iDirection; 
	@FXML
	TableColumn<MovementCost, Integer> iInfantryCost; 
	@FXML
	TableColumn<MovementCost, Integer> iCavalryCost;
	
	final ObservableList<MovementCost> data = FXCollections.observableArrayList(
			new MovementCost("nw", 1, 3),
			new MovementCost("ne", 1, 5),
			new MovementCost("e", 1, 5),
			new MovementCost("se", 1, 5),
			new MovementCost("we", 1, 5),
			new MovementCost("w", 1, 5),
			new MovementCost("h", 1, 5)
			);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.iDirection.setCellValueFactory(new PropertyValueFactory<MovementCost, String>("direction"));
		this.iInfantryCost.setCellValueFactory(new PropertyValueFactory<MovementCost, Integer>("infantry"));
		this.iCavalryCost.setCellValueFactory(new PropertyValueFactory<MovementCost, Integer>("cavalry"));
		
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
	}
}
