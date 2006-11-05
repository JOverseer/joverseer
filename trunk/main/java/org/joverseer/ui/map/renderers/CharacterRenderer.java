package org.joverseer.ui.map.renderers;

import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.ColorPicker;
import org.joverseer.domain.Character;
import org.springframework.richclient.application.Application;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 18, 2006
 * Time: 8:09:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CharacterRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return Character.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        Character c = (Character)obj;


        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 2 / 5;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 3 / 5;
        int w = mapMetadata.getGridCellWidth() / 2;
        int h = mapMetadata.getGridCellHeight() / 2;

        //todo make decision based on allegiance, not nation no
        if (c.getNationNo() > 10) {
            dx = dx + mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w;
        } else {
            dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - dx;
        }
        Color color1 = ColorPicker.getInstance().getColor1(c.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(c.getNationNo());
        g.setColor(color1);
        g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);
        g.drawRect(x + dx, y + dy, w, h);
    }
}
