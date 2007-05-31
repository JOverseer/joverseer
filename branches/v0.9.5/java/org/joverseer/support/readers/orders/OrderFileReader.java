package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.domain.*;
import org.joverseer.domain.Character;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.MessageDialog;


public class OrderFileReader {

    String orderFile;

    Game game;

    int ordersRead = 0;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getOrderFile() {
        return orderFile;
    }

    public void setOrderFile(String orderFile) {
        this.orderFile = orderFile;
    }

    public boolean checkGame() throws Exception {
        BufferedReader reader = null;
        try {
            Resource resource = Application.instance().getApplicationContext().getResource(getOrderFile());
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line;
            line = reader.readLine();
            if (line == null)
                return false;
            line = reader.readLine();
            if (line == null)
                return false;
            // check game no
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0]) == game.getMetadata().getGameNo()) {
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

    public void readOrders() throws Exception {
        BufferedReader reader = null;
        try {
            Resource resource = Application.instance().getApplicationContext().getResource(getOrderFile());
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line;
            String charName = null;
            int orderI = 0;
            int i = 0;
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
                    if (!parts[0].equals(charName)) {
                        orderI = 0;
                    } else {
                        orderI++;
                    }
                    charName = parts[0];
                    String parameters = "";
                    int orderNo = -1;
                    if (!parts[1].equals("")) {
                        orderNo = Integer.parseInt(parts[1]);

                        for (int j = 2; j < parts.length; j++) {
                            String part = parts[j];
                            if (!part.equals("--")) {
                                parameters = parameters + (parameters.equals("") ? "" : Order.DELIM) + part;
                            }
                        }
                    }
                    Character c = (Character) getGame().getTurn().getContainer(TurnElementsEnum.Character)
                            .findFirstByProperty("id", charName);
                    if (c != null) {
                        Order[] orders = c.getOrders();
                        orders[orderI].setOrderNo(orderNo);
                        orders[orderI].setParameters(parameters);
                        ordersRead++;
                    }
                }
                i++;
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


    public int getOrdersRead() {
        return ordersRead;
    }


    public void setOrdersRead(int charsRead) {
        this.ordersRead = ordersRead;
    }

}