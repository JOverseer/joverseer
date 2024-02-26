package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.StringUtils;

/**
 * Reads orders from an order file.
 * 
 * uses Character container in game, and game number
 * @author Marios Skounakis
 */
public class OrderFileReader implements OrderTextReaderInterface {

    final Game game;

    int ordersRead = 0;

    public OrderFileReader(Game game) {
    	this.game = game;
    }
    public boolean checkGame(BufferedReader reader) throws Exception {
        try {
            String line;
            line = reader.readLine();
            if (line == null)
                return false;
            line = reader.readLine();
            if (line == null)
                return false;
            // check game no
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0]) == this.game.getMetadata().getGameNo()) {
                return true;
            }
            return false;
        } catch (Exception exc) {
            throw exc;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    @Override
	public void readOrders(BufferedReader reader) throws Exception {
        try {
            String line;
            String charName = null;
            int orderI = 0;
            Pattern turnFileInfo = Pattern.compile("(\\d+),(\\d+),(\\d+),(\\d+),([\\w ]+),.*");

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ENDMEAUTOINPUT"))
                    break;
                if (line.startsWith("BEGINMEAUTOINPUT")) {
                    continue;
                }
                ;
                Matcher m = turnFileInfo.matcher(line);
                if (m.matches()) {
                    int gameNo = Integer.parseInt(m.group(1));
                    if (gameNo != GameHolder.instance().getGame().getMetadata().getGameNo()) {
                        throw new Exception("Invalid game number. Possibly the order file is from a different game.");
                    }
                    continue;
                }
                if (!line.equals("")) {
                    String[] parts = line.split(",");
                    if (!parts[0].trim().equals(charName)) {
                        orderI = 0;
                    } else {
                        orderI++;
                    }
                    charName = parts[0].trim();
                    String parameters = "";
                    int orderNo = -1;
                    if (!parts[1].equals("")) {
                        orderNo = Integer.parseInt(parts[1]);

                        for (int j = 2; j < parts.length; j++) {
                            String part = parts[j];
                            StringUtils.trimLeading(part);  // don't trim trailing wsp...the char may be <5 chars.
                            if (!part.replace(" ", "").equals("--")) {
                                parameters = parameters + (parameters.equals("") ? "" : Order.DELIM) + part;
                            }
                        }
                    }
                    Character c = (Character) this.game.getTurn().getContainer(TurnElementsEnum.Character)
                            .findFirstByProperty("id", charName);
                    if ((c == null) && (charName.length() < 5)) {
                    	while (charName.length() < 5) {
                    		charName = charName + " ";
                    	}
                    	c = (Character) this.game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charName);
                    }
                    if (c == null && charName.endsWith(" ")) {
                    	c = (Character) this.game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", charName.substring(0, 3));
                    }
                    if (c != null) {
                        Order[] orders = c.getOrders();
                        orders[orderI].setOrderNo(orderNo);
                        orders[orderI].setParameters(parameters);
                        if (orderNo == 830 || orderNo == 850 || orderNo == 860) {
                            //String paramTemp = orders[i].getParameter(orders[i].getLastParamIndex());
                            String paramZero = new String(orders[orderI].getParameter(0)).trim();
                            if (paramZero.equals("no") || paramZero.equals("ev")) {
                            	String params = orders[orderI].getParameters();
                            	params = params.substring(3) + "#" + paramZero;
                                orders[orderI].setParameters(params);
                            }
                        }
                        this.ordersRead++;
                    }
                }
            }
        } catch (Exception exc) {
            throw exc;
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    @Override
	public int getOrdersRead() {
        return this.ordersRead;
    }


    public void setOrdersRead(int ordersRead) {
        this.ordersRead = ordersRead;
    }

}
