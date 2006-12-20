package org.joverseer.ui.map;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.joverseer.metadata.domain.HexTerrainEnum;

import java.util.Hashtable;
import java.util.Locale;
import java.awt.*;


public class ColorPicker {
    Hashtable color1 = new Hashtable();
    Hashtable color2 = new Hashtable();
    Hashtable colors = new Hashtable();

    MessageSource colorSource = (MessageSource) Application.instance().getApplicationContext().getBean("colorSource");

    private static ColorPicker instance = new ColorPicker();

    private ColorPicker() {
    }

    public static ColorPicker getInstance() {
        return instance;
    }

    public Color getColor1(int nationNo) {
        if (!color1.containsKey(nationNo)) {
            String colorStr = colorSource.getMessage("nation." + nationNo + ".color", null, Locale.getDefault());
            color1.put(nationNo, Color.decode(colorStr));
        }
        return (Color)color1.get(nationNo);
    }

    public Color getColor2(int nationNo) {
        if (!color2.containsKey(nationNo)) {
            String colorStr = colorSource.getMessage("nation." + nationNo + ".color2", null, Locale.getDefault());
            color2.put(nationNo, Color.decode(colorStr));
        }
        return (Color)color2.get(nationNo);
    }

    public Color getColor(String color) {
        if (!colors.containsKey(color)) {
            String colorStr = colorSource.getMessage(color + ".color", null, Locale.getDefault());
            colors.put(color, Color.decode(colorStr));
        }
        return (Color)colors.get(color);
    }
}
