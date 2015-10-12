package org.joverseer.domain.structuredOrderResults;

/**
 * Stores Kidnaps (Order result)
 * 
 * @author Marios Skounakis
 *
 */
public class Kidnap implements IStructuredOrderResult {
	String target;

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
