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
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;


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

        GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");

        HashMap rangeHexes = armi.getRangeHexes();
        for (Object hno : rangeHexes.keySet()) {
            int hexNo = (Integer)hno;
            int cost = (Integer)rangeHexes.get(hexNo);
            Point p = MapPanel.instance().getHexCenter(hexNo);
            Rectangle2D.Float e = new Rectangle2D.Float(p.x - 5, p.y - 5, 10, 10);
            g.setColor(Color.RED);
            g.fill(e);
            g.setColor(Color.WHITE);
            g.setFont(f);
            g.drawString(String.valueOf(cost), p.x - 4, p.y + 4);
        }
    }


    private void renderCharacterRangeMapItem (Object obj, Graphics2D g, int x, int y) {
        CharacterRangeMapItem crmi = (CharacterRangeMapItem)obj;
        Character c = crmi.getCharacter();
        int hexNo = Integer.parseInt(c.getHexNo());
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
            if ((d = MovementUtils.distance(hexNo, hn)) == 12) {
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
