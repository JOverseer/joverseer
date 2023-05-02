package org.joverseer.ui.command;

import java.awt.Point;
import java.util.ArrayList;

import org.joverseer.JOApplication;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.command.ActionCommand;

public class CreateCombatForHexCommand extends ActionCommand {
	Integer hexNo;

	//dependencies
	GameHolder gameHolder;

	public CreateCombatForHexCommand(Integer hexNo,GameHolder gameHolder) {
		super("createCombatForHexCommand");
		this.hexNo = hexNo;
		this.gameHolder = gameHolder;
	}

	protected int getHexNo() {
		if (this.hexNo != null) {
			return this.hexNo;
		}
		Point p = MapPanel.instance().getSelectedHex();
		return (int) p.getX() * 100 + (int) p.getY();
	}

	@Override
	protected void doExecuteCommand() {
		int hex = getHexNo();
		Game game = this.gameHolder.getGame();

		Combat combat = new Combat();
		combat.setMaxRounds(10);
		combat.setHexNo(hex);
		combat.setArmiesAndPCFromHex();

		combat.setDescription("Combat at " + String.valueOf(hex));
		combat.loadTerrainAndClimateFromHex();
		combat.autoSetRelationsToHated();
		game.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(combat);
		JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
		ShowCombatCalculatorCommand cmd = new ShowCombatCalculatorCommand(combat,this.gameHolder);
		cmd.execute();
	}
}
