/**
 * 
 */
package org.joverseer.ui.command.range;

public class ShowUnfedNavyOpenSeasRangeCommand extends ShowNavyRangeCommand {
	public ShowUnfedNavyOpenSeasRangeCommand(int hexNo) {
		super("showUnfedNavyOpenSeasRangeCommand", false, true, hexNo);
	}
}