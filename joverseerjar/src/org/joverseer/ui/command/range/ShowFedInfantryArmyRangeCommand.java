/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowFedInfantryArmyRangeCommand extends ShowArmyRangeCommand {
	public ShowFedInfantryArmyRangeCommand(int hexNo) {
		super("showFedInfantryArmyRangeCommand", false, true, hexNo);
	}
}