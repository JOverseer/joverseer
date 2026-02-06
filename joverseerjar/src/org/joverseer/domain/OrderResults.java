package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.support.Container;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;

/*
 * Class which holds all order results for a turn. 
 * A bit messy, holds the container for 
 */
public class OrderResults implements IHasTurnNumber, Serializable {
	private static final long serialVersionUID = -2068442266182484674L;
	int turnNo;

	OrderResultContainer results;
	
	public OrderResults(int turnN, OrderResultContainer res) {
		this.turnNo = turnN;
		this.results = res;
	}
	
	@Override
	public int getTurnNo() {
		return this.turnNo;
	}
	
	public OrderResultContainer getResultCont() {
		return this.results;
	}
	
	public Container<OrderResult> getOrderResultContainer(){
		return this.results.getContainer();
	}

}
