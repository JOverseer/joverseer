package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

public class OrdersGiven {
	String characterName;
	ArrayList<OrderWrapper> orders = new ArrayList<OrderWrapper>();

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public ArrayList<OrderWrapper> getOrders() {
		return orders;
	}

	public void addOrder(OrderWrapper ow) {
		orders.add(ow);
	}
}
