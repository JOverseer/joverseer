package org.joverseer.ui.listviews.advancedCharacterListView;

import org.joverseer.support.infoSources.InfoSource;

public class CharacterAttributeWrapper {
	String attribute;
	InfoSource infoSource;
	Object value;
	int turnNo;
	
	public CharacterAttributeWrapper(String attribute, Object value, int turnNo, InfoSource infoSource) {
		this.attribute = attribute;
		this.value = value;
		this.turnNo = turnNo;
		this.infoSource = infoSource;
		
	}
	
	public InfoSource getInfoSource() {
		return infoSource;
	}
	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public int getTurnNo() {
		return turnNo;
	}
	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}
	
	
}