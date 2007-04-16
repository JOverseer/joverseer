package org.joverseer.orders.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
    public String generateOrderFile(Game g, Turn t, int nationNo) throws Exception {
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
    
    protected String getFileHeader(Game g, Turn t, int nationNo, ArrayList<Character> chars) throws Exception {
        // check for existence of 285 order
        boolean order285exists = false;
        for (Character ch : chars) {
            for (int i=0; i<2; i++) {
                if (ch.getOrders()[i].getOrderNo() == 285) {
                    order285exists = true;
                }
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
                dueDateParts[1], dueDateParts[0], dueDateParts[2],
                order285exists ? "YES" : "NO",
                chars.size() * 2);
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
            if (p == null || p.equals("") || p.equals("-")) {
                p = "--";
            }
            ret += "," + p;
        }
        return ret;
    }
}
