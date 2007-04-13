package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.drawing.ColorPicker;


public class HighlightedHexRenderer extends DefaultHexRenderer {
    String highlightColor = "#ff3300";
    int width = 2;
    
    public boolean appliesTo(Object obj) {
        return HighlightHexesMapItem.class.isInstance(obj);
    }
    
    public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }

        if (metadata == null) {
            init();
        }
        Color color = Color.decode(getHighlightColor());
        
        Stroke currentStroke = g.getStroke();
        g.setStroke(GraphicUtils.getBasicStroke(getWidth()));
        HighlightHexesMapItem hmi = (HighlightHexesMapItem)obj;
        for (Integer hexNo : hmi.getHexesToHighlight()) {
            Point p = MapPanel.instance().getHexLocation(hexNo);
            Polygon polygon = new Polygon(xPoints, yPoints, 6);
            polygon.translate(p.x, p.y);
            g.setColor(color);
            g.drawPolygon(polygon);
        }
        g.setStroke(currentStroke);
    }

    
    public String getHighlightColor() {
        return highlightColor;
    }

    
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    
    public int getWidth() {
        return width;
    }

    
    public void setWidth(int width) {
        this.width = width;
    }


}
