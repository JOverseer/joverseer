package org.joverseer.ui.domain;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;

/**
 * Wraps information about Enemy Character Rumors.
 * Also contains a static method for generating the character rumors for the game.
 * 
 * A character rumor contains:
 * - the character name
 * - the hex number, if applicable
 * - the turn number of the latest rumor
 * - a string of a list of all rumors from all turns
 * - whether the character is a starting char or not
 * - the character type (agent or emmisary)
 * 
 * @author Marios Skounakis
 */
public class EnemyCharacterRumorWrapper implements IHasMapLocation {

    String name;
    int hexNo;
    int turnNo;
    String reportedTurns = "";
    boolean startChar;
    String charType;

    public int getHexNo() {
        return hexNo;
    }

    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportedTurns() {
        return reportedTurns;
    }

    public void setReportedTurns(String reportedTurns) {
        this.reportedTurns = reportedTurns;
    }

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public int getX() {
        return hexNo / 100;
    }

    public int getY() {
        return hexNo % 100;
    }

    public void addReport(String rep) {
        reportedTurns += (reportedTurns.equals("") ? "" : ", ") + rep;
    }


    public boolean getStartChar() {
        return startChar;
    }


    public void setStartChar(boolean startChar) {
        this.startChar = startChar;
    }


    public String getCharType() {
        return charType;
    }

    public void setCharType(String charType) {
        this.charType = charType;
    }

    public static Container getAgentWrappers() {
        Container thieves = new Container(new String[] {"name"});
        Game g = GameHolder.instance().getGame();
        if (Game.isInitialized(g)) {
            // rumor type - short description
            String[] types = new String[] {"theft", "theft", "assas.", "kidnap", "emiss.", "sabot."};
            // char type, according to rumor
            String[] charTypes = new String[] {"agent", "agent", "agent", "agent", "emmisary", "agent",};
            // the patterns for identifying the rumors
            String[] prefixes = new String[] {"^There are rumors of a theft attempt involving (.+) at .+",
                    "^There are rumors of a theft attempt involving (.+)\\.$",
                    "^There are rumors of an assassination attempt involving (.+) and .+",
                    "^There are rumors of a kidnap attempt involving (.+) and .+",
                    "^The loyalty was influenced from the efforts or presence of (.+) at .+",
                    "^There are rumors of a sabotage attempt involving (.+) at .+"};
            for (int i = 0; i <= g.getMaxTurn(); i++) {
                Turn t = g.getTurn(i);

                if (t == null)
                    continue;

                NationRelations gameNr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation)
                        .findFirstByProperty("nationNo", g.getMetadata().getNationNo());

                for (NationMessage nm : (ArrayList<NationMessage>) t.getContainer(TurnElementsEnum.NationMessage)
                        .getItems()) {
                    String charName = null;
                    String repType = null;
                    String charType = null;
                    // for all patterns
                    for (int j = 0; j < prefixes.length; j++) {
                        String prefix = prefixes[j];
                        Matcher m = Pattern.compile(prefix).matcher(nm.getMessage());
                        if (m.matches()) {
                            // if pattern matches nation message
                            // get the info for the match (name, report type, char type)
                            charName = m.group(1).trim();
                            repType = types[j];
                            charType = charTypes[j];
                            // stop matching patterns
                            break;
                        }
                    }
                    if (charName != null) {
                        // if a match was found create new rumor
                        EnemyCharacterRumorWrapper thief = (EnemyCharacterRumorWrapper) thieves.findFirstByProperty(
                                "name", charName);
                        Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty(
                                "name", charName);
                        if (c != null) {
                            if (c.getNationNo() > 0) {
                                NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation)
                                        .findFirstByProperty("nationNo", c.getNationNo());
                                if (nr.getAllegiance() == gameNr.getAllegiance()) {
                                    // if you can deduce that the char is of friendly allegiance,
                                    // do not show him
                                    continue;
                                }
                            }
                        }
                        if (thief == null) {
                            // if new character, create rumor wrapper
                            thief = new EnemyCharacterRumorWrapper();
                            boolean startChar = g.getMetadata().getCharacters().findFirstByProperty("id",
                                    Character.getIdFromName(charName)) != null;
                            thief.setName(charName);
                            thief.setTurnNo(t.getTurnNo());
                            thief.addReport(repType + " " + t.getTurnNo());
                            thief.setStartChar(startChar);
                            thief.setCharType(charType);
                            thieves.addItem(thief);
                        } else {
                            // if rumor wrapper already exists, add the report for this turn 
                            thief.addReport(repType + " " + t.getTurnNo());
                        }
                    }
                }
            }

        }
        return thieves;
    }

}
