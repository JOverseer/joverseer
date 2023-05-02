package org.joverseer.support.readers.newXml;

/**
 * To hold the overall game properties gathered from the XML
 * @author Dave
 *
 */
public class GameInfoOptionWrapper {
	protected String name;
	protected String value;
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
