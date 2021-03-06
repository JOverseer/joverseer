package org.joverseer.tools.nationMessages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

/**
 * Utility class that parses nation messages and extracts a hex number, if possible.
 * The hex number is derived by the pop center or the character as found in the rumor.
 *
 * @author Marios Skounakis
 */
public class NationMessageParser {
    String name = "\\p{L}+(?:[\\s\\-]\\p{L}+)*";
    int turnNo;
    Turn previousTurn;

    NationMessagePattern[] patterns = new NationMessagePattern[]{
            new NationMessagePattern(String.format("(%s) is no longer under our control.$", this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format(".*transported from the %s to (%s)\\.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format("^There are rumors of a theft attempt involving %s at (%s)\\.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format("^There are rumors of a kidnap attempt involving (%s) and %s.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_CHAR),
            new NationMessagePattern(String.format("^There are rumors of a kidnap attempt involving %s and (%s).$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_CHAR),
            new NationMessagePattern(String.format("^There are rumors of an assassination attempt involving (%s) and %s.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_CHAR),
            new NationMessagePattern(String.format("^There are rumors of an assassination attempt involving %s and (%s).$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_CHAR),
            new NationMessagePattern(String.format(".*Gold was stolen at (%s)\\.$", this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format("^The loyalty was influenced from the efforts or presence of %s at (%s)\\.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format("^There are rumors of a sabotage attempt involving %s at (%s)\\.$", this.name, this.name), NationMessagePattern.NAMED_ELEMENT_PC),
            new NationMessagePattern(String.format("^The loyalty was influenced/reduced at (%s)\\.$", this.name), NationMessagePattern.NAMED_ELEMENT_PC),
    };



    public NationMessageParser(int turnNo,GameHolder gameHolder) {
        super();
        this.turnNo = turnNo;
        Game g = GameHolder.instance().getGame();
        if (Game.isInitialized(g)) {
        	this.previousTurn = g.getTurn(NationMessageParser.this.turnNo - 1);
        }
    }

    public int getHexNo(String message) {
        for (NationMessagePattern p : this.patterns) {
            int hexNo = p.getHexNo(message);
            if (hexNo > -1) return hexNo;
        }
        return -1;
    }

    class NationMessagePattern {
        static final String NAMED_ELEMENT_PC = "POPCENTER";
        static final String NAMED_ELEMENT_CHAR = "CHAR";

        String pattern;
        String namedElementType;
        String elementName;


        public NationMessagePattern(String pattern, String namedElementType) {
            super();
            this.pattern = pattern;
            this.namedElementType = namedElementType;
        }


        public String getElementName() {
            return this.elementName;
        }


        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        public String getNamedElementType() {
            return this.namedElementType;
        }

        public void setNamedElementType(String namedElementType) {
            this.namedElementType = namedElementType;
        }

        public String getPattern() {
            return this.pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public int getHexNo(String message) {
            Pattern pt = Pattern.compile(getPattern());
            Matcher m = pt.matcher(message);
            if (m.matches()) {
                setElementName(m.group(1));
                if (NationMessageParser.this.previousTurn == null) return -1;
                if (getNamedElementType().equals(NAMED_ELEMENT_PC)) {
                    PopulationCenter pc = (PopulationCenter)NationMessageParser.this.previousTurn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", getElementName());
                    if (pc != null) {
                        return pc.getHexNo();
                    }
                } else if (getNamedElementType().equals(NAMED_ELEMENT_CHAR)) {
                    Character c = (Character)NationMessageParser.this.previousTurn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", getElementName());
                    if (c != null) {
                        return c.getHexNo();
                    }
                }
            }
            return -1;

        }

    }
}
