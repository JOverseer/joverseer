package org.joverseer.support.readers.newXml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderWrapper {
	int orderSet;
	String orderNumber;
	ArrayList<OrderParameterWrapper> parameters = new ArrayList<OrderParameterWrapper>();

	public int getOrderSet() {
		return this.orderSet;
	}

	public void setOrderSet(int orderSet) {
		this.orderSet = orderSet;
	}

	public String getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void addParameter(OrderParameterWrapper parameter) {
		this.parameters.add(parameter);
	}

	public ArrayList<String> getParameters() {
		Collections.sort(this.parameters, new Comparator<OrderParameterWrapper>() {

			@Override
			public int compare(OrderParameterWrapper o1, OrderParameterWrapper o2) {
				if (o1.getType().equals("Movement") && o2.getType().equals("Additional"))
					return -1;
				if (o1.getType().equals("Additional") && o2.getType().equals("Movement"))
					return 1;
				return o1.getSeqNo() - o2.getSeqNo();
			}

		});
		ArrayList<String> ret = new ArrayList<String>();
		for (OrderParameterWrapper opw : this.parameters) {
			ret.add(opw.getParameter());
		}
		return ret;
	}

}
