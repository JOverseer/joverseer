package org.joverseer.ui.map.renderers;

import org.springframework.richclient.application.Application;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.config.ResourceFactoryBean;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexSideElementEnum;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ReplicateScaleFilter;
import java.util.HashMap;
import java.util.Collection;
import java.util.Locale;


public class DefaultHexRenderer extends ImageRenderer implements ApplicationListener {
    HashMap terrainColors = new HashMap();

    protected int[] xPoints = new int[6];
    protected int[] yPoints = new int[6];

    Point hexCenter;

    protected Polygon polygon;
    protected MapMetadata metadata = null;

    Color majorRiverColor;
    Color minorRiverColor;
    Color roadColor;
    Color bridgeFordColor;
    HashMap mapOptions;

    public DefaultHexRenderer() {
    }

    protected void init() {
        metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        xPoints[0] = metadata.getHexSize() / 2 * metadata.getGridCellWidth();
        xPoints[1] = metadata.getHexSize() * metadata.getGridCellWidth();
        xPoints[2] = metadata.getHexSize() * metadata.getGridCellWidth();
        xPoints[3] = metadata.getHexSize() / 2 * metadata.getGridCellWidth();
        xPoints[4] = 0;
        xPoints[5] = 0;

        yPoints[0] = 0;
        yPoints[1] = metadata.getHexSize() / 4 * metadata.getGridCellHeight();
        yPoints[2] = metadata.getHexSize() * 3 / 4 * metadata.getGridCellHeight();
        yPoints[3] = metadata.getHexSize() * metadata.getGridCellHeight();
        yPoints[4] = metadata.getHexSize() * 3 / 4 * metadata.getGridCellHeight();
        yPoints[5] = metadata.getHexSize() / 4 * metadata.getGridCellHeight();

        hexCenter = new Point(metadata.getHexSize() / 2 * metadata.getGridCellWidth(), metadata.getHexSize() / 2 * metadata.getGridCellHeight());


        MessageSource colorSource = (MessageSource)Application.instance().getApplicationContext().getBean("colorSource");

        String colorStr = "#DDDDDD";
        for (HexTerrainEnum t : HexTerrainEnum.values()) {
            colorStr = colorSource.getMessage(t.toString() + ".color", null, Locale.getDefault());
            setTerrainColor(t, Color.decode(colorStr));
        }

        colorStr = colorSource.getMessage("majorRiver.color", null, Locale.getDefault());
        setMajorRiverColor(Color.decode(colorStr));
        colorStr = colorSource.getMessage("minorRiver.color", null, Locale.getDefault());
        setMinorRiverColor(Color.decode(colorStr));
        colorStr = colorSource.getMessage("road.color", null, Locale.getDefault());
        setRoadColor(Color.decode(colorStr));
        colorStr = colorSource.getMessage("bridge.color", null, Locale.getDefault());
        setBridgeFordColor(Color.decode(colorStr));

        images.clear();
        
        mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
    }

    protected Polygon getSidePolygon(HexSideEnum side) {
        int i = side.getSide();
        Polygon p = new Polygon(new int[]{xPoints[i-1 % 6], xPoints[i % 6]}, new int[]{yPoints[i-1 % 6], yPoints[i % 6]}, 2);
        return p;
    }

    protected Point getSideCenter(HexSideEnum side) {
        int i = side.getSide();
        return new Point((xPoints[i % 6] + xPoints[i-1])/2, (yPoints[i % 6] + yPoints[i-1])/2);
    }

