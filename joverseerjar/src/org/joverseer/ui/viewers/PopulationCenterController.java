package org.joverseer.ui.viewers;

import java.net.URL;
import java.util.ResourceBundle;

import org.joverseer.domain.PopulationCenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class PopulationCenterController {

	@FXML
	private Label pcName;
	@FXML
	private Label loyalty;
	@FXML
	private Label owner;
	@FXML
	private Label pcSize;
	@FXML
	private Label fortification;
	@FXML
	private Label seaShelter;
	@FXML
	private Label lostThisTurn;
	@FXML
	private Label productionDescription;
	@FXML
	private Label turnInfo;

	public void populate(PopulationCenter pc)
	{
		this.pcName.setText(pc.getName());
		this.loyalty.setText(Integer.toString(pc.getLoyalty()));
		this.owner.setText(pc.getNation().getShortName());
		this.pcSize.setText(pc.getSize().getRenderString());
		this.fortification.setText(pc.getFortification().getRenderString());
		this.seaShelter.setText(pc.getHarbor().getRenderString());
//		this.lostThisTurn.setText();
		
	}
	public void hideAll()
	{

	}
	public void showAll()
	{
		
	}
}
