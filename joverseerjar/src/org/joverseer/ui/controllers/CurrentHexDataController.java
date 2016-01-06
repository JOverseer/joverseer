package org.joverseer.ui.controllers;

import org.joverseer.domain.Army;
import org.joverseer.ui.viewers.HexInfoController;
import org.joverseer.ui.viewers.PopulationCenterController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class CurrentHexDataController {
	@FXML
	public ListView<Army> lstArmies;
	public PopulationCenterController populationCenterController;
	public HexInfoController hexInfoController;
	public void hideAllArmies()
	{
		lstArmies.setVisible(false);
	}
	public void showAllArmies()
	{
		lstArmies.setVisible(true);
	}
}
