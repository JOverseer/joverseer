package org.joverseer.orders.export;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;


public class OrderTextGenerator extends OrderFileGenerator {

    private String getStatsLine(Character c) {
        String ret = "";
        ret = appendStat(ret, "C", c.getCommandTotal());
        ret = appendStat(ret, "A", c.getAgentTotal());
        ret = appendStat(ret, "E", c.getEmmisaryTotal());
        ret = appendStat(ret, "M", c.getMageTotal());
        return ret;
    }
    
    private String appendStat(String stats, String statLetter, int rank) {
        if (rank != 0) {
            return stats + (stats.equals("") ? "" : ", ") + statLetter + rank;
        }
        return stats;
    }
    
    protected String exportOrder(Character c, Order o) {
        String ret = "";
        if (o == c.getOrders()[0]) {
            ret += String.format("%s (%s) @ %04d (%s)\n", c.getName(), (c.getId() + "    ").substring(0, 5), c.getHexNo(), getStatsLine(c));
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
            ArrayList<Note> notes = (ArrayList<Note>)turn.getContainer(TurnElementsEnum.Notes).findAllByProperty("target", c);
            for (Note n : notes) {
                if (n.getTags() != null && n.getTags().indexOf("Order") > -1) {
                    ret += "\n" + n.getText();
                }
            }
        }
        return ret;
    }

    protected String getFileFooter() {
        return "";
    }

    protected String getFileHeader(Game g, Turn t, int nationNo, ArrayList<Character> chars) throws Exception {
        return String.format("Orders for Game %s, Turn %s, Nation %s\n\n", g.getMetadata().getGameNo(), t.getTurnNo(), nationNo);
    }
    

}
