package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.domain.Character;

/**
 * TransferHandler for character id
 * @author Marios Skounakis
 */

@SuppressWarnings("serial")
public class CharIdTransferHandler extends StringTransferHandler {

    public CharIdTransferHandler(String arg0) {
        super(arg0);
    }

    @Override
	protected String exportString(JComponent c) {
        String v = super.exportString(c);
        Game g = GameHolder.instance().getGame();
        Character ch = g.getTurn().getCharByName(v);
        return ch.getId() + "     ".substring(0, 5 - ch.getId().length());
    }
}
