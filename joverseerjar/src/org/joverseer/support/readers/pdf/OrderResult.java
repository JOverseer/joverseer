package org.joverseer.support.readers.pdf;

import java.time.LocalDateTime;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;

/**
 * Interface for objects that hold information about order results (e.g. LATResultWrapper)
 * 
 * @author Marios Skounakis
 */
public interface OrderResult {
    public void updateGame(Game game, Turn turn, int nationNo, String character);
    
    public void updateGame(Game game, Turn turn, LocalDateTime dt, int nationNo, String character);

}
