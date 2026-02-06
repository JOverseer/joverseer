package org.joverseer.tools.ordercheckerIntegration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joverseer.domain.Order;
import org.joverseer.support.Container;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Container wrapper for the Order Results.
 * 
 * @author Marios Skounakis
 */
public class OrderResultContainer implements ApplicationListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2036430073120789112L;
	/**
	 * 
	 */
	Container<OrderResult> results;
	boolean[] resultOverrides = new boolean[26];
	
	public OrderResultContainer() {
		this.results = new Container<OrderResult>(new String[] { "order", "nationNo" });
	}

	public void addResult(OrderResult res) {
		this.results.addItem(res);
	}

	public void addAll(List<OrderResult> list) {
		for (OrderResult o : list) {
			addResult(o);
		}
	}

	public ArrayList<OrderResult> getResultsForOrder(Order o) {
		return this.results.findAllByProperty("order", o);
	}

	public OrderResultTypeEnum getResultTypeForOrder(Order o) {
		OrderResultTypeEnum resType = null;
		for (OrderResult r : getResultsForOrder(o)) {
			if (resType == null || r.getType().getValue() > resType.getValue()) {
				resType = r.getType();
			}
		}
		return resType;
	}

	public void removeResultsForOrder(Order o) {
		try {
			ArrayList<OrderResult> ors = getResultsForOrder(o);
			this.results.removeAll(ors);
			if(ors.size() != 0) this.overrideResultForNation(ors.get(0).getNationNo(), false);
		} catch(java.util.ConcurrentModificationException ex) {
			Logger.getRootLogger().error(ex.getMessage());
		}
	}
	
	public void overrideResultForNation(int n, boolean bool) {
		if(this.resultOverrides == null) this.resultOverrides = new boolean[26];
		this.resultOverrides[n] = bool;
	}
	
	public boolean getOverrideForNation(int n) {
		if(this.resultOverrides == null) this.resultOverrides = new boolean[26];
		return this.resultOverrides[n];
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
//			JOverseerEvent e = (JOverseerEvent) applicationEvent;
//			if (e.isLifecycleEvent(LifecycleEventsEnum.OrderChangedEvent)) {
//				Order o = (Order) e.getData();
//				try {
//					removeResultsForOrder(o);
//					System.out.println("Done " + this.getResultsForOrder(o).toString());
//				}
//				catch (java.util.ConcurrentModificationException ex) {
//					//HACK:
//					Logger.getRootLogger().error(ex.getMessage());
//				}
//			}
		}
	}
	
	public void clear() {
		this.results.clear();
	}
	
	public void clearAllOrdersForNation(int nationNo) {
		try {
			ArrayList<OrderResult> arr = (ArrayList<OrderResult>) this.results.findAllByProperty("nationNo", nationNo).clone();
			for (int i = 0; i < arr.size(); i++) {
				this.results.removeItem(arr.get(i));
			}
			
			//this.results.removeAllByProperties("nationNo", nationNo);
			//this.removeAllByNationNo(nationNo);
		} catch(java.util.ConcurrentModificationException ex) {
			Logger.getRootLogger().error(ex.getMessage());
		}
		this.overrideResultForNation(nationNo, false);
	}
	
	public Container<OrderResult> getContainer(){
		return this.results;
	}
	
//	public static OrderResultContainer instance()
//	{
//		return (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");
//	}

}
