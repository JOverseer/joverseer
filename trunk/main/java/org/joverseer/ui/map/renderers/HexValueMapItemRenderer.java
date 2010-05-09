package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.joverseer.domain.Army;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.domain.mapItems.AbstractRangeMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.domain.mapItems.HexInfoTurnReportMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * Renders MapItem objects that store hex-value pairs
 * e.g. ArmyRangeMapItem, HexInfoTurnReportMapItem, etc
 * 
 * @author Marios Skounakis
 */
public class HexValueMapItemRenderer extends DefaultHexRenderer {
    String highlightColor = "#33AA00";
    int width = 2;
    
    public boolean appliesTo(Object obj) {
        return CharacterRangeMapItem.class.isInstance(obj) ||
        		AbstractRangeMapItem.class.isInstance(obj) ||
                HexInfoTurnReportMapItem.class.isInstance(obj);
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (CharacterRangeMapItem.class.isInstance(obj)) {
            renderCharacterRangeMapItem(obj, g, x, y);
        } else if (AbstractRangeMapItem.class.isInstance(obj)) {
        	renderRangeMapItem(obj, g, x, y);
        } else if (HexInfoTurnReportMapItem.class.isInstance(obj)) {
            renderHexInfoTurnReportMapItem(obj, g, x, y);
        }
    }

    private void renderRangeMapItem (Object obj, Graphics2D g, int x, int y) {
        AbstractRangeMapItem armi = (AbstractRangeMapItem)obj;
        
        String fontName = "Helvetica";
        int fontSize = 9;
        int fontStyle = Font.PLAIN;
        Font f = new Font(fontName, fontStyle, fontSize);

        Color bgColor = getColorForArmy(armi.isFed());
        
        HashMap rangeHexes = armi.getRangeHexes();
        for (Object hno : rangeHexes.keySet()) {
            int hexNo = (Integer)hno;
            int cost = (Integer)rangeHexes.get(hexNo);
            String strCost = String.valueOf(cost);
            Rectangle2D r = g.getFontMetrics().getStringBounds(strCost, g);
            int w = new Double(r.getWidth()).intValue();
            int h = new Double(r.getHeight()).intValue();
            int w1 = Math.max(w, h);
            int h1 = w1;
            Point p = MapPanel.instance().getHexCenter(hexNo);
            Rectangle2D.Float e = new Rectangle2D.Float(p.x - w1 / 2 - 2, p.y - h1 / 2 - 2, w1 + 4, h1 + 4);
            g.setColor(bgColor);
            g.fill(e);
            g.setColor(Color.WHITE);
            g.draw(e);
            g.setFont(f);
            g.drawString(strCost, p.x - w / 2, p.y + h / 2);
        }
    }
    
    private void renderHexInfoTurnReportMapItem(Object obj, Graphics2D g, int x, int y) {
    	Game game = GameHolder.instance().getGame();
    	
    	String fontName = "Helvetica";
        int fontSize = 9;
        int fontStyle = Font.PLAIN;
        Font f = new Font(fontName, fontStyle, fontSize);

        Color bgColor = Color.decode("#999999");
    	
    	HexInfoTurnReportMapItem hitrmi = (HexInfoTurnReportMapItem)obj;
    	for (Object hno : hitrmi.getHexes().keySet()) {
    		int hexNo = (Integer)hno;
            int turn = (Integer)hitrmi.getHexes().get(hexNo);
            if (turn == game.getCurrentTurn()) {
            	continue;
            }
            if (turn < 0) turn = 0;
            int colorDistance = Math.min(10, game.getCurrentTurn() - turn) * 10;
            Color c = new Color(bgColor.getRed() - colorDistance,
            					bgColor.getGreen() - colorDistance,
            					bgColor.getBlue() - colorDistance);
            
            String str = String.valueOf(turn);
            Rectangle2D r = g.getFontMetrics().getStringBounds(str, g);
            int w = new Double(r.getWidth()).intValue();
            int h = new Double(r.getHeight()).intValue();
            int w1 = Math.max(w, h);
            int h1 = w1;
            Point p = MapPanel.instance().getHexCenter(hexNo);
            Rectangle2D.Float e = new Rectangle2D.Float(p.x - w1 / 2 - 2, p.y - h1 / 2 - 2, w1 + 4, h1 + 4);
            g.setColor(c);
            g.fill(e);
            g.setColor(Color.WHITE);
            g.draw(e);
            g.setFont(f);
            g.drawString(str, p.x - w / 2, p.y + h / 2);
    	}
    }

    private Color getColorForArmy(boolean fed) {
        MessageSource colorSource = (MessageSource)Application.instance().getApplicationContext().getBean("colorSource");
        if (!fed) {
            return Color.decode(colorSource.getMessage("armyRangeBg.unfed.color", new Object[]{}, Locale.getDefault()));
        } else {
            return Color.decode(colorSource.getMessage("armyRangeBg.fed.color", new Object[]{}, Locale.getDefault()));
        }
    }

    private void renderCharacterRangeMapItem (Object obj, Graphics2D g, int x, int y) {
        CharacterRangeMapItem crmi = (CharacterRangeMapItem)obj;
        int hexNo = crmi.getHexNo();
        int range = crmi.getRange();
        if (metadata == null) {
            init();
        }
        Color color = Color.decode(getHighlightColor());
        
        Stroke currentStroke = g.getStroke();
        g.setStroke(GraphicUtils.getBasicStroke(getWidth()));
        // find all hexes within 12 distance from hexNo
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        ArrayList<Hex> hexes = (ArrayList<Hex>)gm.getHexes();
        for (Hex h : hexes) {
            int hn = h.getColumn() * 100 + h.getRow();
            int d;
            if ((d = MovementUtils.distance(hexNo, hn)) == range) {
                // draw each hex in range with special color
                Point p = MapPanel.instance().getHexLocation(hn);
                Polygon polygon = new Polygon(xPoints, yPoints, 6);
                polygon.translate(p.x, p.y);
                g.setColor(color);
                g.drawPolygon(polygon);
//                Point p = MapPanel.instance().getHexCenter(hn);
//                Ellipse2D.Float e = new Ellipse2D.Float(p.x - 3, p.y - 3, 6, 6);
//                g.setColor(Color.RED);
//                g.fill(e);
            }
        }
        g.setStroke(currentStroke);
    }
    
    public String getHighlightColor() {
    	highlightColor = "#DDFF00";
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
