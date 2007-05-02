package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;


public class OrderTextReader {
        String orderText;
        
        Game game;
        
        ArrayList<String> lineResults = new ArrayList<String>();
        
        int chars = 0;
        int orders = 0;

        public Game getGame() {
                return game;
        }

        public void setGame(Game game) {
                this.game = game;
        }

        public String getOrderText() {
                return orderText;
        }

        public void setOrderText(String orderText) {
                this.orderText = orderText;
        }
        
        public void readOrders(int pass) {
            try {
                if (pass == 0) {
                    lineResults.clear();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getOrderText().getBytes())));
                String line;
                String charId = null;
                String location = null;
                int i = 0;
                int charLine = 0;
                String[] orderText = new String[]{null, null};
                int[] orderLines = new int[]{0, 0};
    
                String charPattern = "^[\\p{L}\\?]+([\\-\\s'][\\p{L}\\?]+)*\\s+\\([\\w ]{5}\\) @ \\d{4}.*";
                String orderPattern = "^\\d{3}\\s{1,2}\\w{5,7}.*";
                
                Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                int lineCounter = 0;
                while ((line = reader.readLine()) != null) {
                    if (chP.matcher(line).matches()) {
                        if (charId != null) {
                            addOrders(charId, location, charLine, orderText, orderLines, pass);
                        }
                        int j1 = line.indexOf("(");
                        int j2 = line.indexOf(")");
                        int j3 = line.indexOf("@ ");
                        if (j1 > -1 && j2 > -1 && j3 > -1) {
                            charId = line.substring(j1 + 1, j2);
                            location = line.substring(j3 + 2, j3 + 6);
                            charLine = lineCounter;
                            i = 0;
                            lineResults.add("Character line (char id: " + charId + ").");
                        } else {
                            lineResults.add("Line ignored. Looks like character line but parsing failed.");
                        }
                    } else if (Pattern.matches(orderPattern, line)) {
                        if (charId != null && i < 2) {
                            orderLines[i] = lineCounter;
                            orderText[i] = line;
                            lineResults.add("Order line.");
                            i++;
                        } else {
                            lineResults.add("Order line ignored.");
                        }
                    } else {
                        lineResults.add("Line ignored.");
                    }
                    lineCounter++;
                }
                if (charId != null) {
                    addOrders(charId, location, charLine, orderText, orderLines, pass);
                }
            }
            catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        }
        
        
        
        private void addOrders(String charId, String location, int charLine, String[] orderText, int[] orderLines, int pass) {
            Character c = (Character)getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charId);
            if (c == null) {
                c = (Character)getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charId.trim());
            }
            if (c == null) {
                String lineRes = lineResults.get(charLine);
                lineRes += " " + "Character was not found in game.";
                lineResults.set(charLine, lineRes.trim());
                return;
            }
            if (c.getHexNo() != Integer.parseInt(location)) {
                String lineRes = lineResults.get(charLine);
                lineRes += " " + "Character was found but at a different location - ignoring.";
                lineResults.set(charLine, lineRes.trim());
                return;
            }
            chars++;
            Order[] orders = c.getOrders();
            for (int i=0; i<2; i++) {
                if (orderText[i] == null) {
                    orders[i].clear();
                } else {
                    this.orders++;
                    String repString = "  ";
                    if (orderText[i].trim().indexOf(repString) == -1) {
                        repString = " ";
                    }
                    String[] parts = orderText[i].trim().replace(repString, Order.DELIM).split(Order.DELIM);
                    String parameters = "";
                    int orderNo = -1;
                    if (!parts[0].equals("")) {
                        orderNo = Integer.parseInt(parts[0]);
                        for (int j=2; j<parts.length; j++) {
                            String part = parts[j].trim();
                            if (!part.equals("--") && !part.equals("")) {
                                    parameters = parameters + (parameters.equals("") ? "" : Order.DELIM) + part; 
                            }
                        }
                    }
                    String lineRes = lineResults.get(orderLines[i]);
                    lineRes += " Parsed order: " + orderNo + ". Order will be added to " + c.getName() + ".";
                    lineResults.set(orderLines[i], lineRes.trim());
                    if (pass == 1) {
                        orders[i].setOrderNo(orderNo);
                        orders[i].setParameters(parameters);
                        // check mov army orders and swap params if applicable
                        if (orderNo == 830 || orderNo == 850 || orderNo == 860) {
                            String paramTemp = orders[i].getParameter(orders[i].getLastParamIndex());
                            for (int ii=orders[i].getLastParamIndex(); ii>0; ii--) {
                                orders[i].setParameter(ii, orders[i].getParameter(ii-1));
                            }
                            orders[i].setParameter(0, paramTemp);
                        }
                    }
                }
            }
        }

        
        public int getChars() {
            return chars;
        }

        
        public void setChars(int chars) {
            this.chars = chars;
        }

        
        public ArrayList<String> getLineResults() {
            return lineResults;
        }

        
        public int getOrders() {
            return orders;
        }

        
        public void setOrders(int orders) {
            this.orders = orders;
        }
        
        
}
