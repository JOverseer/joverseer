package org.joverseer.ui.map.renderers;

import org.joverseer.domain.HexInfo;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 22 Οκτ 2006
 * Time: 11:35:24 μμ
 * To change this template use File | Settings | File Templates.
 */
public class HexInfoRenderer extends DefaultHexRenderer {
    int densityFactor = 4;
    Renderer hexNumberRenderer = null;
    BufferedImage img = null;

    public int getDensityFactor() {
        return densityFactor;
    }

    public void setDensityFactor(int densityFactor) {
        this.densityFactor = densityFactor;
    }

    public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }

    private Image getImage() {
        if (img == null) {
            MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
            img = new BufferedImage(mm.getGridCellWidth() * mm.getHexSize(), mm.getGridCellHeight() * mm.getHexSize(), BufferedImage.TRANSLUCENT);
            Polygon polygon = new Polygon(xPoints, yPoints, 6);
            Graphics2D g = img.createGraphics();

            int w = metadata.getHexSize() * metadata.getGridCellWidth();
            int h = metadata.getHexSize() * metadata.getGridCellHeight();
            int m = w / getDensityFactor();

            g.setClip(null);
            g.clip(polygon);
            g.setColor(Color.gray);
            for (int i=2; i<10; i++) {
                Line2D.Float l = new Line2D.Float(- w + m * i, h, m * i, 0);
                g.draw(l);
            }
            g.setColor(Color.black);
            g.drawPolygon(polygon);
            g.setClip(null);

        }
        return img;
    }

    private boolean visibleToAllegiance(Hex hex, Game game, NationAllegianceEnum allegiance) {
        ArrayList<NationMapRange> nmrs = (ArrayList <NationMapRange>)game.getMetadata().getNationMapRanges().getItems();
        for (NationMapRange nmr : nmrs) {
            Nation n = game.getMetadata().getNationByNum(nmr.getNationNo());
            if (n.getAllegiance() != allegiance) continue;
            if (nmr.getRectangle().contains(hex.getColumn(), hex.getRow())) {
                return true;
            }
        }
        return false;
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }

        if (metadata == null) {
            init();
        }
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
        String map = (String)mapOptions.get("nationMap");
        boolean visible = false;
        Hex hex = (Hex)obj;
        if (map == null) {
            HexInfo hexInfo = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hex.getHexNo());
            visible = hexInfo.getVisible();
        } else if (map.equals("Dark Servants")) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.DarkServants);
        } else if (map.equals("Free People")) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.FreePeople);
        } else if (map.equals("Neutrals")) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.Neutral);
        } else {
            int nationNo = Integer.parseInt(map);
            NationMapRange nmr = (NationMapRange)game.getMetadata().getNationMapRanges().findFirstByProperty("nationNo", nationNo);
            visible = nmr.getRectangle().contains(hex.getColumn(), hex.getRow());
            int a = 1;
        }
        if (!visible) {
            Image img = getImage();
            g.drawImage(img, x, y, null);

            if (getHexNumberRenderer() != null) {
                getHexNumberRenderer().render(hex, g, x, y);
            }
        }
    }

    public Renderer getHexNumberRenderer() {
        return hexNumberRenderer;
    }

    public void setHexNumberRenderer(Renderer hexNumberRendererId) {
        this.hexNumberRenderer = hexNumberRendererId;
    }
}
