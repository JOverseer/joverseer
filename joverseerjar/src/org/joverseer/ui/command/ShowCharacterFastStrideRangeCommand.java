package org.joverseer.ui.command;

/**
 * Shows the movement range for a character using the Fast Stride spell on the map
 * 
 * @author Marios Skounakis
 */
public class ShowCharacterFastStrideRangeCommand extends ShowCharacterMovementRangeCommand {

    public ShowCharacterFastStrideRangeCommand(int hexNo) {
        super(hexNo, 16);
    }
}
