package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.joverseer.domain.Combat;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renders combats as a small dot on the top of the hex
 *  
 * @author Marios Skounakis
 */
public class CombatRenderer implements Renderer {
    protected MapMetadata mapMetadata = null;

    @Override
	public boolean appliesTo(Object obj) {
        return Combat.class.isInstance(obj);
    }

    protected void init() {
        this.mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (this.mapMetadata == null) init();
        ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        int w = this.mapMetadata.getGridCellWidth() / 3;
        int h = this.mapMetadata.getGridCellHeight() / 3;
        int dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() * 1/2 - w/2;
        int dy = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() * 1 / 9;

        Color color1 = ColorPicker.getInstance().getColor("combatFG");
        Color color2 = ColorPicker.getInstance().getColor("combatBorder");
        g.setColor(color1);

        RoundRectangle2D.Float e = new RoundRectangle2D.Float(x + dx, y + dy, w, h, w/5*2, h/5*2);
        g.fill(e);
        //g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);

        g.draw(e);
    }
}
