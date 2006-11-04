package org.joverseer.metadata.domain;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 24 Οκτ 2006
 * Time: 11:33:16 μμ
 * To change this template use File | Settings | File Templates.
 */
public class Artifact implements Serializable {
    String name;
    int no;
    ArrayList powers = new ArrayList();
    String alignment;
    String owner;

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList getPowers() {
        return powers;
    }

    public void setPowers(ArrayList powers) {
        this.powers = powers;
    }

    public String getPower1() {
        if (powers.size() == 0) {
            return "";
        }
        return powers.get(0).toString();
    }

    public String getPower2() {
        if (powers.size() < 2) {
            return "";
        }
        return powers.get(1).toString(); 
    }
}
