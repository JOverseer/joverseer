package org.joverseer.ui.support;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JScrollPane;

public class UIUtils {
	public static String enumToString(Object enumValue) {
		String str = enumValue.getClass().getSimpleName() + "." + enumValue.toString();
		return Messages.getString(str);
	}
	
	public static void fixScrollPaneMouseScroll(JScrollPane scp) {
		scp.getVerticalScrollBar().setUnitIncrement(25);
        scp.getHorizontalScrollBar().setUnitIncrement(25);
	}
	public static String OptPrefix(String collector,String prefix,String postfix)
	{
		if (collector.equals(""))
		{
			return postfix;
		} else {
			return collector + prefix + postfix;
		}
	}
	//TODO push the Optxxx
	public static String OptTab(String collector,String postfix)
	{
		return OptPrefix(collector, "\t", postfix);
	}
	public static String OptSpace(String collector,String postfix)
	{
		return OptPrefix(collector, " ", postfix);
	}
	public static String OptNewLine(String collector,String postfix)
	{
		return OptPrefix(collector, "\n", postfix);
	}

	public static String OptCommaSpace(String collector,String postfix)
	{
		return OptPrefix(collector, ", ", postfix);
	}
	
	public static String[] createMessages(String[] keys) {
		String[] ret = new String[keys.length];
		for(int i=0;i<keys.length;i++) {
			ret[i] = Messages.getString(keys[i]);
		}
		return ret;
	}
	/**
	 * Find a UI component on the screen by Name.
	 * @param name
	 * @return
	 */
	public static Component getComponentByName(Container root, String name) {
		for (Component c : root.getComponents()) {
			if (name.equals(c.getName())) {
				return c;
			}
			if (c instanceof Container) {
				Component result = getComponentByName((Container) c, name);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	} 
}