    public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }

    protected Color getColor(Hex hex) {
        if (terrainColors.containsKey(hex.getTerrain().getTerrain())) {
            return (Color)terrainColors.get(hex.getTerrain().getTerrain());
        }
        return Color.white;
    }

    public void renderRoad(Graphics2D g, HexSideEnum side, int x, int y) {
        Stroke s = g.getStroke();
        Stroke r = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Point sideCenter = getSideCenter(side);
        Point center = new Point(hexCenter);
        sideCenter.translate(x, y);
        center.translate(x, y);
        g.setColor(getRoadColor());
        g.setStroke(r);
        g.drawLine(center.x, center.y, sideCenter.x, sideCenter.y);
        g.setStroke(s);

    }

    public void renderMajorRiver(Graphics2D g, HexSideEnum side, int x, int y) {
        Stroke s = g.getStroke();
        Stroke r = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Polygon sp = getSidePolygon(side);
        sp.translate(x, y);
        g.setColor(getMajorRiverColor());

        g.setStroke(r);
        g.drawPolygon(sp);
        g.setStroke(s);
    }

    public void renderMinorRiver(Graphics2D g, HexSideEnum side, int x, int y) {
        Stroke s = g.getStroke();
        Stroke r = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Polygon sp = getSidePolygon(side);
        sp.translate(x, y);
        g.setColor(getMinorRiverColor());
        g.setStroke(r);
        g.drawPolygon(sp);
        g.setStroke(s);
    }

    public void renderBridgeOrFord(Graphics2D g, HexSideEnum side, int x, int y) {
        Stroke s = g.getStroke();
        Stroke r = new BasicStroke(6);
        Point sideCenter = getSideCenter(side);
        Point center = new Point(hexCenter);
        Point start = new Point((center.x + 2 * sideCenter.x) / 3, (center.y + 2 * sideCenter.y) / 3);
        start.translate(x, y);
        sideCenter.translate(x, y);
        g.setColor(getBridgeFordColor());
        g.setStroke(r);
        g.drawLine(start.x, start.y, sideCenter.x, sideCenter.y);
        g.setStroke(s);
    }

    protected boolean withinMapRange(int x, int y, MapMetadata metadata) {
        if (x < metadata.getMinMapColumn()) return false;
        if (x > metadata.getMaxMapColumn()) return false;
        if (y < metadata.getMinMapRow()) return false;
        if (y > metadata.getMaxMapRow()) return false;
        return true;
    }
    
    public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }

        if (metadata == null) {
            init();
        }

        Hex hex = (Hex)obj;
        if (!withinMapRange(hex.getColumn(), hex.getRow(), metadata)) return;
        
        boolean imageDrawn = false;
        if (mapOptions.get(MapOptionsEnum.HexGraphics) == null || mapOptions.get(MapOptionsEnum.HexGraphics).equals(MapOptionValuesEnum.HexGraphicsTexture)) {
        	BufferedImage img = getImage(hex.getTerrain().toString() + ".terrain", 
                metadata.getGridCellWidth() * metadata.getHexSize(), 
                metadata.getGridCellHeight() * metadata.getHexSize());
	        if (img!= null) {
	            g.drawImage(img, x, y, null);
	            Polygon polygon = new Polygon(xPoints, yPoints, 6);
	            polygon.translate(x, y);
	            g.setColor(Color.black);
	            g.drawPolygon(polygon);
	            imageDrawn = true;
	        }
        }
        if (!imageDrawn) {
            Polygon polygon = new Polygon(xPoints, yPoints, 6);
            polygon.translate(x, y);
            g.setColor(getColor(hex));
            g.fillPolygon(polygon);
            g.setColor(Color.black);
            g.drawPolygon(polygon);
        } 
        for (HexSideEnum side : HexSideEnum.values()) {
            Collection elements = hex.getHexSideElements(side);
            if (elements.size() > 0) {
                if (elements.contains(HexSideElementEnum.MajorRiver)) {
                    renderMajorRiver(g, side, x, y);
                } else if (elements.contains(HexSideElementEnum.MinorRiver)) {
                    renderMinorRiver(g, side, x, y);
                };
                if (elements.contains(HexSideElementEnum.Road)) {
                    renderRoad(g, side, x, y);
                };
                if (elements.contains(HexSideElementEnum.Bridge)) {
                    renderBridgeOrFord(g, side, x, y);
                };
                if (elements.contains(HexSideElementEnum.Ford)) {
                    renderBridgeOrFord(g, side, x, y);
                };

            }
        }
    }

    public void setTerrainColor(HexTerrainEnum terrain, Color c) {
        setTerrainColor(terrain.getTerrain(), c);
    }

    public void setTerrainColor(int i, Color c) {
        if (terrainColors.containsKey(i)) {
            terrainColors.remove(i);
        }
        terrainColors.put(i, c);
    }

    public Color getBridgeFordColor() {
        return bridgeFordColor;
    }

    public void setBridgeFordColor(Color bridgeFordColor) {
        this.bridgeFordColor = bridgeFordColor;
    }

    public Color getMajorRiverColor() {
        return majorRiverColor;
    }

    public void setMajorRiverColor(Color majorRiverColor) {
        this.majorRiverColor = majorRiverColor;
    }

    public Color getMinorRiverColor() {
        return minorRiverColor;
    }

    public void setMinorRiverColor(Color minorRiverColor) {
        this.minorRiverColor = minorRiverColor;
    }

    public Color getRoadColor() {
        return roadColor;
    }

    public void setRoadColor(Color roadColor) {
        this.roadColor = roadColor;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.MapMetadataChangedEvent.toString())) {
                init();
            }
        }
    }

}
