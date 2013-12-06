package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapTooltipHolder;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renders all characters in the hex as seperate dots
 * 
 * @author Marios Skounakis
 */
public class MultiCharacterRenderer implements Renderer {
    protected MapMetadata mapMetadata = null;

    @Override
	public boolean appliesTo(Object obj) {
        return Character.class.isInstance(obj);
    }

    protected void init() {
        this.mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (this.mapMetadata == null) init();
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn turn = game.getTurn();

        org.joverseer.domain.Character c = (Character)obj;

        // show dead chars according to preference
        String pval = PreferenceRegistry.instance().getPreferenceValue("map.deadCharacters");
        if (pval.equals("no")) {
            if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) return;
        }

        // do not show hostages
        if (c.getHostage() != null && c.getHostage()) return;

        ArrayList<Character> charsInHex = null;
        charsInHex = turn.getContainer(TurnElementsEnum.Character).findAllByProperty("hexNo", c.getHexNo());
        ArrayList<Character> toRemove = new ArrayList<Character>();
        if (pval.equals("no")) {
            for (Character ch : charsInHex) {
                if (ch.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
                    toRemove.add(ch);
                }
            }
        }
        for (Character ch : charsInHex) {
            if (ch.getHostage() != null && ch.getHostage() == true && !toRemove.contains(ch)) {
                toRemove.add(ch);
            }
        }
        charsInHex.removeAll(toRemove);

        int i = charsInHex.indexOf(c);
        //HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
        //boolean simpleColors =!mapOptions.get(MapOptionsEnum.HexGraphics).equals(MapOptionValuesEnum.HexGraphicsTexture);
        pval = PreferenceRegistry.instance().getPreferenceValue("map.charsAndArmies");
        boolean simpleColors = pval.equals("simplified");
        
        
        if (i>0 && simpleColors) return;
        
        int ii = i % 12;
        int jj = i / 12;

        int dx = this.mapMetadata.getGridCellWidth() * 1 / 4;
        int dy = this.mapMetadata.getGridCellHeight();
        int w = this.mapMetadata.getGridCellWidth() / 3;
        int h = this.mapMetadata.getGridCellHeight() / 3;

        //todo make decision based on allegiance, not nation no
//      if (c.getNationNo() > 10) {
//      dx = dx + mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w;
//      } else {
//      dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - dx;
//      }
        Color color1 = ColorPicker.getInstance().getColor1(c.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(c.getNationNo());
        if (simpleColors) {
        	color1 = Color.white;
        	color2 = Color.black;
        }
        
        boolean dragon = InfoUtils.isDragon(c.getName()); 
        if (dragon && !simpleColors) {
        	color1 = ColorPicker.getInstance().getColor("dragon");
        }
        
        g.setColor(color1);
        //g.fillRect(x + dx, y + dy, w, h);

        int cx = x + dx + (w * ii);
        int cy = y + dy + (h * jj);
        RoundRectangle2D.Float e = new RoundRectangle2D.Float(cx, cy, w, h, w/5*2, h/5*2);
        g.fill(e);

        g.setColor(color2);
        //g.drawRect(x + dx, y + dy, w, h);
        g.draw(e);
        if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
            g.drawLine((int)e.getBounds().getX(), (int)e.getBounds().getY(), 
                    (int)e.getBounds().getMaxX(), (int)e.getBounds().getMaxY());
        }
        
        if (dragon && !simpleColors) {
        	g.setColor(color2);
        	Rectangle2D.Float ee = new Rectangle2D.Float(cx+2, cy+2, w-2, h-2);
            g.fill(ee);
        }
        MapTooltipHolder.instance().addTooltipObject(new Rectangle(cx, cy, w, h), c);
    }
}
