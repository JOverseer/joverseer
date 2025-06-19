package org.joverseer.ui.map.renderers;

import org.joverseer.metadata.domain.Hex;
import java.awt.*;

/**
 * Renders the hex number
 *  
 * @author Marios Skounakis
 */
public class HexNumberRenderer extends AbstractBaseRenderer {
	// these are normally injected.
    String fontName = "SansSerif";
    int fontSize = 8;
    int fontStyle = Font.PLAIN;

    public HexNumberRenderer() {
    }

    @Override
	public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }
    
    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }

        Hex hex = (Hex)obj;
        if (!this.mapMetadata.withinMapRange(hex)) return;
        //set font size based on cell width:
//        switch (this.mapMetadata.getGridCellWidth()) {
//        case 6:
//        	this.fontSize = 4;
//        	break;
//        case 7:
//        	this.fontSize = 5;
//        	break;
//        case 9:
//        	this.fontSize = 6;
//        	break;
//        case 11:
//        	this.fontSize = 7;
//        	break;
//        case 13:
//        	this.fontSize = 8;
//        	break;
//        case 15:
//        	this.fontSize = 9;
//        	break;
//        case 17:
//        	this.fontSize = 10;
//        	break;
//        case 19:
//        	this.fontSize = 11;
//        	break;
//        case 21:
//        	this.fontSize = 12;
//        	break;
//        case 23:
//        	this.fontSize = 13;
//        	break;
//        }
        this.fontSize = Math.round(this.mapMetadata.getGridCellWidth() / 2f) + 1;
        //Font f = new Font(this.fontName, this.fontStyle, this.mapMetadata.getGridCellWidth() < 10 ? 7 : this.fontSize);
        
        Font f = new Font(this.fontName, this.fontStyle, this.fontSize);
        String hexNo = hex.getHexNoStr();
        
        int w = ((Number)f.getStringBounds(hexNo, g.getFontRenderContext()).getWidth()).intValue();

        
        x = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 - w / 2 + x;
        y = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() / 4 + y;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(f);
        g.setColor(Color.black);
        g.drawString(hexNo, x, y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
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

	@Override
	public void refreshConfig() {
		//nothing to do.
		
	}
}
