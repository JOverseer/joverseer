package org.joverseer.ui.support.transferHandlers;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

/**
 * MouseInputAdapter that adds drag&drop functionality to a component Method
 * getTransferHandler can be overriden to provide the customer handler for the
 * dnd operation
 * 
 * @author Marios Skounakis
 */
public class DragAndDropMouseInputHandler extends MouseInputAdapter {
	private boolean recognized;
	protected Point pressedPoint;

	@Override
	public void mousePressed(MouseEvent e) {
		this.pressedPoint = e.getPoint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		if (!this.recognized && ((Math.abs(this.pressedPoint.x - p.x) > 5) || (Math.abs(this.pressedPoint.y - p.y) > 5))) {
			this.recognized = true;
			JComponent c = (JComponent) e.getSource();
			TransferHandler th = getTransferHandler(e);
			if (th != null) {
				th.exportAsDrag(c, e, DnDConstants.ACTION_COPY);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.recognized = false;
		this.pressedPoint = null;
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
