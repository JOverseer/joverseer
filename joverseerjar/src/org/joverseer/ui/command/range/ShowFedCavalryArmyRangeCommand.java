/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowFedCavalryArmyRangeCommand extends ShowArmyRangeCommand {
	public ShowFedCavalryArmyRangeCommand(int hexNo) {
		super("showFedCavalryArmyRangeCommand", true, true, hexNo);
	}
}