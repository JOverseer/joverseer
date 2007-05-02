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
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.ArrayList;


public class HexInfoRenderer extends DefaultHexRenderer {
    GameHolder gh;
    HashMap mapOptions;
    int densityFactor = 4;
    Renderer hexNumberRenderer = null;
    BufferedImage img = null;

    @Override
    protected void init() {
        super.init();
        img = null;
        gh = GameHolder.instance();
        mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
    }


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
            img = new BufferedImage(metadata.getGridCellWidth() * metadata.getHexSize(), metadata.getGridCellHeight() * metadata.getHexSize(), BufferedImage.TRANSLUCENT);
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
                if (nmr.getRectangle().getX() + nmr.getRectangle().getWidth() == hex.getColumn() + 1) {
                    return hex.getRow() % 2 == 1;
                }
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
        Hex hex = (Hex)obj;
        if (!withinMapRange(hex.getColumn(), hex.getRow(), metadata)) return;
        Game game = gh.getGame();
        
        Object map = mapOptions.get(MapOptionsEnum.NationMap);
        boolean showClimate = mapOptions.get(MapOptionsEnum.ShowClimate) == null ? false : mapOptions.get(MapOptionsEnum.ShowClimate) == MapOptionValuesEnum.ShowClimateOn;
        boolean visible = false;
        if (map == null) {
            HexInfo hexInfo = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hex.getHexNo());
            visible = hexInfo.getVisible();
            int a = 1;
        } else if (map == MapOptionValuesEnum.NationMapDarkServants) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.DarkServants);
        } else if (map == MapOptionValuesEnum.NationMapFreePeople) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.FreePeople);
        } else if (map == MapOptionValuesEnum.NationMapNeutrals) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.Neutral);
        } else {
            int nationNo = Integer.parseInt((String)map);
            NationMapRange nmr = (NationMapRange)game.getMetadata().getNationMapRanges().findFirstByProperty("nationNo", nationNo);
            visible = false;
            if (nmr.getRectangle().contains(hex.getColumn(), hex.getRow())) {
                if (nmr.getRectangle().getX() + nmr.getRectangle().getWidth() == hex.getColumn() + 1) {
                    visible = hex.getRow() % 2 == 1;
                } else {
                    visible = true;
                }
            }

        }
        boolean repaintNumber = false;
        if (showClimate) {
            HexInfo hexInfo = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hex.getHexNo());
            if (hexInfo.getClimate() != null) {
                Color climateColor = ColorPicker.getInstance().getColor("climate." + hexInfo.getClimate().toString());
                Color transClimateColor = new Color(climateColor.getRed(), climateColor.getBlue(), climateColor.getGreen(), 100);
                int radius = metadata.getGridCellWidth() * 2;
                int cx = x + metadata.getGridCellWidth() * metadata.getHexSize() / 2;
                int cy = y + metadata.getGridCellHeight() * metadata.getHexSize() / 2;
                Ellipse2D.Float el = new Ellipse2D.Float(cx - radius / 2, cy - radius / 2, radius, radius);
                g.setColor(transClimateColor);
                g.fill(el);
                repaintNumber = true;
            }
        }
        if (!visible) {
            Image img = getImage();
            g.drawImage(img, x, y, null);
            repaintNumber = true;
        }
        
        if (repaintNumber) {
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
