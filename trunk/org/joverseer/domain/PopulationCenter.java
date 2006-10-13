package org.joverseer.domain;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.infoSources.InfoSource;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 8:02:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenter implements IBelongsToNation, Serializable {
    String name;
    int x;
    int y;

    PopulationCenterSizeEnum size;
    FortificationSizeEnum fortification;
    HarborSizeEnum harbor;

    int nationNo;
    Nation nation;

    boolean capital;
    boolean hidden;

    int loyalty;

    InformationSourceEnum informationSource;

    InfoSource infoSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PopulationCenterSizeEnum getSize() {
        return size;
    }

    public void setSize(PopulationCenterSizeEnum size) {
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public FortificationSizeEnum getFortification() {
        return fortification;
    }

    public void setFortification(FortificationSizeEnum fortification) {
        this.fortification = fortification;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public HarborSizeEnum getHarbor() {
        return harbor;
    }

    public void setHarbor(HarborSizeEnum harbor) {
        this.harbor = harbor;
    }

    public boolean getCapital() {
        return capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }

    public InformationSourceEnum getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(InformationSourceEnum informationSource) {
        this.informationSource = informationSource;
    }

    public InfoSource getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public PopulationCenter clone() {
        PopulationCenter newPc = new PopulationCenter();
        newPc.setName(getName());
        newPc.setCapital(getCapital());
        newPc.setFortification(getFortification());
        newPc.setHarbor(getHarbor());
        newPc.setHidden(getHidden());
        newPc.setNationNo(getNationNo());
        newPc.setLoyalty(getLoyalty());
        newPc.setSize(getSize());
        newPc.setX(getX());
        newPc.setY(getY());

        newPc.setInfoSource(getInfoSource());
        newPc.setInformationSource(getInformationSource());

        return newPc;
    }
}
