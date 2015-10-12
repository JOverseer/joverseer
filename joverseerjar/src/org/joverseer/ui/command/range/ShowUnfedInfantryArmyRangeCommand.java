/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowUnfedInfantryArmyRangeCommand extends ShowArmyRangeCommand {
	public ShowUnfedInfantryArmyRangeCommand(int hexNo) {
		super("showUnfedInfantryArmyRangeCommand", false, false, hexNo);
	}
}