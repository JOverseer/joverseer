package org.joverseer.ui.support;

import java.awt.*;

import javax.swing.JPanel;


public class GraphicUtils {
    public static Font getFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    public static Stroke getBasicStroke(int width) {
        return new BasicStroke(width);
    }

    public static Stroke getDashStroke(int width, int dashSize) {
        return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{dashSize, dashSize}, 2);
    }

}
