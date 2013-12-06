package org.joverseer.support.readers.pdf;

/**
 * Holds information about product amounts (for all products)
 * 
 * @author Marios Skounakis
 */
public class ProductAmountWrapper {
	String leather;
	String bronze;
	String steel;
	String mithril;
	String food;
	String timber;
	String gold;
	String mounts;
	
	public String getBronze() {
		return this.bronze;
	}
	public void setBronze(String bronze) {
		this.bronze = bronze;
	}
	public String getFood() {
		return this.food;
	}
	public void setFood(String food) {
		this.food = food;
	}
	public String getGold() {
		return this.gold;
	}
	public void setGold(String gold) {
		this.gold = gold;
	}
	public String getLeather() {
		return this.leather;
	}
	public void setLeather(String leather) {
		this.leather = leather;
	}
	public String getMithril() {
		return this.mithril;
	}
	public void setMithril(String mithril) {
		this.mithril = mithril;
	}
	public String getMounts() {
		return this.mounts;
	}
	public void setMounts(String mounts) {
		this.mounts = mounts;
	}
	public String getSteel() {
		return this.steel;
	}
	public void setSteel(String steel) {
		this.steel = steel;
	}
	public String getTimber() {
		return this.timber;
	}
	public void setTimber(String timber) {
		this.timber = timber;
	}
	
	
}
