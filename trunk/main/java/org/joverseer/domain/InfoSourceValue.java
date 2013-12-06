package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.support.infoSources.InfoSource;

/**
 * Represents a value and infoSource pair. This coupling lets us assign attributes to
 * domain objects where the attribute has a different info source than the domain object.
 * 
 * @author Marios Skounakis
 *
 */
public class InfoSourceValue implements Serializable {
	private static final long serialVersionUID = 7381252624491847966L;

	Object value;
	InfoSource infoSource;
	
	public InfoSource getInfoSource() {
		return this.infoSource;
	}
	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public InfoSourceValue(Object value, InfoSource infoSource) {
		super();
		this.value = value;
		this.infoSource = infoSource;
	}
	public InfoSourceValue() {
		super();
	}
	
	
}
