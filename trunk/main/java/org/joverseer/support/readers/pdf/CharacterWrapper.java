package org.joverseer.support.readers.pdf;

import java.util.ArrayList;
import org.joverseer.domain.Character;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public class CharacterWrapper {
	String name;
	String orders;
	int hexNo;
	
	ArrayList<OrderResult> orderResults = new ArrayList<OrderResult>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<OrderResult> getOrderResults() {
		return orderResults;
	}

	public void setOrderResults(ArrayList orderResults) {
		this.orderResults = (ArrayList<OrderResult>)orderResults;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}
	
	public void addOrderResult(OrderResult result) {
		orderResults.add(result);
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	
	public void updateCharacter(Character c) {
            c.setOrderResults(getOrders());
        }
	
}
