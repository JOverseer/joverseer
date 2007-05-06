package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.support.infoSources.InfoSource;

public class InfoSourceValue implements Serializable {
	private static final long serialVersionUID = 7381252624491847966L;

	Object value;
	InfoSource infoSource;
	
	public InfoSource getInfoSource() {
		return infoSource;
	}
	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}
	public Object getValue() {
		return value;
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
