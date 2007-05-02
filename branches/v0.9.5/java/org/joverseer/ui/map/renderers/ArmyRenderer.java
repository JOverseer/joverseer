package org.joverseer.ui.map.renderers;

import org.joverseer.domain.*;
import org.joverseer.domain.Character;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

import java.awt.*;


public class ArmyRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return Army.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        Army army = (Army)obj;


        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1 / 5;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 7 / 10;
        int w = mapMetadata.getGridCellWidth() / 2;
        int h = mapMetadata.getGridCellHeight() / 2;

        //todo make decision based on allegiance, not nation no
        if (army.getNationNo() > 10) {
            dx = dx + mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w;
        } else {
            dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - dx;
        }
        Color color1 = ColorPicker.getInstance().getColor1(army.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(army.getNationNo());
        g.setColor(color1);
        g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);
        g.drawRect(x + dx, y + dy, w, h);
    }
}
