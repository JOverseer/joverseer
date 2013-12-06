package org.joverseer.ui.map.renderers;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.ui.map.MapMetadata;
import org.springframework.richclient.application.Application;

import java.awt.*;

/**
 * Renders the hex number
 *  
 * @author Marios Skounakis
 */
public class HexNumberRenderer implements Renderer{
    String fontName = "Microsoft Sans Serif";
    int fontSize = 8;
    int fontStyle = Font.PLAIN;
    MapMetadata mapMetadata;

    public HexNumberRenderer() {
    }

    private void init() {
        this.mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    @Override
	public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }
    
    protected boolean withinMapRange(int x, int y, MapMetadata metadata) {
        if (x < metadata.getMinMapColumn()) return false;
        if (x > metadata.getMaxMapColumn()) return false;
        if (y < metadata.getMinMapRow()) return false;
        if (y > metadata.getMaxMapRow()) return false;
        return true;
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }
        if (this.mapMetadata == null) {
            init();
        }

        Hex hex = (Hex)obj;
        if (!withinMapRange(hex.getColumn(), hex.getRow(), this.mapMetadata)) return;

        Font f = new Font(this.fontName, this.fontStyle, this.mapMetadata.getGridCellWidth() < 10 ? 7 : this.fontSize);
        String hexNo = String.valueOf(hex.getColumn());
        if (hex.getColumn() < 10) {
            hexNo = "0" + hexNo;
        }
        if (hex.getRow() < 10) {
            hexNo = hexNo + "0";
        }
        hexNo += String.valueOf(hex.getRow());

        int w = ((Number)f.getStringBounds(hexNo, g.getFontRenderContext()).getWidth()).intValue();

        
        x = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 - w / 2 + x;
        y = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() / 4 + y;

        g.setFont(f);
        g.setColor(Color.black);
        g.drawString(hexNo, x, y);
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
}
