package org.joverseer.ui.command;

import java.awt.Point;
import java.util.ArrayList;

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
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;

public class CreateCombatForHexCommand extends ActionCommand {
	Integer hexNo;

	public CreateCombatForHexCommand(Integer hexNo) {
		super("createCombatForHexCommand");
		this.hexNo = hexNo;
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
		Game game = GameHolder.instance().getGame();

		Combat combat = new Combat();
		combat.setMaxRounds(10);
		String strHex = String.valueOf(hex);
		//ArrayList<Army> allarmies = game.getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo" }, new Object[] { strHex });
		ArrayList<Army> fparmies = game.getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.FreePeople });
		ArrayList<Army> dsarmies = game.getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.DarkServants });
		ArrayList<Army> ntarmies = game.getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.Neutral });

		if (ntarmies.size() > 0) {
			MessageDialog dlg = new MessageDialog("Error", Application.instance().getApplicationContext().getMessage("createCombatForHexCommand.error.NetrualArmiesFound", null, null));
			dlg.showDialog();
		}

		PopulationCenter pc = (PopulationCenter) game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", hex);
		if (pc != null && (pc.getNationNo() == 0 || pc.getNation().getAllegiance().equals(NationAllegianceEnum.Neutral))) {
			MessageDialog dlg = new MessageDialog("Error", Application.instance().getApplicationContext().getMessage("createCombatForHexCommand.error.PopWithUnknownOrNeutralNationFound", null, null));
			dlg.showDialog();
		}

		ArrayList<Army> side1;
		ArrayList<Army> side2;

		if (pc != null && pc.getNation().getAllegiance().equals(NationAllegianceEnum.FreePeople)) {
			side2 = fparmies;
			side1 = dsarmies;
		} else {
			side1 = fparmies;
			side2 = dsarmies;
		}

		for (int i = 0; i < 2; i++) {
			ArrayList<Army> sideArmies = i == 0 ? side1 : side2;
			for (Army a : sideArmies) {
				CombatArmy ca;
				if (a.computeNumberOfMen() > 0) {
					ca = new CombatArmy(a);
				} else {
					ArmyEstimate ae = (ArmyEstimate) game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", a.getCommanderName());
					if (ae != null) {
						ca = new CombatArmy(ae);

					} else {
						ca = new CombatArmy(a);

					}
				}
				combat.addToSide(i, ca);
				ca.setBestTactic();
			}
		}

		if (pc != null) {
			CombatPopCenter cpc = new CombatPopCenter(pc);
			combat.setSide2Pc(cpc);
		}
		combat.setHexNo(hex);
		String description = Application.instance().getApplicationContext().getMessage("createCombatForHexCommand.success.Title", new Object[] { hex }, null);
		if (pc != null) {
			description += " - " + pc.getName();
		}
		combat.setDescription(description);
		combat.loadTerrainAndClimateFromHex();
		combat.autoSetRelationsToHated();
		game.getTurn().getContainer(TurnElementsEnum.CombatCalcCombats).addItem(combat);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
		ShowCombatCalculatorCommand cmd = new ShowCombatCalculatorCommand(combat);
		cmd.execute();
	}
}
