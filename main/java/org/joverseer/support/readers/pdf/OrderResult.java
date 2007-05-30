package org.joverseer.support.readers.pdf;

import org.joverseer.game.Turn;

/**
 * Interface for objects that hold information about order results (e.g. LATResultWrapper)
 * 
 * @author Marios Skounakis
 */
public interface OrderResult {
    public void updateGame(Turn turn, int nationNo, String character);
}
