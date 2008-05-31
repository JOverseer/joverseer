package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;


public class MultiCharacterRenderer implements Renderer {
    protected MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return Character.class.isInstance(obj);
    }

    protected void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn turn = game.getTurn();

        org.joverseer.domain.Character c = (Character)obj;
        
        String pval = PreferenceRegistry.instance().getPreferenceValue("map.deadCharacters");
        if (pval.equals("no")) {
            if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) return;
        }

        ArrayList<Character> charsInHex = null;
        charsInHex = turn.getContainer(TurnElementsEnum.Character).findAllByProperty("hexNo", c.getHexNo());
        if (pval.equals("no")) {
            ArrayList<Character> toRemove = new ArrayList<Character>();
            for (Character ch : charsInHex) {
                if (ch.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
                    toRemove.add(ch);
                }
            }
            charsInHex.removeAll(toRemove);
        }
        int i = charsInHex.indexOf(c);

        int ii = i % 12;
        int jj = i / 12;
        
        int dx = mapMetadata.getGridCellWidth() * 1 / 4;
        int dy = mapMetadata.getGridCellHeight();
        int w = mapMetadata.getGridCellWidth() / 3;
        int h = mapMetadata.getGridCellHeight() / 3;

        //todo make decision based on allegiance, not nation no
//        if (c.getNationNo() > 10) {
//            dx = dx + mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w;
//        } else {
//            dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - dx;
//        }
        Color color1 = ColorPicker.getInstance().getColor1(c.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(c.getNationNo());
        g.setColor(color1);
        //g.fillRect(x + dx, y + dy, w, h);


        RoundRectangle2D.Float e = new RoundRectangle2D.Float(x + dx + (w * ii), y + dy + (h * jj), w, h, w/5*2, h/5*2);
        g.fill(e);

        g.setColor(color2);
        //g.drawRect(x + dx, y + dy, w, h);
        g.draw(e);
    }
}