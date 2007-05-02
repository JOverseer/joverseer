package org.joverseer.support.readers.xml;

import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;

import java.util.ArrayList;


public class EconomyWrapper {
    int armyMaint;
    int popMaint;
    int charMaint;
    int totalMaint;
    int taxRate;
    int revenue;
    int surplus;
    int reserve;
    int taxBase;

    ArrayList products = new ArrayList();

    public ArrayList getProducts() {
        return products;
    }

    public void setProducts(ArrayList products) {
        this.products = products;
    }

    public int getArmyMaint() {
        return armyMaint;
    }

    public void setArmyMaint(int armyMaint) {
        this.armyMaint = armyMaint;
    }

    public int getCharMaint() {
        return charMaint;
    }

    public void setCharMaint(int charMaint) {
        this.charMaint = charMaint;
    }

    public int getPopMaint() {
        return popMaint;
    }

    public void setPopMaint(int popMaint) {
        this.popMaint = popMaint;
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

    public int getTotalMaint() {
        return totalMaint;
    }

    public void setTotalMaint(int totalMaint) {
        this.totalMaint = totalMaint;
    }

    public void addProduct(ProductWrapper product) {
        products.add(product);
    }

    public NationEconomy getNationEconomy() {
        NationEconomy ne = new NationEconomy();
        ne.setArmyMaintenance(getArmyMaint());
        ne.setPopMaintenance(getPopMaint());
        ne.setCharMaintenance(getCharMaint());
        ne.setTotalMaintenance(getTotalMaint());
        ne.setTaxRate(getTaxRate());
        ne.setTaxBase(getTaxBase());
        ne.setRevenue(getRevenue());
        ne.setReserve(getReserve());
        ne.setSurplus(getSurplus());
        
        for (ProductWrapper pw : (ArrayList<ProductWrapper>)products) {
            pw.updateNationEconomy(ne);
        }
        return ne;
    }
    
    public void updateProductPrices(Turn t) {
        Container prices = t.getContainer(TurnElementsEnum.ProductPrice);
        for (ProductWrapper pw : (ArrayList<ProductWrapper>)products) {
            pw.updateProductPrice(prices);
        }
    }
}
