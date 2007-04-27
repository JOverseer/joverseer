package org.joverseer.support.readers.pdf;

import org.joverseer.game.Turn;


public interface OrderResult {
    public void updateGame(Turn turn, int nationNo, String character);
}
