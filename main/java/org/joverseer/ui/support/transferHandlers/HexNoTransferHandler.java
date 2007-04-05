package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.ui.map.MapPanel;


public class HexNoTransferHandler extends StringTransferHandler {
    public HexNoTransferHandler(String arg0) {
        super(arg0);
    }

    protected String exportString(JComponent c) {
//        String v = String.valueOf(((MapPanel)c).getSelectedHex().x * 100 + ((MapPanel)c).getSelectedHex().y);
//        if (v.length() < 4) {
//            v = "0" + v;
//        }
    	String v = ((MapPanel)c).getHex();
        return v;
    }
}
