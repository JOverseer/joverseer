package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;


public class OrderTextReader {
        String orderText;
        
        Game game;
        
        int chars = 0;

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
        
        public void readOrders() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getOrderText().getBytes())));
                String line;
                String charId = null;
                String location = null;
                int i = 0;
                String[] orderText = new String[]{null, null};
    
                String charPattern = "^[\\p{L}\\?]+([\\-\\s'][\\p{L}\\?])* \\([\\w ]{5}\\) @ \\d{4}.*";
                String orderPattern = "^\\d{3}  \\w{7}.*";
                
                Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                while ((line = reader.readLine()) != null) {
                    if (chP.matcher(line).matches()) {
                        if (charId != null) {
                            addOrders(charId, location, orderText);
                        }
                        int j1 = line.indexOf("(");
                        int j2 = line.indexOf(")");
                        int j3 = line.indexOf("@ ");
                        if (j1 > -1 && j2 > -1 && j3 > -1) {
                            charId = line.substring(j1 + 1, j2);
                            location = line.substring(j3 + 2, j3 + 6);
                            i = 0;
                        }
                    }
                    if (Pattern.matches(orderPattern, line)) {
                        if (charId != null && i < 2) {
                            orderText[i] = line;
                            i++;
                        }
                    }
                }
                if (charId != null) {
                    addOrders(charId, location, orderText);
                }
            }
            catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        }
        
        private void addOrders(String charId, String location, String[] orderText) {
            Character c = (Character)getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charId);
            if (c == null) return;
            if (c.getHexNo() != Integer.parseInt(location)) return;
            chars++;
            Order[] orders = c.getOrders();
            for (int i=0; i<2; i++) {
                if (orderText[i] == null) {
                    orders[i].clear();
                } else {
                    String[] parts = orderText[i].replace("  ", " ").split(" ");
                    String parameters = "";
                    int orderNo = -1;
                    if (!parts[0].equals("")) {
                        orderNo = Integer.parseInt(parts[0]);
                        for (int j=2; j<parts.length; j++) {
                            String part = parts[j];
                            if (!part.equals("--") && !part.equals("")) {
                                    parameters = parameters + (parameters.equals("") ? "" : " ") + part; 
                            }
                        }
                    }
                    orders[i].setOrderNo(orderNo);
                    orders[i].setParameters(parameters);
                }
            }
        }

        
        public int getChars() {
            return chars;
        }

        
        public void setChars(int chars) {
            this.chars = chars;
        }
}
