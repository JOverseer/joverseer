package org.joverseer.ui.support;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.flexdock.docking.DockingManager;


public class GraphicUtils {
    public static Font getFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    public static Stroke getBasicStroke(int width) {
        return new BasicStroke(width);
    }

    public static Stroke getDashStroke(int width, int dashSize) {
        return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{dashSize, dashSize}, 2);
    }
    
    public static void addOverwriteDropListener(JTextComponent c) {
        final JTextComponent cc = c;
        c.setDropTarget(new DropTarget(c, new DropTargetListener() {

            public void dragEnter(DropTargetDragEvent arg0) {
            }
    
            public void dragExit(DropTargetEvent arg0) {
            }
    
            public void dragOver(DropTargetDragEvent arg0) {
            }
    
            public void drop(DropTargetDropEvent arg0) {
                try {
                    cc.setText(arg0.getTransferable().getTransferData(DataFlavor.stringFlavor).toString());
                    cc.requestFocus();
                }
                catch (Exception exc) {
                    
                }
            }
            public void dropActionChanged(DropTargetDragEvent arg0) {
            }
        }));
    }
    
    
}       
