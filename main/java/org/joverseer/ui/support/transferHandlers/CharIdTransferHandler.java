package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.domain.Character;


public class CharIdTransferHandler extends StringTransferHandler {

    public CharIdTransferHandler(String arg0) {
        super(arg0);
    }

    protected String exportString(JComponent c) {
        String v = super.exportString(c);
        Game g = GameHolder.instance().getGame();
        Character ch = (Character)g.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", v);
        return ch.getId() + "     ".substring(0, 5 - ch.getId().length());
    }
}
