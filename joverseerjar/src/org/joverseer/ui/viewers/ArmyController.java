package org.joverseer.ui.viewers;

import java.net.URL;
import java.util.ResourceBundle;

import org.joverseer.domain.Army;
import org.joverseer.domain.PopulationCenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ArmyController {

	@FXML
	private Label leader;
	@FXML
	private Label nation;
	@FXML
	private Label armySize;
	@FXML
	private Label armyOrNavy;
	@FXML
	private Label composition;
	@FXML
	private Label fedOrNot;
	@FXML
	private Label infOrCav;

	public void populate(Army a)
	{
		this.leader.setText(a.getCommanderName());
		this.nation.setText(a.getNation().getShortName());
		this.armySize.setText(a.getSize().toString());
	}
	public void hideAll()
	{

	}
	public void showAll()
	{
		
	}
}
