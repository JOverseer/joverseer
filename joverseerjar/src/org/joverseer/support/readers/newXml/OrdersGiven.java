package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

public class OrdersGiven {
	String characterName;
	ArrayList<OrderWrapper> orders = new ArrayList<OrderWrapper>();

	public String getCharacterName() {
		return this.characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public ArrayList<OrderWrapper> getOrders() {
		return this.orders;
	}

	public void addOrder(OrderWrapper ow) {
		this.orders.add(ow);
	}
}
