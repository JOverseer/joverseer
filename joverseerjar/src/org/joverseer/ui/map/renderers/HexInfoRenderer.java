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
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.drawing.ColorPicker;

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
    int densityFactor = 4;
    Renderer hexNumberRenderer = null;
    BufferedImage img = null;
    String fontName = "Microsoft Sans Serif";
    int fontSize = 8;
    int fontStyle = Font.ITALIC;
    
    @Override
	public void refreshConfig() {
        super.refreshConfig();
        // the map options have already been refreshed.
        this.img = null;
        this.gh = GameHolder.instance();
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
            this.img = new BufferedImage(this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize(), this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize(), Transparency.TRANSLUCENT);
            Polygon polygon1 = new Polygon(this.xPoints, this.yPoints, 6);
            Graphics2D g = this.img.createGraphics();

            int w = this.mapMetadata.getHexSize() * this.mapMetadata.getGridCellWidth();
            int h = this.mapMetadata.getHexSize() * this.mapMetadata.getGridCellHeight();
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
            if (nmr.containsHex(hex)) {
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

        Hex hex = (Hex)obj;
        if (!this.mapMetadata.withinMapRange(hex)) return;
        Game game = this.gh.getGame();
        
        String pval2 = PreferenceRegistry.instance().getPreferenceValue("map.showClimate");
        Object map = this.mapOptions.get(MapOptionsEnum.NationMap);
        boolean showClimate = (pval2.equals("no") ? false : true);
        boolean visible = false;
        if (map == null) {
            HexInfo hexInfo = game.getTurn().getHexInfo(hex.getHexNo());
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
        	int ind = ((String)map).indexOf("-") + 1;
        	String pre = ((String)map).substring(0, ind);
        	String num = ((String)map).substring(ind);
        	if(pre.equals(Messages.getString("MapOptionsView.RegionOption"))) {
	            int number = Integer.parseInt(num);
	            NationMapRange nmr = (NationMapRange)game.getMetadata().getNationMapRanges().findFirstByProperty("nationNo", number);
	        	visible = nmr.containsHex(hex);
        	}
        	if(pre.equals(Messages.getString("MapOptionsView.NationOption"))) {
	            int number = Integer.parseInt(num);
	            HexInfo hexInfo = game.getTurn().getHexInfo(hex.getHexNo());
	        	visible = hexInfo.getVisible() && hexInfo.getNationSources().contains(Integer.valueOf(number));
        	}
        }
        
        boolean repaintNumber = false;
        if (showClimate) {
            HexInfo hexInfo = (HexInfo)game.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hex.getHexNo());
            if (hexInfo.getClimate() != null) {
                Color climateColor = ColorPicker.getInstance().getColor("climate." + hexInfo.getClimate().toString());
                Color transClimateColor = new Color(climateColor.getRed(), climateColor.getBlue(), climateColor.getGreen(), 100);
                int radius = this.mapMetadata.getGridCellWidth() * 2;
                int cx = x + this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2;
                int cy = y + this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() / 2;
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
        		x = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() + x - w / 2;
                y = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() / 4 + y + 8;

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
