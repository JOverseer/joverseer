package org.joverseer.orders.export;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;


public class OrderTextGenerator extends OrderFileGenerator {

    
    @Override
	protected String exportOrder(Character c, Order o) {
        String ret = "";
        if (o == c.getOrders()[0]) {
            ret += String.format("%s (%s) @ %04d (%s)\n", c.getName(), (c.getId() + "    ").substring(0, 5), c.getHexNo(), c.getBasicStatString());
        }

        if (o.isBlank()) {
            ret += "--";
        } else {
            String parameters = "";
            for (int i=0; i<16; i++) {
                if (o.getParameter(i) == null || o.getParameter(i).equals("--") || o.getParameter(i).equals("-")) continue;
                parameters += (parameters.equals("") ? "" : "  ") + o.getParameter(i);
            }
            ret += (o.getNoAndCode().replace(" ", "  ") + "  " + parameters).trim(); 
        }
        if (o == c.getOrders()[1]) {
            // export comments
            ArrayList<Note> notes = (ArrayList<Note>)this.turn.getContainer(TurnElementsEnum.Notes).findAllByProperty("target", c);
            for (Note n : notes) {
                if (n.getTags() != null && n.getTags().indexOf("Order") > -1) {
                    ret += "\n" + n.getText();
                }
            }
        }
        return ret;
    }

    @Override
	protected String getFileFooter() {
        return "";
    }

    @Override
	protected String getFileHeader(Game g, Turn t, int nationNo, ArrayList<Character> chars) throws Exception {
        return String.format("Orders for Game %s, Turn %s, Nation %s\n\n", g.getMetadata().getGameNo(), t.getTurnNo(), nationNo);
    }
    

}
