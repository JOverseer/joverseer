package org.joverseer.ui.domain;

import org.joverseer.metadata.GameTypeEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 19 Íןו 2006
 * Time: 10:39:55 לל
 * To change this template use File | Settings | File Templates.
 */
public class NewGame {
    GameTypeEnum gameType;
    int number;
    int nationNo;

    public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
