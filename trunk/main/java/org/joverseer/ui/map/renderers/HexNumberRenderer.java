package org.joverseer.ui.map.renderers;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.ui.map.MapMetadata;
import org.springframework.richclient.application.Application;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 8:03:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class HexNumberRenderer implements Renderer{
    String fontName = "Helvetica";
    int fontSize = 8;
    int fontStyle = Font.PLAIN;
    MapMetadata mapMetadata;

    public HexNumberRenderer() {
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public boolean appliesTo(Object obj) {
        return Hex.class.isInstance(obj);
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (!appliesTo(obj)) {
            throw new IllegalArgumentException(obj.toString());
        }
        if (mapMetadata == null) {
            init();
        }

        Hex hex = (Hex)obj;
        Font f = new Font(fontName, fontStyle, fontSize);
        String hexNo = String.valueOf(hex.getColumn());
        if (hex.getColumn() < 10) {
            hexNo = "0" + hexNo;
        }
        if (hex.getRow() < 10) {
            hexNo = hexNo + "0";
        }
        hexNo += String.valueOf(hex.getRow());

        int w = ((Number)f.getStringBounds(hexNo, g.getFontRenderContext()).getWidth()).intValue();

        x = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w / 2 + x;
        y = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() / 4 + y;

        g.setFont(f);
        g.setColor(Color.black);
        g.drawString(hexNo, x, y);
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
}