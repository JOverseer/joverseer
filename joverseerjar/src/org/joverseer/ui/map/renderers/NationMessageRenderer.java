package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renders nation messages as a small dot on the bottom of the hex
 * 
 * @author Marios Skounakis
 */
public class NationMessageRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    @Override
	public boolean appliesTo(Object obj) {
        return org.joverseer.domain.NationMessage.class.isInstance(obj);
    }

    private void init() {
        this.mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (this.mapMetadata == null) init();

        //NationMessage nm = (NationMessage)obj;


        int w = this.mapMetadata.getGridCellWidth() / 3;
        int h = this.mapMetadata.getGridCellHeight() / 3;
        int dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() * 1/2 - w/2;
        int dy = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() * 8 / 9;

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
