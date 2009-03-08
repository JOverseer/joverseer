package org.joverseer.ui.command;

/**
 * Shows the movement range for a character using the Path Mastery spell on the map
 * 
 * @author Marios Skounakis
 */
public class ShowCharacterPathMasteryRangeCommand extends ShowCharacterMovementRangeCommand {

    public ShowCharacterPathMasteryRangeCommand(int hexNo) {
        super(hexNo, 19);
    }
}
