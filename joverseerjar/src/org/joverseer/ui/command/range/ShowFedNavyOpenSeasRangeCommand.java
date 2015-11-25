/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowFedNavyOpenSeasRangeCommand extends ShowNavyRangeCommand {
	public ShowFedNavyOpenSeasRangeCommand(int hexNo) {
		super("showFedNavyOpenSeasRangeCommand", true, true, hexNo);
	}
}