package org.joverseer.orders.export;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.apache.commons.beanutils.BeanComparator;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationDescriptor;


public class OrderFileGenerator {
    Turn turn;
    
    public String generateOrderFile(Game g, Turn t, int nationNo) throws Exception {
        this.turn = t;
        String ret = "";
        ArrayList<Character> chars = (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo);
        Collections.sort(chars, new BeanComparator("id"));
        ArrayList<Character> toRemove = new ArrayList<Character>();
        for (Character ch : chars) {
            if (ch.getHealth() == null || ch.getHealth() == 0 || ch.getHexNo() <= 0) {
                toRemove.add(ch);
            }
        }
        chars.removeAll(toRemove);
        

        String header = getFileHeader(g, t, nationNo, chars);
        ret += header;
        for (Character c : chars) {
            for (int i=0; i<c.getNumberOfOrders(); i++) {
                Order o = c.getOrders()[i];
                ret += exportOrder(c, o);
                ret += "\n";
            }
            ret += "\n";
        }
        ret += getFileFooter();
        return ret;
    }
    
    protected String getFileHeader(Game g, Turn t, int nationNo, ArrayList<Character> chars) throws Exception {
        // check for existence of 285 order
        boolean order285exists = false;
        int orderCount = 0;
        for (Character ch : chars) {
            for (int i=0; i<ch.getNumberOfOrders(); i++) {
                if (ch.getOrders()[i].getOrderNo() == 285) {
                    order285exists = true;
                }
                orderCount++;
            }
        }
        
        String ret = "BEGINMEAUTOINPUT\n";
        PlayerInfo pi = (PlayerInfo)t.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
        if (pi == null) {
            throw new Exception("Player Info not found.");
        }
        String[] dueDateParts = pi.getDueDate().split(" ");
        ret += String.format("%s,%s,%s,%s,%s,%s%s%s,%s,%s",
                g.getMetadata().getGameNo(),
                pi.getNationNo(),
                pi.getAccountNo(),
                pi.getSecret(),
                pi.getPlayerName(),
                dueDateParts[1], dueDateParts[0], (dueDateParts.length == 2 ? (Calendar.getInstance().get(Calendar.YEAR)) : dueDateParts[2]),
                order285exists ? "YES" : "NO",
                orderCount);
        ret += "\n\n";
        return ret;
    }
    
    protected String getFileFooter() {
    	ApplicationDescriptor appDesc = (ApplicationDescriptor)Application.instance().getApplicationContext().getBean("applicationDescriptor");
        return "\n" +
                "ENDMEAUTOINPUT" + "\n" +
                "JO" + appDesc.getVersion();
    }
    
    protected String exportOrder(Character c, Order o) {
        String ret = c.getId() + "     ".substring(0, 5 - c.getId().length());
        ret += ",";
        ret += o.getOrderNo();
        for (int i=0; i<15; i++) {
            String p = o.getParameter(i);
            if (o.getOrderNo() == 830 || o.getOrderNo() == 850 || o.getOrderNo() == 860) {
                // special handling for move army orders
                if (i == 0) {
                    p = o.getParameter(o.getLastParamIndex());
                } else if (i <= o.getLastParamIndex()) {
                    p = o.getParameter(i-1);
                } else {
                    p = null;
                }
            }
            if (p == null || p.equals("") || p.equals("-")) {
                p = "--";
            }
            ret += "," + p;
        }
        return ret;
    }
}
