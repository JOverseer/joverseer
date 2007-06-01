package org.joverseer.ui.command;

/**
 * Shows the movement range for a character using the Long Stride spell on the map
 * 
 * @author Marios Skounakis
 */
public class ShowCharacterLongStrideRangeCommand extends ShowCharacterMovementRangeCommand {

    public ShowCharacterLongStrideRangeCommand(int hexNo) {
        super(hexNo, 14);
    }
}
