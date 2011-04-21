package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Generic String transfer handler Pretty useless
 * 
 * @author Marios Skounakis
 */
public class StringTransferHandler extends TransferHandler {
	String property;

	public StringTransferHandler(String arg0) {
		super(arg0);
		property = arg0;
	}

	protected void importString(JComponent c, String str) {

	}

	protected void cleanup(JComponent c, boolean remove) {

	}

	protected String getValue(JComponent c) {
		try {
			Object o;
			Method m = c.getClass().getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
			o = m.invoke(c, (Object) null);
			String v = o.toString();
			return v;
		} catch (Exception exc) {
			return "";
		}
	}

	protected String exportString(JComponent c) {
		return getValue(c);
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(exportString(c));
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				String str = (String) t.getTransferData(DataFlavor.stringFlavor);
				importString(c, str);
				return true;
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) {
			}
		}

		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == MOVE);
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.stringFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}

}
