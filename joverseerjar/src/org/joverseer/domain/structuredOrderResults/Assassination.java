package org.joverseer.domain.structuredOrderResults;

/**
 * Stores Assassinations (Order result)
 * 
 * @author Marios Skounakis
 *
 */
public class Assassination implements IStructuredOrderResult {
	String target;

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	
}
