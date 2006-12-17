package org.joverseer.metadata.domain;

import java.io.Serializable;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 17 Δεκ 2006
 * Time: 6:40:20 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationMapRange implements Serializable {
    int nationNo;
    int tlX;
    int tlY;
    int brX;
    int brY;

    public int getBrX() {
        return brX;
    }

    public void setBrX(int brX) {
        this.brX = brX;
    }

    public int getBrY() {
        return brY;
    }

    public void setBrY(int brY) {
        this.brY = brY;
    }

    public int getTlX() {
        return tlX;
    }

    public void setTlX(int tlX) {
        this.tlX = tlX;
    }

    public int getTlY() {
        return tlY;
    }

    public void setTlY(int tlY) {
        this.tlY = tlY;
    }

    public Rectangle getRectangle() {
        return new Rectangle(getTlX(), getTlY(), getBrX() - getTlX(), getBrY() - getTlY());
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }
}

