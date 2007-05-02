package org.joverseer.ui.map.renderers;

import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.domain.Character;
import org.joverseer.domain.Army;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementUtils;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class RangeMapItemRenderer extends DefaultHexRenderer {
    String highlightColor = "#ff3300";
    int width = 2;
    
    public boolean appliesTo(Object obj) {
        return CharacterRangeMapItem.class.isInstance(obj) ||
                ArmyRangeMapItem.class.isInstance(obj);
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (CharacterRangeMapItem.class.isInstance(obj)) {
            renderCharacterRangeMapItem(obj, g, x, y);
        } else if (ArmyRangeMapItem.class.isInstance(obj)) {
            renderArmyRangeMapItem(obj, g, x, y);
        }
    }

    private void renderArmyRangeMapItem (Object obj, Graphics2D g, int x, int y) {
        ArmyRangeMapItem armi = (ArmyRangeMapItem)obj;
        Army a = armi.getArmy();

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
