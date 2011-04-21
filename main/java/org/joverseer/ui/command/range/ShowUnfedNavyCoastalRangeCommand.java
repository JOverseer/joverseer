/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowUnfedNavyCoastalRangeCommand extends ShowNavyRangeCommand {
	public ShowUnfedNavyCoastalRangeCommand(int hexNo) {
		super("showUnfedNavyCoastalRangeCommand", false, false, hexNo);
	}
}