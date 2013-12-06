package org.joverseer.support.readers.xml;

import java.util.ArrayList;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;

/**
 * Holds information about a nation economy (from xml turns)
 * 
 * @author Marios Skounakis
 */
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

	ArrayList<ProductWrapper> products = new ArrayList<ProductWrapper>();

	public ArrayList<ProductWrapper> getProducts() {
		return this.products;
	}

	public void setProducts(ArrayList<ProductWrapper> products) {
		this.products = products;
	}

	public int getArmyMaint() {
		return this.armyMaint;
	}

	public void setArmyMaint(int armyMaint) {
		this.armyMaint = armyMaint;
	}

	public int getCharMaint() {
		return this.charMaint;
	}

	public void setCharMaint(int charMaint) {
		this.charMaint = charMaint;
	}

	public int getPopMaint() {
		return this.popMaint;
	}

	public void setPopMaint(int popMaint) {
		this.popMaint = popMaint;
	}

	public int getReserve() {
		return this.reserve;
	}

	public void setReserve(int reserve) {
		this.reserve = reserve;
	}

	public int getRevenue() {
		return this.revenue;
	}

	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}

	public int getSurplus() {
		return this.surplus;
	}

	public void setSurplus(int surplus) {
		this.surplus = surplus;
	}

	public int getTaxBase() {
		return this.taxBase;
	}

	public void setTaxBase(int taxBase) {
		this.taxBase = taxBase;
	}

	public int getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}

	public int getTotalMaint() {
		return this.totalMaint;
	}

	public void setTotalMaint(int totalMaint) {
		this.totalMaint = totalMaint;
	}

	public void addProduct(ProductWrapper product) {
		this.products.add(product);
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

		for (ProductWrapper pw : this.products) {
			pw.updateNationEconomy(ne);
		}
		return ne;
	}

	public void updateProductPrices(Turn t) {
		Container<ProductPrice> prices = t.getProductPrices();
		for (ProductWrapper pw : this.products) {
			pw.updateProductPrice(prices);
		}
	}
}
