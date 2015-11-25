/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowFedNavyCoastalRangeCommand extends ShowNavyRangeCommand {
	public ShowFedNavyCoastalRangeCommand(int hexNo) {
		super("showFedNavyCoastalRangeCommand", true, false, hexNo);
	}
}