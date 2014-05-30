package org.joverseer.domain;

import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for products
 * 
 * @author Marios Skounakis
 */
public enum ProductEnum {
	Leather("le"), Bronze("br"), Steel("st"), Mithril("mi"), Food("fo"), Mounts("mo"), Timber("ti"), Gold("go");

	String code;

	private ProductEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public static ProductEnum getFromCode(String code) {
		for (ProductEnum p : ProductEnum.values()) {
			if (p.getCode().equals(code))
				return p;
		}
		return null;
	}

	public String getRenderString() {
		return UIUtils.enumToString(this);
	}

	//This is the code, rather than the full word
	//Use UIUtils.EnumToString for the fullname.
	public String getLocalized() {
		return Messages.getString("ProductEnum."+code);
	}

	public static ProductEnum getFromText(String text) {
		if (text.equals("Leather"))
			return ProductEnum.Leather;
		if (text.equals("Bronze"))
			return ProductEnum.Bronze;
		if (text.equals("Steel"))
			return ProductEnum.Steel;
		if (text.equals("Mithril"))
			return ProductEnum.Mithril;
		if (text.equals("Food"))
			return ProductEnum.Food;
		if (text.equals("Timber"))
			return ProductEnum.Timber;
		if (text.equals("Mounts"))
			return ProductEnum.Mounts;
		if (text.equals("Gold"))
			return ProductEnum.Gold;
		return null;
	}
}
