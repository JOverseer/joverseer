
package org.joverseer.metadata;

/**
 * Enumeration of game types
 * 
 * @author Marios Skounakis
 *
 */
public enum GameTypeEnum {
    game1650("1650"),
    game2950("2950"),
    gameBOFA("BOFA"),
    gameFA("1000"),
    gameUW("UW"),
    gameKS("KS"),
	gameCME("CME");

    private final String myName;

    private GameTypeEnum(String name) {
        this.myName = name;
    }

    @Override
	public String toString() {
        return this.myName;
    }

}
