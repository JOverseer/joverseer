package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.NationMessage;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;


public class ArtifactRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return org.joverseer.domain.Artifact.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        Artifact a = (Artifact)obj;


        int w = mapMetadata.getGridCellWidth() / 3;
        int h = mapMetadata.getGridCellHeight() / 3;
        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1/10;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 1 / 2 - h / 2;

        Color color1 = ColorPicker.getInstance().getColor("artifactFG");
        Color color2 = ColorPicker.getInstance().getColor("artifactBorder");
        g.setColor(color1);

        RoundRectangle2D.Float e = new RoundRectangle2D.Float(x + dx, y + dy, w, h, w/5*2, h/5*2);
        g.fill(e);
        //g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);

        g.draw(e);
        //g.drawRect(x + dx, y + dy, w, h);
    }
}
