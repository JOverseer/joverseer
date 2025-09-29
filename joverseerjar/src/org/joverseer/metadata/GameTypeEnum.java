
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
    gameCME("CME"),
    gameCMF("CMF"),
	gameOOE("OOE");

    private final String myName;

    private GameTypeEnum(String name) {
        this.myName = name;
    }

    @Override
	public String toString() {
        return this.myName;
    }
    public String toMEString() {
    	if (this.myName == gameKS.myName) {
    		return "KIN";
    	}
    	return this.myName;
    }
    public String toOrderCheckerName() {
		if (this.equals(GameTypeEnum.game1650)) {
			return "1650";
		}
		if (this.equals(GameTypeEnum.game2950)) {
			return "2950";
		}
		if (this.equals(GameTypeEnum.gameBOFA)) {
			return "BOFA";
		}
		if (this.equals(GameTypeEnum.gameFA)) {
			return "Fourth Age";
		}
		if (this.equals(GameTypeEnum.gameUW)) {
			return "Untold War";
		}
		if (this.equals(GameTypeEnum.gameKS)) {
			return "Kin Strife";
		} 
		if (this.equals(GameTypeEnum.gameCME)) {
			return "CME";
		}
		if (this.equals(GameTypeEnum.gameCMF)) {
			return "CMF";
		}
		if (this.equals(GameTypeEnum.gameOOE)) {
			return "Out of the East";
		}
		return "unknown";
    }
}
