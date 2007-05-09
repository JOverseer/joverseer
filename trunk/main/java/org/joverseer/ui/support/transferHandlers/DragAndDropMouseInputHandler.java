package org.joverseer.ui.support.transferHandlers;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;


public class DragAndDropMouseInputHandler extends MouseInputAdapter {
    private boolean recognized;
    protected Point pressedPoint;
    private boolean isDragged;
    
    public void mousePressed(MouseEvent e) {
        pressedPoint = e.getPoint();
    }
 
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        if (!recognized &&
            ((Math.abs(pressedPoint.x - p.x) > 5) ||
             (Math.abs(pressedPoint.y - p.y) > 5))) {
            isDragged = true;
            recognized = true;
            JComponent c = (JComponent) e.getSource();
            TransferHandler th = getTransferHandler(e);
            if (th != null) {
                th.exportAsDrag(c, e, DnDConstants.ACTION_COPY);
            }
        }
    }
 
    public void mouseReleased(MouseEvent e) {
        recognized = false;
        isDragged = false;
        pressedPoint = null;
    }
    
    public TransferHandler getTransferHandler(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        TransferHandler th = c.getTransferHandler();
        return th;
    }
    
    public static void addToComponent(JComponent c) {
        addToComponent(c, new DragAndDropMouseInputHandler());
    }
    
    public static void addToComponent(JComponent c, DragAndDropMouseInputHandler handler) {
        c.addMouseListener(handler);
        c.addMouseMotionListener(handler);
    }
}
