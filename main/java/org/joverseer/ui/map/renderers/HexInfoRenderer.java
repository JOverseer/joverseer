package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.domain.HexInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renders visible/invisible hexes
 * it can render:
 * - a specific map (e.g. Northmen)
 * - a map combined from multiple nations (e.g. DS map)
 * - the current map, using the HexInfo objects
 * 
 * @author Marios Skounakis
 */
public class HexInfoRenderer extends DefaultHexRenderer {
    GameHolder gh;
    HashMap mapOptions;
    int densityFactor = 4;
    Renderer hexNumberRenderer = null;
    BufferedImage img = null;
    String fontName = "Microsoft Sans Serif";
    int fontSize = 8;
    int fontStyle = Font.ITALIC;
    
    @Override
	protected void init() {
        super.init();
        this.img = null;
        this.gh = GameHolder.instance();
        this.mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
    }


    public int getDensityFactor() {
        return this.densityFactor;
    }

    public void setDensityFactor(int densityFactor) {
        this.densityFactor = densityFactor;
    }

    @Override
	public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }

    private Image getImage() {
        if (this.img == null) {
            this.img = new BufferedImage(this.metadata.getGridCellWidth() * this.metadata.getHexSize(), this.metadata.getGridCellHeight() * this.metadata.getHexSize(), Transparency.TRANSLUCENT);
            Polygon polygon1 = new Polygon(this.xPoints, this.yPoints, 6);
            Graphics2D g = this.img.createGraphics();

            int w = this.metadata.getHexSize() * this.metadata.getGridCellWidth();
            int h = this.metadata.getHexSize() * this.metadata.getGridCellHeight();
            int m = w / getDensityFactor();

            g.setClip(null);
            g.clip(polygon1);
            g.setColor(Color.gray);
            for (int i=2; i<10; i++) {
                Line2D.Float l = new Line2D.Float(- w + m * i, h, m * i, 0);
                g.draw(l);
            }
            g.setColor(Color.black);
            g.drawPolygon(polygon1);
            g.setClip(null);

        }
        return this.img;
    }

    private boolean visibleToAllegiance(Hex hex, Game game, NationAllegianceEnum allegiance) {
        ArrayList<NationMapRange> nmrs = (ArrayList <NationMapRange>)game.getMetadata().getNationMapRanges().getItems();
        for (NationMapRange nmr : nmrs) {
            Nation n = game.getMetadata().getNationByNum(nmr.getNationNo());
            if (!n.getAllegiance().equals(allegiance)) continue;
            if (nmr.getRectangle().contains(hex.getColumn(), hex.getRow())) {
                if (nmr.getRectangle().getX() + nmr.getRectangle().getWidth() == hex.getColumn() + 1) {
                	continue;
                }
                return true;
            }
        }
        return false;
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }
        String pval = PreferenceRegistry.instance().getPreferenceValue("map.fogOfWarStyle");
        boolean simpleColors = pval.equals("xs");

        if (this.metadata == null) {
            init();
        }
        Hex hex = (Hex)obj;
        if (!withinMapRange(hex.getColumn(), hex.getRow(), this.metadata)) return;
        Game game = this.gh.getGame();
        
        Object map = this.mapOptions.get(MapOptionsEnum.NationMap);
        boolean showClimate = (this.mapOptions.get(MapOptionsEnum.ShowClimate) == null ? false : this.mapOptions.get(MapOptionsEnum.ShowClimate).equals(MapOptionValuesEnum.ShowClimateOn));
        boolean visible = false;
        if (map == null) {
            HexInfo hexInfo = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hex.getHexNo());
            visible = hexInfo.getVisible();
        } else if (map == MapOptionValuesEnum.NationMapDarkServants) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.DarkServants);
        } else if (map == MapOptionValuesEnum.NationMapNotDarkServants) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.FreePeople) ||
            			visibleToAllegiance(hex, game, NationAllegianceEnum.Neutral);
        } else if (map == MapOptionValuesEnum.NationMapFreePeople) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.FreePeople);
        } else if (map == MapOptionValuesEnum.NationMapNotFreePeople) {
        	visible = visibleToAllegiance(hex, game, NationAllegianceEnum.DarkServants) ||
						visibleToAllegiance(hex, game, NationAllegianceEnum.Neutral);
        } else if (map == MapOptionValuesEnum.NationMapNeutrals) {
            visible = visibleToAllegiance(hex, game, NationAllegianceEnum.Neutral);
        } else if (map == MapOptionValuesEnum.NationMapNotNeutrals) {
        	visible = visibleToAllegiance(hex, game, NationAllegianceEnum.DarkServants) ||
						visibleToAllegiance(hex, game, NationAllegianceEnum.FreePeople);
        } else if (map == MapOptionValuesEnum.NationMapNone) {
        	visible = true;
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
                int radius = this.metadata.getGridCellWidth() * 2;
                int cx = x + this.metadata.getGridCellWidth() * this.metadata.getHexSize() / 2;
                int cy = y + this.metadata.getGridCellHeight() * this.metadata.getHexSize() / 2;
                Ellipse2D.Float el = new Ellipse2D.Float(cx - radius / 2, cy - radius / 2, radius, radius);
                g.setColor(transClimateColor);
                g.fill(el);
                repaintNumber = true;
            }
        }
        if (!visible) {
        	if (simpleColors) {
        		Font f = new Font(this.fontName, this.fontStyle, this.fontSize);
        		int w = ((Number)f.getStringBounds("0000", g.getFontRenderContext()).getWidth()).intValue();
        		x = this.metadata.getGridCellWidth() * this.metadata.getHexSize() + x - w / 2;
                y = this.metadata.getGridCellHeight() * this.metadata.getHexSize() / 4 + y + 8;

        		g.setFont(f);
        		if (hex.getTerrain() == HexTerrainEnum.mountains ||
        				hex.getTerrain() == HexTerrainEnum.forest ||
        				hex.getTerrain() == HexTerrainEnum.hillsNrough ||
        				hex.getTerrain() == HexTerrainEnum.swamp ||
        				hex.getTerrain() == HexTerrainEnum.sea ||
        				hex.getTerrain() == HexTerrainEnum.ocean) {
        			g.setColor(Color.decode("#DDDDDD"));
        		} else {
        			g.setColor(Color.gray);
        		}
                g.drawString("x", x, y);
        	} else {
        		Image img1 = getImage();
            	g.drawImage(img1, x, y, null);
            	repaintNumber = true;
        	}
        }
        
        if (repaintNumber) {
            if (getHexNumberRenderer() != null) {
                getHexNumberRenderer().render(hex, g, x, y);
            }
        }
    }

    public Renderer getHexNumberRenderer() {
        return this.hexNumberRenderer;
    }

    public void setHexNumberRenderer(Renderer hexNumberRendererId) {
        this.hexNumberRenderer = hexNumberRendererId;
    }
}
