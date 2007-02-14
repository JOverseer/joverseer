package org.joverseer.orders;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.domain.Character;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;


public class OrderUtils {
    public static Game getGame() {
        Game g = GameHolder.instance().getGame();
        return g;
    }
    
    public static Turn getTurn() {
        return getGame().getTurn();
    }
    
    public static Character getCharacterFromId(String id) {
        return (Character)getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", id);
    }
    
    public static void appendOrderResult(Character c, String result) {
        c.setOrderResults((c.getOrderResults().equals("") ? "" : c.getOrderResults() + " ") + result);
    }
}
