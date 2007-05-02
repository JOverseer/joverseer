package org.joverseer.ui.map.renderers;

import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.domain.*;
import org.joverseer.domain.Character;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;


public class NationMessageRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return org.joverseer.domain.NationMessage.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        NationMessage nm = (NationMessage)obj;


        int w = mapMetadata.getGridCellWidth() / 3;
        int h = mapMetadata.getGridCellHeight() / 3;
        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1/2 - w/2;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 8 / 9;

        Color color1 = ColorPicker.getInstance().getColor("rumorFG");
        Color color2 = ColorPicker.getInstance().getColor("rumorBorder");
        g.setColor(color1);

        RoundRectangle2D.Float e = new RoundRectangle2D.Float(x + dx, y + dy, w, h, w/5*2, h/5*2);
        g.fill(e);
        //g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);

        g.draw(e);
        //g.drawRect(x + dx, y + dy, w, h);
    }
}
