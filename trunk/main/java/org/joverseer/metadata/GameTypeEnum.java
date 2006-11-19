/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 10:00:42 PM
 * To change this template use File | Settings | File Templates.
 */
package org.joverseer.metadata;

import java.io.Serializable;

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
