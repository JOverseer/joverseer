package org.joverseer.ui.command;

import java.awt.Point;
import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
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
		combat.autoSetCombatRelations();
		game.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(combat);
		JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
		ShowCombatCalculatorCommand cmd = new ShowCombatCalculatorCommand(combat,this.gameHolder);
		cmd.execute();
	}
}
