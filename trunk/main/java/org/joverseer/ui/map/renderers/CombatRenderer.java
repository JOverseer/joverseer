package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;


public class CombatRenderer implements Renderer {
    protected MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return Combat.class.isInstance(obj);
    }

    protected void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn turn = game.getTurn();

        Combat c = (Combat)obj;
        
        int w = mapMetadata.getGridCellWidth() / 3;
        int h = mapMetadata.getGridCellHeight() / 3;
        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1/2 - w/2;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 1 / 9;

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
