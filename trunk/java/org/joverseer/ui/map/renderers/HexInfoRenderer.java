package org.joverseer.ui.map.renderers;

import org.joverseer.domain.HexInfo;
import org.joverseer.metadata.domain.Hex;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 22 Οκτ 2006
 * Time: 11:35:24 μμ
 * To change this template use File | Settings | File Templates.
 */
public class HexInfoRenderer extends DefaultHexRenderer {
    int densityFactor = 4;

    public int getDensityFactor() {
        return densityFactor;
    }

    public void setDensityFactor(int densityFactor) {
        this.densityFactor = densityFactor;
    }

    public boolean appliesTo(Object obj) {
        return HexInfo.class.isInstance(obj);
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }

        if (metadata == null) {
            init();
        }

        HexInfo hexInfo = (HexInfo)obj;
        if (!hexInfo.getVisible()) {
            Polygon polygon = new Polygon(xPoints, yPoints, 6);
            polygon.translate(x, y);

            int w = metadata.getHexSize() * metadata.getGridCellWidth();
            int h = metadata.getHexSize() * metadata.getGridCellHeight();
            int m = w / getDensityFactor();

            Stroke s = new BasicStroke(1);
            g.setStroke(s);

            g.setClip(null);
            g.clip(polygon);
            g.setColor(Color.gray);
            for (int i=2; i<10; i++) {
                Line2D.Float l = new Line2D.Float(x - w + m * i, y + h, x + m * i, y);
                g.draw(l);
            }
            g.setColor(Color.black);
            g.drawPolygon(polygon);
            g.setClip(null);
        }
    }
}
