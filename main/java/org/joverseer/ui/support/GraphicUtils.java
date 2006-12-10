package org.joverseer.ui.support;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 10 Δεκ 2006
 * Time: 9:02:03 μμ
 * To change this template use File | Settings | File Templates.
 */
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
