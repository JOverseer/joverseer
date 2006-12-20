
package org.joverseer.metadata;


public enum GameTypeEnum {
    game1650("1650"),
    game2950("2950");

    private final String myName; // for debug only

    private GameTypeEnum(String name) {
        myName = name;
    }

    public String toString() {
        return myName;
    }

}
