/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowUnfedCavalryArmyRangeCommand extends ShowArmyRangeCommand {
	public ShowUnfedCavalryArmyRangeCommand(int hexNo) {
		super("showUnfedCavalryArmyRangeCommand", true, false, hexNo);
	}
}