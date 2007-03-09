package org.joverseer.orders.export;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.beanutils.BeanComparator;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;


public class OrderFileGenerator {
    public String generateOrderFile(Game g, Turn t, int nationNo) throws Exception {
        String ret = "";
        String header = getFileHeader(g, t, nationNo);

        ret += header;
        ArrayList<Character> chars = (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo);
        Collections.sort(chars, new BeanComparator("id"));
        for (Character c : chars) {
            for (int i=0; i<2; i++) {
                Order o = c.getOrders()[i];
                ret += exportOrder(c, o);
                ret += "\n";
            }
            ret += "\n";
        }
        ret += getFileFooter();
        return ret;
    }
    
    protected String getFileHeader(Game g, Turn t, int nationNo) throws Exception {
        String ret = "BEGINMEAUTOINPUT\n";
        PlayerInfo pi = (PlayerInfo)t.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
        if (pi == null) {
            throw new Exception("Player Info not found.");
        }
        ret += String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                g.getMetadata().getGameNo(),
                g.getMetadata().getNationNo(),
                pi.getAccountNo(),
                pi.getSecret(),
                pi.getPlayerName(),
                pi.getDueDate(),
                "NO",
                "24");
        ret += "\n\n";
        return ret;
    }
    
    protected String getFileFooter() {
        return "\n" +
                "ENDMEAUTOINPUT" + "\n" +
                "AM03e.3";
    }
    
    protected String exportOrder(Character c, Order o) {
        String ret = c.getId();
        ret += ",";
        ret += o.getOrderNo();
        for (int i=0; i<16; i++) {
            String p = o.getParameter(i);
            if (p == null || p.equals("")) {
                p = "--";
            }
            ret += "," + p;
        }
        return ret;
    }
}
