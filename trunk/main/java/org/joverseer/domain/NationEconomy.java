package org.joverseer.domain;

import java.io.Serializable;


public class NationEconomy implements IBelongsToNation, Serializable {
    int nationNo;

    int armyMaintenance;
    int popMaintenance;
    int charMaintenance;
    int totalMaintenance;
    int taxRate;
    int revenue;
    int surplus;
    int reserve;
    int taxBase;

    public int getArmyMaintenance() {
        return armyMaintenance;
    }

    public void setArmyMaintenance(int armyMaintenance) {
        this.armyMaintenance = armyMaintenance;
    }

    public int getCharMaintenance() {
        return charMaintenance;
    }

    public void setCharMaintenance(int charMaintenance) {
        this.charMaintenance = charMaintenance;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public int getPopMaintenance() {
        return popMaintenance;
    }

    public void setPopMaintenance(int popMaintenance) {
        this.popMaintenance = popMaintenance;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getTaxBase() {
        return taxBase;
    }

    public void setTaxBase(int taxBase) {
        this.taxBase = taxBase;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public int getTotalMaintenance() {
        return totalMaintenance;
    }

    public void setTotalMaintenance(int totalMaintenance) {
        this.totalMaintenance = totalMaintenance;
    }
}
