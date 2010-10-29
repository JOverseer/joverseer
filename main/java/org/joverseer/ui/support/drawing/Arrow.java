package org.joverseer.ui.support.drawing;

/** * @(#)Arrow.java * * Copyright (c) 2000 by Sundar Dorai-Raj
 * * @author Sundar Dorai-Raj
 * * Email: sdoraira@vt.edu
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License
 * * as published by the Free Software Foundation; either version 2
 * * of the License, or (at your option) any later version,
 * * provided that any use properly credits the author.
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class Arrow {
	public static Shape getArrowHead(int x, int y, int base, int length, double theta) {
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];

		xPoints[0] = -length;
		xPoints[1] = 0;
		xPoints[2] = -length;

		yPoints[0] = base / 2;
		yPoints[1] = 0;
		yPoints[2] = -base / 2;

		Polygon p = new Polygon(xPoints, yPoints, 3);
		AffineTransform at = new AffineTransform();
		at.rotate(theta);
		Shape s = at.createTransformedShape(p);
		at = new AffineTransform();
		at.translate(x, y);
		s = at.createTransformedShape(s);
		return s;
	}

	public static void renderArrow(Point destination, Point origin, Color c, Stroke lineStroke, Graphics2D g) {
		Color oldColor = g.getColor();
		g.setColor(c);
		double theta = Math.atan2((destination.y - origin.y), (destination.x - origin.x));
		g.setStroke(new BasicStroke(1));
		Shape arrowHead = Arrow.getArrowHead(destination.x, destination.y, 10, 15, theta);
		g.fill(arrowHead);

		g.setStroke(lineStroke);
		// draw line
		g.drawLine(destination.x, destination.y, origin.x, origin.y);
		g.setColor(oldColor);
	}
}